package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.SwimmingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface SwimmingRepository {
    fun getWorkout(workoutId: Long): Observable<SwimmingWorkout>
    fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<List<SwimmingWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<WorkoutSummary>
}

class SwimmingRepositoryImpl(
    private val swimmingWorkoutDao: SwimmingWorkoutDao
) : SwimmingRepository {

    override fun getWorkout(workoutId: Long): Observable<SwimmingWorkout> {
        return swimmingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<SwimmingWorkout>> {
        return swimmingWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Observable<List<SwimmingWorkout>> {
        val startTime = if(startDate != null) {
            dateAsStart(startDate).time
        } else {
            -1
        }
        val endTime = if(endDate != null) {
            dateAsStart(endDate).time
        } else {
            -1
        }
        return swimmingWorkoutDao.getWorkoutsByDateRange(startTime, endTime)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Observable<WorkoutSummary> {
        val startTime = if(startDate != null) {
            dateAsStart(startDate).time
        } else {
            -1
        }
        val endTime = if(endDate != null) {
            dateAsStart(endDate).time
        } else {
            -1
        }
        return swimmingWorkoutDao.getWorkoutsSummaryByDateRange(startTime, endTime)
    }
}
