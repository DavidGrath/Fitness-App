package com.davidgrath.fitnessapp.framework.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YogaWorkout(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val date: Long,
    val timeZoneId: String,
    val name: String,
    val duration: Long = 0,
    val kCalBurned: Int = 0
)