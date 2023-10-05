package com.davidgrath.fitnessapp.data.entities

data class WalkingLocationData(
    val id: Int,
    val workoutId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float?,
    val altitude: Double?,
    val bearing: Float?,
    val speed: Float?
)

