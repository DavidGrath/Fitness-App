package com.davidgrath.fitnessapp.data.entities

data class GymWorkout(
    val id: Int,
    val date: Long,
    val timeZoneId: String,
    val name: String,
    val duration: Long,
    val kCalBurned: Int
)
