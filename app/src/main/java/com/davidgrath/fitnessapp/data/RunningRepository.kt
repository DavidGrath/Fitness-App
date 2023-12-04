package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.RunningLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.RunningWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.RunningLocationData
import com.davidgrath.fitnessapp.framework.database.entities.RunningWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface RunningRepository {
    fun getWorkout(workoutId: Long): Observable<RunningWorkout>
    fun getAllWorkoutsSingle() : Single<List<RunningWorkout>>
    fun getWorkoutLocationData(workoutId: Long) : Observable<List<RunningLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Long) : Single<List<RunningLocationData>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<List<RunningWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<WorkoutSummary>
}

class RunningRepositoryImpl(
    private val runningWorkoutDao: RunningWorkoutDao,
    private val runningLocationDataDao: RunningLocationDataDao
) : RunningRepository {

    override fun getWorkout(workoutId: Long): Observable<RunningWorkout> {
        return runningWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<RunningWorkout>> {
        return runningWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Long): Observable<List<RunningLocationData>> {
        return runningLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Long): Single<List<RunningLocationData>> {
        return runningLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Observable<List<RunningWorkout>> {
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
        return runningWorkoutDao.getWorkoutsByDateRange(startTime, endTime)
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
        return runningWorkoutDao.getWorkoutsSummaryByDateRange(startTime, endTime)
    }
}
