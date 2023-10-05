package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.CyclingLocationData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CyclingLocationDataDao {

    private val cyclingLocationData = ArrayList<CyclingLocationData>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<CyclingLocationData>>()

    //CREATE
    fun insertWorkoutLocationData(workoutId: Int, latitude: Double, longitude: Double, timestamp: Long,
                                  accuracy: Float?, altitude: Double?, bearing: Float?, speed: Float?) : Single<Int> {
        cyclingLocationData.add(
            CyclingLocationData(incrementId, workoutId, latitude, longitude,
                timestamp, accuracy, altitude, bearing, speed)
        )
        behaviorSubject.onNext(cyclingLocationData)
        return Single.just(incrementId++)
    }
    //READ
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<CyclingLocationData>> {
        return behaviorSubject.map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }

    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<CyclingLocationData>> {
        return Single.just(cyclingLocationData).map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }
    //UPDATE
    //DELETE
}