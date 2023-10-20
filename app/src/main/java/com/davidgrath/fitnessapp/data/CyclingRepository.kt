
package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.CyclingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.CyclingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.CyclingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date

interface CyclingRepository {
    fun getWorkout(workoutId: Long): Observable<CyclingWorkout>
    fun getAllWorkoutsSingle() : Single<List<CyclingWorkout>>
    fun getWorkoutLocationData(workoutId: Long) : Observable<List<CyclingLocationData>>
    fun getWorkoutLocationDataSingle(workoutId: Long) : Single<List<CyclingLocationData>>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<CyclingWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class CyclingRepositoryImpl(
    private val cyclingWorkoutDao: CyclingWorkoutDao,
    private val cyclingLocationDataDao: CyclingLocationDataDao
) : CyclingRepository {

    override fun getWorkout(workoutId: Long): Observable<CyclingWorkout> {
        return cyclingWorkoutDao.getWorkout(workoutId)
    }

    override fun getAllWorkoutsSingle(): Single<List<CyclingWorkout>> {
        return cyclingWorkoutDao.getAllWorkoutsSingle()
    }

    override fun getWorkoutLocationData(workoutId: Long): Observable<List<CyclingLocationData>> {
        return cyclingLocationDataDao.getWorkoutLocationData(workoutId)
    }

    override fun getWorkoutLocationDataSingle(workoutId: Long): Single<List<CyclingLocationData>> {
        return cyclingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<CyclingWorkout>> {
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
        return cyclingWorkoutDao.getWorkoutsByDateRangeSingle(startTime, endTime)
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
        return cyclingWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startTime, endTime)
    }
}

