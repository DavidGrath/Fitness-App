package com.davidgrath.fitnessapp.data.entities

data class RunningLocationData(
    val id: Int,
    val workoutId: Int,
    val longitude: Double,
    val latitude: Double,
    val timestamp: Long,
    val accuracy: Float?,
    val altitude: Double?,
    val bearing: Float?,
    val speed: Float?
)
