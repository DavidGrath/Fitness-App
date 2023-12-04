package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.dao.GymSetDao
import com.davidgrath.fitnessapp.framework.database.dao.GymWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.GymSet
import com.davidgrath.fitnessapp.framework.database.entities.GymWorkout
import com.davidgrath.fitnessapp.util.dateAsStart
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date
import java.util.TimeZone

interface GymRepository {
    fun addWorkout(name: String): Single<Long>
//    fun addRoutine(workoutId: Int, routineName: String): Single<Int>
    fun addSet(workoutId: Long, setIdentifier: String, repCount: Int, timed: Boolean, timeTaken: Long): Single<Long>
    fun getWorkoutsByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<List<GymWorkout>>
    fun getWorkoutsSummaryByDateRange(startDate: Date? = null, endDate: Date? = null): Observable<WorkoutSummary>
}

class GymRepositoryImpl(
    private val gymWorkoutDao: GymWorkoutDao,
//    private val gymRoutineDao: GymRoutineDao,
    private val gymSetDao: GymSetDao
): GymRepository {

    override fun addWorkout(name: String): Single<Long> {
        val timestamp = Date().time
        val timeZone = TimeZone.getDefault()
        return gymWorkoutDao.insertWorkout(GymWorkout(null, timestamp, timeZone.id, name))
    }

    /*override fun addRoutine(workoutId: Int, routineName: String): Single<Int> {
        val timestamp = Date().time
        return gymRoutineDao.insertRoutine(workoutId, timestamp, routineName)
    }*/

    override fun addSet(workoutId: Long, setIdentifier: String, repCount: Int, timed: Boolean,
        timeTaken: Long): Single<Long> {
        val timestamp = Date().time
        val gymSet = GymSet(null, workoutId, setIdentifier, timestamp, repCount, timeTaken)
        return gymSetDao.insertSet(gymSet)
    }

    override fun getWorkoutsByDateRange(startDate: Date?, endDate: Date?): Observable<List<GymWorkout>> {
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
        return gymWorkoutDao.getWorkoutsByDateRange(startTime, endTime)
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
        return gymWorkoutDao.getWorkoutsSummaryByDateRange(startTime, endTime)
    }
}

