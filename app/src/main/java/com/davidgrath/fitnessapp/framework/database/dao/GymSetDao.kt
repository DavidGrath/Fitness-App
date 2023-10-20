package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.framework.database.entities.GymSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class GymSetDao {

    //CREATE
    @Insert
    abstract fun insertSet(gymSet: GymSet) : Single<Long>

    //READ
    @Query("SELECT * FROM GymSet WHERE workoutId = :workoutId")
    abstract fun getAllSetsByWorkoutId(workoutId: Long) : Observable<List<GymSet>>
    @Query("SELECT * FROM GymSet WHERE workoutId = :workoutId")
    abstract fun getAllSetsByWorkoutIdSingle(workoutId: Long) : Single<List<GymSet>>

    //UPDATE

    //DELETE
}

/*
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
}*/
