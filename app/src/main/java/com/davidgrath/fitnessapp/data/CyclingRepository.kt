package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.CyclingLocationData
import com.davidgrath.fitnessapp.data.entities.CyclingWorkout
import com.davidgrath.fitnessapp.framework.database.CyclingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.CyclingWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface CyclingRepository {
    fun getWorkout(workoutId: Int): Observable<CyclingWorkout>
    fun getAllWorkoutsSingle() : Single<List<CyclingWorkout>>
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<CyclingLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<CyclingLocationData>>
}

class CyclingRepositoryImpl(
    private val cyclingWorkoutDao: CyclingWorkoutDao,
    private val cyclingLocationDataDao: CyclingLocationDataDao
) : CyclingRepository {

    override fun getWorkout(workoutId: Int): Observable<CyclingWorkout> {
        return cyclingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<CyclingWorkout>> {
        return cyclingWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Int): Observable<List<CyclingLocationData>> {
        return cyclingLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Int): Single<List<CyclingLocationData>> {
        return cyclingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }
}

