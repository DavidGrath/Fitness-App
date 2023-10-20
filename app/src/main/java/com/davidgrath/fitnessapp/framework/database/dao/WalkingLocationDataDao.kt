package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.framework.database.entities.WalkingLocationData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class WalkingLocationDataDao {

    //CREATE
    @Insert
    abstract fun insertWorkoutLocationData(walkingLocationData: WalkingLocationData) : Single<Long>
    //READ
    @Query("SELECT * FROM WalkingLocationData WHERE workoutId = :workoutId")
    abstract fun getWorkoutLocationData(workoutId: Long) : Observable<List<WalkingLocationData>>
    @Query("SELECT * FROM WalkingLocationData WHERE workoutId = :workoutId")
    abstract fun getWorkoutLocationDataSingle(workoutId: Long) : Single<List<WalkingLocationData>>
    //UPDATE
    //DELETE
}

/*
class WalkingLocationDataDao {
    private val workoutLocationData = ArrayList<WalkingLocationData>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<WalkingLocationData>>()

    //CREATE
    fun insertWorkoutLocationData(workoutId: Int, latitude: Double, longitude: Double, timestamp: Long,
                                  accuracy: Float?, altitude: Double?, bearing: Float?, speed: Float?) : Single<Int> {
        workoutLocationData.add(
            WalkingLocationData(incrementId, workoutId, latitude, longitude, timestamp, accuracy, altitude, bearing, speed)
        )
        behaviorSubject.onNext(workoutLocationData)
        return Single.just(incrementId++)
    }
    //READ
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<WalkingLocationData>> {
        return behaviorSubject.map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }

    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<WalkingLocationData>> {
        return Single.just(workoutLocationData).map { list ->
            list.filter { it.workoutId == workoutId }
        }
    }
    //UPDATE
    //DELETE
}*/
