package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.GymRoutine
import com.davidgrath.fitnessapp.util.dateAsEnd
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Date

class GymRoutineDao {
    private val routineList = ArrayList<GymRoutine>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<GymRoutine>>()

    //CREATE
    fun insertRoutine(workoutId: Int, timestamp: Long, routineName: String) : Single<Int> {
        routineList.add(
            GymRoutine(incrementId, workoutId, timestamp, routineName)
        )
        behaviorSubject.onNext(routineList)
        return Single.just(incrementId++)
    }
    //READ

    fun getRoutineById(routineId: Int) : Observable<List<GymRoutine>> {
        return behaviorSubject.map {
            val filtered = it.filter { it.id == routineId }
            filtered
        }
    }

    fun getAllRoutinesByWorkoutId(workoutId: Int) : Observable<List<GymRoutine>> {
        return behaviorSubject.map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    fun getAllRoutinesByWorkoutIdSingle(workoutId: Int) : Single<List<GymRoutine>> {
        return Single.just(routineList).map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    fun getAllRoutinesByDateRangeSingle(startDate: Date?, endDate: Date?): Single<List<GymRoutine>> {
        return Single.just(routineList).map {
            val filtered = it.filter {
                if(startDate != null) {
                    if(endDate != null) {
                        dateAsStart(startDate).time <= it.timestamp && it.timestamp <= dateAsEnd(endDate).time
                    } else {
                        dateAsStart(startDate).time <= it.timestamp
                    }
                } else {
                    if(endDate != null) {
                        it.timestamp <= dateAsEnd(endDate).time
                    } else {
                        true
                    }
                }
            }
            filtered
        }
    }

    //UPDATE

    //DELETE
}
