package com.davidgrath.fitnessapp.data.entities

data class WorkoutSummary(
    val totalCaloriesBurned: Int,
    val workoutCount: Int = 0,
    val timeSpentMinutes: Int
)
