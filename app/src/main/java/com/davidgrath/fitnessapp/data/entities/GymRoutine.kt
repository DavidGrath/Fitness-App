package com.davidgrath.fitnessapp.data.entities

data class GymRoutine(
    val id: Int,
    val workoutId: Int,
    val timestamp: Long,
    val routineName: String
)
