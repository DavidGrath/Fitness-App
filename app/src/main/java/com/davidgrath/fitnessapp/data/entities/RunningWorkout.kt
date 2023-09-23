package com.davidgrath.fitnessapp.data.entities

data class RunningWorkout(
    val id: Int,
    val date: Long,
    val timeZoneId: String,
    val duration: Long,
    val kCalBurned: Int
)