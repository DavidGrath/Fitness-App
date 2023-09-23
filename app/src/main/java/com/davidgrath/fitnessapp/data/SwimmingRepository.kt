package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.framework.database.SwimmingWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface SwimmingRepository {
    fun getWorkout(workoutId: Int): Observable<SwimmingWorkout>
    fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>>
}

class SwimmingRepositoryImpl(
    private val swimmingWorkoutDao: SwimmingWorkoutDao
) : SwimmingRepository {

    override fun getWorkout(workoutId: Int): Observable<SwimmingWorkout> {
        return swimmingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<SwimmingWorkout>> {
        return swimmingWorkoutDao.getAllWorkoutsSingle()
    }
}
