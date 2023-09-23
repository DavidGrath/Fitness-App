package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.GymSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class GymSetDao {
    private val workoutSetList = ArrayList<GymSet>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<GymSet>>()

    //CREATE
    fun insertSet(workoutId: Int, setIdentifier: String, timestamp: Long, repCount: Int,
                   timed: Boolean, timeTaken: Long) : Single<Int> {
        workoutSetList.add(
            GymSet(incrementId, workoutId, setIdentifier, timestamp, repCount, timed, timeTaken)
        )
        behaviorSubject.onNext(workoutSetList)
        return Single.just(incrementId++)
    }
    //READ

    fun getAllSetsByWorkoutId(workoutId: Int) : Observable<List<GymSet>> {
        return behaviorSubject.map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    fun getAllSetsByWorkoutIdSingle(workoutId: Int) : Single<List<GymSet>> {
        return Single.just(workoutSetList).map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    //UPDATE

    //DELETE
}