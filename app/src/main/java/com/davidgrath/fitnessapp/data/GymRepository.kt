package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.GymWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.GymRoutineDao
import com.davidgrath.fitnessapp.framework.database.GymSetDao
import com.davidgrath.fitnessapp.framework.database.GymWorkoutDao
import io.reactivex.rxjava3.core.Single
import java.util.Date
import java.util.TimeZone

interface GymRepository {
    fun addWorkout(name: String): Single<Int>
    fun addRoutine(workoutId: Int, routineName: String): Single<Int>
    fun addSet(routineId: Int, setIdentifier: String, repCount: Int, timed: Boolean, timeTaken: Long): Single<Int>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Single<List<GymWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary>
}

class GymRepositoryImpl(
    private val gymWorkoutDao: GymWorkoutDao,
    private val gymRoutineDao: GymRoutineDao,
    private val gymSetDao: GymSetDao
): GymRepository {

    override fun addWorkout(name: String): Single<Int> {
        val timestamp = Date().time
        val timeZone = TimeZone.getDefault()
        return gymWorkoutDao.insertWorkout(timestamp, timeZone.id, name)
    }

    override fun addRoutine(workoutId: Int, routineName: String): Single<Int> {
        val timestamp = Date().time
        return gymRoutineDao.insertRoutine(workoutId, timestamp, routineName)
    }

    override fun addSet(routineId: Int, setIdentifier: String, repCount: Int, timed: Boolean,
        timeTaken: Long): Single<Int> {
        val timestamp = Date().time
        return gymSetDao.insertSet(routineId, setIdentifier, timestamp, repCount, timed, timeTaken)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Single<List<GymWorkout>> {
        return gymWorkoutDao.getWorkoutsByDateRangeSingle(startDate, endDate)
    }

    override fun getWorkoutsSummaryByDateRange(startDate: Date?, endDate: Date?): Single<WorkoutSummary> {
        return gymWorkoutDao.getWorkoutsSummaryByDateRangeSingle(startDate, endDate)
    }
}

