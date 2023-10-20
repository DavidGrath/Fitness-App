package com.davidgrath.fitnessapp.framework.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YogaAsana(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val workoutId: Long,
    val asanaIdentifier: String,
    val timestamp: Long,
    val timeTaken: Long
)
