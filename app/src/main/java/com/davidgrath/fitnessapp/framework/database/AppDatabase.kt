package com.davidgrath.fitnessapp.framework.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davidgrath.fitnessapp.framework.database.dao.CyclingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.CyclingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.dao.GymSetDao
import com.davidgrath.fitnessapp.framework.database.dao.GymWorkoutDao
import com.davidgrath.fitnessapp.framework.database.dao.RunningLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.RunningWorkoutDao
import com.davidgrath.fitnessapp.framework.database.dao.SwimmingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.dao.WalkingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.dao.WalkingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.dao.YogaAsanaDao
import com.davidgrath.fitnessapp.framework.database.dao.YogaWorkoutDao
import com.davidgrath.fitnessapp.framework.database.entities.CyclingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.GymSet
import com.davidgrath.fitnessapp.framework.database.entities.GymWorkout
import com.davidgrath.fitnessapp.framework.database.entities.RunningLocationData
import com.davidgrath.fitnessapp.framework.database.entities.RunningWorkout
import com.davidgrath.fitnessapp.framework.database.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.WalkingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.YogaAsana
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout

@Database(entities = [CyclingLocationData::class, CyclingWorkout::class,
                     GymSet::class, GymWorkout::class, RunningLocationData::class, RunningWorkout::class,
                    SwimmingWorkout::class, WalkingLocationData::class, WalkingWorkout::class,
                    YogaAsana::class, YogaWorkout::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cyclingLocationDataDao(): CyclingLocationDataDao
    abstract fun cyclingWorkoutDao(): CyclingWorkoutDao
    abstract fun gymSetDao(): GymSetDao
    abstract fun gymWorkoutDao(): GymWorkoutDao
    abstract fun runningLocationDataDao(): RunningLocationDataDao
    abstract fun runningWorkoutDao(): RunningWorkoutDao
    abstract fun swimmingWorkoutDao(): SwimmingWorkoutDao
    abstract fun walkingLocationDataDao(): WalkingLocationDataDao
    abstract fun walkingWorkoutDao(): WalkingWorkoutDao
    abstract fun yogaAsanaDao(): YogaAsanaDao
    abstract fun yogaWorkoutDao(): YogaWorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}