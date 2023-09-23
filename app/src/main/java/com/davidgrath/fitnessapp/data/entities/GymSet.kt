package com.davidgrath.fitnessapp.data.entities

data class GymSet(
    val id: Int,
    val workoutId: Int,
    val setIdentifier: String,
    val timestamp: Long,
    val repCount: Int,
    //Based on personal experience, I now believe this field has no use
    val timed: Boolean,
    val timeTaken: Long
)
