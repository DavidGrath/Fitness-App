package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.GymWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.data.entities.YogaWorkout
import com.davidgrath.fitnessapp.framework.database.YogaAsanaDao
import com.davidgrath.fitnessapp.framework.database.YogaWorkoutDao
import io.reactivex.rxjava3.core.Single
import java.util.Date
import java.util.TimeZone

interface YogaRepository {
    fun addWorkout(name: String): Single<Int>
    fun addAsana(workoutId: Int, setIdentifier: String, timeTaken: Long): Single<Int>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<YogaWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class YogaRepositoryImpl(
    private val yogaWorkoutDao: YogaWorkoutDao,
    private val yogaAsanaDao: YogaAsanaDao
) : YogaRepository {

    override fun addWorkout(name: String): Single<Int> {
        val timestamp = Date().time
        val timeZone = TimeZone.getDefault()
        return yogaWorkoutDao.insertWorkout(timestamp, timeZone.id, name)
    }

    override fun addAsana(workoutId: Int, setIdentifier: String, timeTaken: Long): Single<Int> {
        val timestamp = Date().time
        return yogaAsanaDao.insertAsana(workoutId, setIdentifier, timestamp, timeTaken)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<YogaWorkout>> {
        return yogaWorkoutDao.getWorkoutsByDateRangeSingle(startDate, endDate)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
        return yogaWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startDate, endDate)
    }
}