package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.GymWorkout
import com.davidgrath.fitnessapp.util.dateAsEnd
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Date

class GymWorkoutDao {
    private val workoutList = ArrayList<GymWorkout>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<GymWorkout>>()

    //CREATE
    fun insertWorkout(date: Long, timeZoneId: String, name: String) : Single<Int> {
        workoutList.add(
            GymWorkout(incrementId, date, timeZoneId, name, 0L, 0)
        )
        behaviorSubject.onNext(workoutList)
        return Single.just(incrementId++)
    }
    //READ
    fun getAllWorkoutsSingle() : Single<List<GymWorkout>> {
        return Single.just(workoutList)
    }

    fun getWorkout(workoutId: Int) : Observable<GymWorkout> {
        return behaviorSubject.flatMap {
            val workout = it.find { it.id == workoutId }
            if(workout == null) {
                Observable.empty<GymWorkout>()
            } else {
                Observable.just(workout)
            }
        }
    }

    fun getWorkoutSingle(workoutId: Int): Single<GymWorkout> {
        val workout = workoutList.find { it.id == workoutId }
        return if(workout == null) {
            Single.error(Exception())
        } else {
            Single.just(workout)
        }
    }

    fun getAllWorkoutsByDateRangeSingle(startDate: Date?, endDate: Date?): Single<List<GymWorkout>> {
        return Single.just(workoutList).map {
            val filtered = it.filter {
                if(startDate != null) {
                    if(endDate != null) {
                        dateAsStart(startDate).time <= it.date && it.date <= dateAsEnd(endDate).time
                    } else {
                        dateAsStart(startDate).time <= it.date
                    }
                } else {
                    if(endDate != null) {
                        it.date <= dateAsEnd(endDate).time
                    } else {
                        true
                    }
                }
            }
            filtered
        }
    }
    //UPDATE
    fun setWorkoutDurationAndKCalBurned(workoutId: Int, duration: Long, kCalBurned: Int) : Single<Int> {
        val workoutIndex = workoutList.indexOfFirst { it.id == workoutId }
        if(workoutIndex == -1) {
            return Single.just(0)
        } else {
            val updated = workoutList[workoutIndex].copy(duration = duration, kCalBurned = kCalBurned)
            workoutList[workoutIndex] = updated
            behaviorSubject.onNext(workoutList)
            return Single.just(1)
        }
    }
    //DELETE
}