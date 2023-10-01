package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.util.dateAsEnd
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class SwimmingWorkoutDao {

    private val workoutList = ArrayList<SwimmingWorkout>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<SwimmingWorkout>>()

    init {
        val calendar = Calendar.getInstance()
        val monthPool = (1..28).toMutableList()
        for(i in 0..19) {
            val monthDayIndex = (0.until(monthPool.size)).random()
            calendar.set(Calendar.MONTH, (0..11).random())
            calendar.set(Calendar.DAY_OF_MONTH, monthPool.removeAt(monthDayIndex))
            val workout = SwimmingWorkout(incrementId++, calendar.timeInMillis,
                TimeZone.getDefault().id, 600_000L, 100)
            workoutList.add(workout)
        }
    }

    //CREATE
    fun insertWorkout(date: Long, timeZoneId: String) : Single<Int> {
        workoutList.add(
            SwimmingWorkout(incrementId, date, timeZoneId, 0L, 0)
        )
        behaviorSubject.onNext(workoutList)
        return Single.just(incrementId++)
    }
    //READ
    fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>> {
        return Single.just(workoutList)
    }
    fun getWorkout(workoutId: Int) : Observable<SwimmingWorkout> {
        return behaviorSubject.flatMap {
            val workout = it.find { it.id == workoutId }
            if(workout == null) {
                Observable.empty<SwimmingWorkout>()
            } else {
                Observable.just(workout)
            }
        }
    }

    fun getWorkoutSingle(workoutId: Int): Single<SwimmingWorkout> {
        val workout = workoutList.find { it.id == workoutId }
        return if(workout == null) {
            Single.error(Exception())
        } else {
            Single.just(workout)
        }
    }

    fun getWorkoutsByDateRangeSingle(startDate: Date? = null, endDate: Date? = null): Single<List<SwimmingWorkout>> {
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

    fun getWorkoutsSummaryRangeSingle(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary> {
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
            val summary = WorkoutSummary(
                filtered.sumOf { it.kCalBurned },
                filtered.size,
                (filtered.sumOf { it.duration } / 60_000).toInt()
            )
            summary

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