package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.framework.database.entities.YogaAsana
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class YogaAsanaDao {

    //CREATE
    @Insert
    abstract fun insertAsana(yogaAsana: YogaAsana) : Single<Long>

    //READ
    @Query("SELECT * FROM YogaAsana WHERE workoutId = :workoutId")
    abstract fun getAllAsanasByWorkoutId(workoutId: Long) : Observable<List<YogaAsana>>
    @Query("SELECT * FROM YogaAsana WHERE workoutId = :workoutId")
    abstract fun getAllAsanasByWorkoutIdSingle(workoutId: Long) : Single<List<YogaAsana>>

    //UPDATE

    //DELETE
}

/*
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
*/
