package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WalkingLocationData
import com.davidgrath.fitnessapp.data.entities.WalkingWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.WalkingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.WalkingWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface WalkingRepository {
    fun getWorkout(workoutId: Int): Observable<WalkingWorkout>
    fun getAllWorkoutsSingle() : Single<List<WalkingWorkout>>
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<WalkingLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<WalkingLocationData>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<WalkingWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class WalkingRepositoryImpl(
    private val walkingWorkoutDao: WalkingWorkoutDao,
    private val walkingLocationDataDao: WalkingLocationDataDao
) : WalkingRepository {

    override fun getWorkout(workoutId: Int): Observable<WalkingWorkout> {
        return walkingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<WalkingWorkout>> {
        return walkingWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Int): Observable<List<WalkingLocationData>> {
        return walkingLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Int): Single<List<WalkingLocationData>> {
        return walkingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<WalkingWorkout>> {
        return walkingWorkoutDao.getWorkoutsByDateRangeSingle(startDate, endDate)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
        return walkingWorkoutDao.getWorkoutsSummaryRangeSingle(startDate, endDate)
    }
}