package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.framework.database.entities.RunningLocationData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class RunningLocationDataDao {

    //CREATE
    @Insert
    abstract fun insertWorkoutLocationData(runningLocationData: RunningLocationData) : Single<Long>

    //READ
    @Query("SELECT * FROM RunningLocationData WHERE workoutId = :workoutId")
    abstract fun getWorkoutLocationData(workoutId: Long) : Observable<List<RunningLocationData>>
    @Query("SELECT * FROM RunningLocationData WHERE workoutId = :workoutId")
    abstract fun getWorkoutLocationDataSingle(workoutId: Long) : Single<List<RunningLocationData>>

    //UPDATE
    //DELETE
}

/*
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
}*/
