package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.GymWorkout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class GymWorkoutDao {

    //CREATE
    @Insert
    abstract fun insertWorkout(gymWorkout: GymWorkout) : Single<Long>

    //region READ
    @Query("SELECT * FROM GymWorkout")
    abstract fun getAllWorkoutsSingle() : Single<List<GymWorkout>>
    @Query("SELECT * FROM GymWorkout WHERE id = :workoutId")
    abstract fun getWorkout(workoutId: Long) : Observable<GymWorkout>
    @Query("SELECT * FROM GymWorkout WHERE id = :workoutId")
    abstract fun getWorkoutSingle(workoutId: Long): Single<GymWorkout>
    @Query("SELECT * FROM GymWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRange(startDate: Long = -1, endDate: Long = -1): Observable<List<GymWorkout>>

    @Query("SELECT * FROM GymWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRangeSingle(startDate: Long = -1, endDate: Long = -1): Single<List<GymWorkout>>
    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM GymWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRange(startDate: Long? = -1, endDate: Long = -1): Observable<WorkoutSummary>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM GymWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRangeSingle(startDate: Long? = -1, endDate: Long = -1): Single<WorkoutSummary>

    //endregion

    //UPDATE
    @Query("UPDATE GymWorkout SET duration = :duration, kCalBurned = :kCalBurned WHERE id = :workoutId")
    abstract fun setWorkoutDurationAndKCalBurned(workoutId: Long, duration: Long, kCalBurned: Int) : Single<Int>
    //DELETE
}

/*
class GymWorkoutDao {
    private val workoutList = ArrayList<GymWorkout>()
    private var incrementId = 1
    private val behaviorSubject = BehaviorSubject.create<List<GymWorkout>>()

    //CREATE
    fun insertWorkout(date: Long, timeZoneId: String, name: String) : Single<Int> {
        workoutList.add(
            GymWorkout(incrementId, date, timeZoneId, name, 0L, 0)
        )
        behaviorSubject.onNext(workoutList)
        return Single.just(incrementId++)
    }

    //region READ
    fun getAllWorkoutsSingle() : Single<List<GymWorkout>> {
        return Single.just(workoutList)
    }

    fun getWorkout(workoutId: Int) : Observable<GymWorkout> {
        return behaviorSubject.flatMap {
            val workout = it.find { it.id == workoutId }
            if(workout == null) {
                Observable.empty<GymWorkout>()
            } else {
                Observable.just(workout)
            }
        }
    }

    fun getWorkoutSingle(workoutId: Int): Single<GymWorkout> {
        val workout = workoutList.find { it.id == workoutId }
        return if(workout == null) {
            Single.error(Exception())
        } else {
            Single.just(workout)
        }
    }

    fun getWorkoutsByDateRangeSingle(startDate: Date? = null, endDate: Date? = null): Single<List<GymWorkout>> {
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
    //endregion

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
