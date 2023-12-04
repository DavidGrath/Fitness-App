package com.davidgrath.fitnessapp.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class WalkingWorkoutDao {

    //CREATE
    @Insert
    abstract fun insertWorkout(walkingWorkout: WalkingWorkout) : Single<Long>

    //region READ
    @Query("SELECT * FROM WalkingWorkout")
    abstract fun getAllWorkoutsSingle() : Single<List<WalkingWorkout>>
    @Query("SELECT * FROM WalkingWorkout WHERE id = :workoutId")
    abstract fun getWorkout(workoutId: Long) : Observable<WalkingWorkout>
    @Query("SELECT * FROM WalkingWorkout WHERE id = :workoutId")
    abstract fun getWorkoutSingle(workoutId: Long): Single<WalkingWorkout>
    @Query("SELECT * FROM WalkingWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRange(startDate: Long = -1, endDate: Long = -1): Observable<List<WalkingWorkout>>
    @Query("SELECT * FROM WalkingWorkout WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END)" +
            " AND (CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsByDateRangeSingle(startDate: Long = -1, endDate: Long = -1): Single<List<WalkingWorkout>>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM WalkingWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRange(startDate: Long? = -1, endDate: Long = -1): Observable<WorkoutSummary>

    @Query("SELECT SUM(kCalBurned) AS totalCaloriesBurned, COUNT(*) AS workoutCount, (SUM(duration)/60000) AS timeSpentMinutes FROM WalkingWorkout" +
            " WHERE (CASE WHEN :startDate == -1 THEN 1 ELSE :startDate <= date END) AND " +
            "(CASE WHEN :endDate == -1 THEN 1 ELSE :endDate <= date END)")
    abstract fun getWorkoutsSummaryByDateRangeSingle(startDate: Long? = -1, endDate: Long = -1): Single<WorkoutSummary>
    //endregion

    //UPDATE
    @Query("UPDATE WalkingWorkout SET duration = :duration, totalDistanceKm = :totalDistanceKm, kCalBurned = :kCalBurned WHERE id = :workoutId")
    abstract fun setWorkoutCalculations(workoutId: Long, duration: Long, totalDistanceKm: Double, kCalBurned: Int) : Single<Int>

    //DELETE
}
