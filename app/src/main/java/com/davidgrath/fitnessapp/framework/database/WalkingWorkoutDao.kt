package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.WalkingWorkout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class WalkingWorkoutDao {
    private val workoutList = ArrayList<WalkingWorkout>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<WalkingWorkout>>()

    //CREATE
    fun insertWorkout(date: Long, timeZoneId: String) : Single<Int> {
        workoutList.add(
            WalkingWorkout(incrementId, date, timeZoneId, 0L, 0)
        )
        behaviorSubject.onNext(workoutList)
        return Single.just(incrementId++)
    }
    //READ
    fun getAllWorkoutsSingle() : Single<List<WalkingWorkout>> {
        return Single.just(workoutList)
    }
    fun getWorkout(workoutId: Int) : Observable<WalkingWorkout> {
        return behaviorSubject.flatMap {
            val workout = it.find { it.id == workoutId }
            if(workout == null) {
                Observable.empty<WalkingWorkout>()
            } else {
                Observable.just(workout)
            }
        }
    }

    fun getWorkoutSingle(workoutId: Int): Single<WalkingWorkout> {
        val workout = workoutList.find { it.id == workoutId }
        return if(workout == null) {
            Single.error(Exception())
        } else {
            Single.just(workout)
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
