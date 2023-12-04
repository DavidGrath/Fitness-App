package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class YogaWorkoutDao {


    //CREATE
    @Insert
    abstract fun insertWorkout(yogaWorkout: YogaWorkout) : Single<Long>

    //region READ
    @Query("SELECT * FROM YogaWorkout")
    abstract fun getAllWorkoutsSingle() : Single<List<YogaWorkout>>
    @Query("SELECT * FROM YogaWorkout WHERE id = :workoutId")
    abstract fun getWorkout(workoutId: Long) : Observable<YogaWorkout>
    @Query("SELECT * FROM YogaWorkout WHERE id = :workoutId")
    abstract fun getWorkoutSingle(workoutId: Long): Single<YogaWorkout>
    @Query("SELECT * FROM YogaWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRange(startDate: Long = -1, endDate: Long = -1): Observable<List<YogaWorkout>>

    @Query("SELECT * FROM YogaWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRangeSingle(startDate: Long = -1, endDate: Long = -1): Single<List<YogaWorkout>>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM YogaWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRange(startDate: Long? = -1, endDate: Long = -1): Observable<WorkoutSummary>
    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM YogaWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRangeSingle(startDate: Long? = -1, endDate: Long = -1): Single<WorkoutSummary>
    //endregion

    //UPDATE
    @Query("UPDATE YogaWorkout SET duration = :duration, kCalBurned = :kCalBurned WHERE id = :workoutId")
    abstract fun setWorkoutDurationAndKCalBurned(workoutId: Long, duration: Long, kCalBurned: Int) : Single<Int>

    //DELETE
}