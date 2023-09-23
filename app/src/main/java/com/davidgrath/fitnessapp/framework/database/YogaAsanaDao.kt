package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.YogaAsana
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class YogaAsanaDao {

    private val workoutAsanaList = ArrayList<YogaAsana>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<YogaAsana>>()

    //CREATE
    fun insertAsana(workoutId: Int, setIdentifier: String, timestamp: Long, timeTaken: Long) : Single<Int> {
        workoutAsanaList.add(
            YogaAsana(incrementId, workoutId, setIdentifier, timestamp, timeTaken)
        )
        behaviorSubject.onNext(workoutAsanaList)
        return Single.just(incrementId++)
    }

    //READ
    fun getAllAsanasByWorkoutId(workoutId: Int) : Observable<List<YogaAsana>> {
        return behaviorSubject.map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    fun getAllAsanasByWorkoutIdSingle(workoutId: Int) : Single<List<YogaAsana>> {
        return Single.just(workoutAsanaList).map {
            val filtered = it.filter { it.workoutId == workoutId }
            filtered
        }
    }

    //UPDATE

    //DELETE
}