package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.CyclingLocationData
import com.davidgrath.fitnessapp.data.entities.CyclingWorkout
import com.davidgrath.fitnessapp.data.entities.RunningLocationData
import com.davidgrath.fitnessapp.data.entities.RunningWorkout
import com.davidgrath.fitnessapp.framework.database.CyclingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.CyclingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.RunningLocationDataDao
import com.davidgrath.fitnessapp.framework.database.RunningWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface RunningRepository {
    fun getWorkout(workoutId: Int): Observable<RunningWorkout>
    fun getAllWorkoutsSingle() : Single<List<RunningWorkout>>
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<RunningLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<RunningLocationData>>
}

class RunningRepositoryImpl(
    private val runningWorkoutDao: RunningWorkoutDao,
    private val runningLocationDataDao: RunningLocationDataDao
) : RunningRepository {

    override fun getWorkout(workoutId: Int): Observable<RunningWorkout> {
        return runningWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<RunningWorkout>> {
        return runningWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Int): Observable<List<RunningLocationData>> {
        return runningLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Int): Single<List<RunningLocationData>> {
        return runningLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }
}
