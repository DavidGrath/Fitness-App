package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.SwimmingWorkout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class SwimmingWorkoutDao {

    //CREATE
    @Insert
    abstract fun insertWorkout(swimmingWorkout: SwimmingWorkout) : Single<Long>
    //region READ

    @Query("SELECT * FROM SwimmingWorkout")
    abstract fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>>
    @Query("SELECT * FROM SwimmingWorkout WHERE id = :workoutId")
    abstract fun getWorkout(workoutId: Long) : Observable<SwimmingWorkout>
    @Query("SELECT * FROM SwimmingWorkout WHERE id = :workoutId")
    abstract fun getWorkoutSingle(workoutId: Long): Single<SwimmingWorkout>
    @Query("SELECT * FROM SwimmingWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRange(startDate: Long = -1, endDate: Long = -1): Observable<List<SwimmingWorkout>>
    @Query("SELECT * FROM SwimmingWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRangeSingle(startDate: Long = -1, endDate: Long = -1): Single<List<SwimmingWorkout>>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM SwimmingWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRange(startDate: Long? = -1, endDate: Long = -1): Observable<WorkoutSummary>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM SwimmingWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRangeSingle(startDate: Long? = -1, endDate: Long = -1): Single<WorkoutSummary>
    //endregion

    //UPDATE
    @Query("UPDATE SwimmingWorkout SET duration = :duration, kCalBurned = :kCalBurned WHERE id = :workoutId")
    abstract fun setWorkoutDurationAndKCalBurned(workoutId: Long, duration: Long, kCalBurned: Int) : Single<Int>

    //DELETE
}

/*
class SwimmingWorkoutDao {

    private val workoutList = ArrayList<SwimmingWorkout>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<SwimmingWorkout>>()

    init {
        val calendar = Calendar.getInstance()
        val monthPool = (1..28).toMutableList()
        for(i in 0..19) {
            val monthDayIndex = (0.until(monthPool.size)).random()
            calendar.set(Calendar.MONTH, (0..11).random())
            calendar.set(Calendar.DAY_OF_MONTH, monthPool.removeAt(monthDayIndex))
            val workout = SwimmingWorkout(incrementId++, calendar.timeInMillis,
                TimeZone.getDefault().id, 600_000L, 100)
            workoutList.add(workout)
        }
    }

    //CREATE
    fun insertWorkout(date: Long, timeZoneId: String) : Single<Int> {
        workoutList.add(
            SwimmingWorkout(incrementId, date, timeZoneId, 0L, 0)
        )
        behaviorSubject.onNext(workoutList)
        return Single.just(incrementId++)
    }
    //READ
    fun getAllWorkoutsSingle() : Single<List<SwimmingWorkout>> {
        return Single.just(workoutList)
    }
    fun getWorkout(workoutId: Int) : Observable<SwimmingWorkout> {
        return behaviorSubject.flatMap {
            val workout = it.find { it.id == workoutId }
            if(workout == null) {
                Observable.empty<SwimmingWorkout>()
            } else {
                Observable.just(workout)
            }
        }
    }

    fun getWorkoutSingle(workoutId: Int): Single<SwimmingWorkout> {
        val workout = workoutList.find { it.id == workoutId }
        return if(workout == null) {
            Single.error(Exception())
        } else {
            Single.just(workout)
        }
    }

    fun getWorkoutsByDateRangeSingle(startDate: Date? = null, endDate: Date? = null): Single<List<SwimmingWorkout>> {
        return Single.just(workoutList).map {
            val filtered = it.filter {
                if(startDate != null) {
                    if(endDate != null) {
                        dateAsStart(startDate).time <= it.date && it.date <= dateAsEnd(endDate).time
                    } else {
                        dateAsStart(startDate).time <= it.date
                    }
                } else {
                    if(endDate != null) {
                        it.date <= dateAsEnd(endDate).time
                    } else {
                        true
                    }
                }
            }
            filtered
        }
    }

    fun getWorkoutsSummaryByDateRangeSingle(startDate: Date? = null, endDate: Date? = null): Single<WorkoutSummary> {
        return Single.just(workoutList).map {
            val filtered = it.filter {
                if(startDate != null) {
                    if(endDate != null) {
                        dateAsStart(startDate).time <= it.date && it.date <= dateAsEnd(endDate).time
                    } else {
                        dateAsStart(startDate).time <= it.date
                    }
                } else {
                    if(endDate != null) {
                        it.date <= dateAsEnd(endDate).time
                    } else {
                        true
                    }
                }
            }
            val summary = WorkoutSummary(
                filtered.sumOf { it.kCalBurned },
                filtered.size,
                (filtered.sumOf { it.duration } / 60_000).toInt()
            )
            summary

        }
    }

    //UPDATE
    fun setWorkoutDurationAndKCalBurned(workoutId: Int, duration: Long, kCalBurned: Int) : Single<Int> {
        val workoutIndex = workoutList.indexOfFirst { it.id == workoutId }
        if(workoutIndex == -1) {
            return Single.just(0)
        } else {
            val updated = workoutList[workoutIndex].copy(duration = duration, kCalBurned = kCalBurned)
            workoutList[workoutIndex] = updated
            behaviorSubject.onNext(workoutList)
            return Single.just(1)
        }
    }

    //DELETE
}*/