package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.YogaAsanaDao
import com.davidgrath.fitnessapp.framework.database.dao.YogaWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.YogaAsana
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Single
import java.util.Date
import java.util.TimeZone

interface YogaRepository {
    fun addWorkout(name: String): Single<Long>
    fun addAsana(workoutId: Long, asanaIdentifier: String, timeTaken: Long): Single<Long>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<YogaWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class YogaRepositoryImpl(
    private val yogaWorkoutDao: YogaWorkoutDao,
    private val yogaAsanaDao: YogaAsanaDao
) : YogaRepository {

    override fun addWorkout(name: String): Single<Long> {
        val timestamp = Date().time
        val timeZone = TimeZone.getDefault()
        val yogaWorkout = YogaWorkout(null, timestamp, timeZone.id, name)
        return yogaWorkoutDao.insertWorkout(yogaWorkout)
    }

    override fun addAsana(workoutId: Long, asanaIdentifier: String, timeTaken: Long): Single<Long> {
        val timestamp = Date().time
        val yogaAsana = YogaAsana(null, workoutId, asanaIdentifier, timestamp, timeTaken)
        return yogaAsanaDao.insertAsana(yogaAsana)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<YogaWorkout>> {
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
        return yogaWorkoutDao.getWorkoutsByDateRangeSingle(startTime, endTime)
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
        return yogaWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startTime, endTime)
    }
}