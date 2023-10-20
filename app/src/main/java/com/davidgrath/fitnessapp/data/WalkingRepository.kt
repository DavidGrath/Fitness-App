package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.WalkingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.WalkingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.WalkingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface WalkingRepository {
    fun getWorkout(workoutId: Long): Observable<WalkingWorkout>
    fun getAllWorkoutsSingle() : Single<List<WalkingWorkout>>
    fun getWorkoutLocationData(workoutId: Long) : Observable<List<WalkingLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Long) : Single<List<WalkingLocationData>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<WalkingWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class WalkingRepositoryImpl(
    private val walkingWorkoutDao: WalkingWorkoutDao,
    private val walkingLocationDataDao: WalkingLocationDataDao
) : WalkingRepository {

    override fun getWorkout(workoutId: Long): Observable<WalkingWorkout> {
        return walkingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<WalkingWorkout>> {
        return walkingWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Long): Observable<List<WalkingLocationData>> {
        return walkingLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Long): Single<List<WalkingLocationData>> {
        return walkingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<WalkingWorkout>> {
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
        return walkingWorkoutDao.getWorkoutsByDateRangeSingle(startTime, endTime)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
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
        return walkingWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startTime, endTime)
    }
}