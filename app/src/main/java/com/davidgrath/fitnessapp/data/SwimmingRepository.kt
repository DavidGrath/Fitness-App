package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.SwimmingWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface SwimmingRepository {
    fun getWorkout(workoutId: Int): Observable<SwimmingWorkout>
    fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<SwimmingWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
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

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<SwimmingWorkout>> {
        return swimmingWorkoutDao.getWorkoutsByDateRangeSingle(startDate, endDate)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
        return swimmingWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startDate, endDate)
    }
}
