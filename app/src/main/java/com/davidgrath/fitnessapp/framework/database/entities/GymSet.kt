package com.davidgrath.fitnessapp.framework.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GymSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val workoutId: Long,
    val setIdentifier: String,
    val timestamp: Long,
    val repCount: Int,
    val timeTaken: Long
)
