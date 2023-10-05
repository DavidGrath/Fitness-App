package com.davidgrath.fitnessapp.framework.database

import com.davidgrath.fitnessapp.data.entities.RunningLocationData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class RunningLocationDataDao {

    private val workoutLocationData = ArrayList<RunningLocationData>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<RunningLocationData>>()

    //CREATE
    fun insertWorkoutLocationData(workoutId: Int, latitude: Double, longitude: Double, timestamp: Long,
                                  accuracy: Float?, altitude: Double?, bearing: Float?, speed: Float?) : Single<Int> {
        workoutLocationData.add(
            RunningLocationData(incrementId, workoutId, latitude, longitude, timestamp, accuracy, altitude, bearing, speed)
        )
        behaviorSubject.onNext(workoutLocationData)
        return Single.just(incrementId++)
    }
    //READ
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<RunningLocationData>> {
        return behaviorSubject.map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }

    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<RunningLocationData>> {
        return Single.just(workoutLocationData).map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }
    //UPDATE
    //DELETE
}

