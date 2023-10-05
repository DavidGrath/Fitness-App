package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.RunningLocationData
import com.davidgrath.fitnessapp.data.entities.RunningWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.RunningLocationDataDao
import com.davidgrath.fitnessapp.framework.database.RunningWorkoutDao
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface RunningRepository {
    fun getWorkout(workoutId: Int): Observable<RunningWorkout>
    fun getAllWorkoutsSingle() : Single<List<RunningWorkout>>
    fun getWorkoutLocationData(workoutId: Int) : Observable<List<RunningLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Int) : Single<List<RunningLocationData>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<RunningWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
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

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<RunningWorkout>> {
        return runningWorkoutDao.getWorkoutsByDateRangeSingle(startDate, endDate)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
        return runningWorkoutDao.getWorkoutsSummaryRangeSingle(startDate, endDate)
    }
}
