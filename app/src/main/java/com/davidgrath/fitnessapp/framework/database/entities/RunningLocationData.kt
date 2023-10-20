package com.davidgrath.fitnessapp.framework.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RunningLocationData(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val workoutId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float?,
    val altitude: Double?,
    val bearing: Float?,
    val speed: Float?
)
