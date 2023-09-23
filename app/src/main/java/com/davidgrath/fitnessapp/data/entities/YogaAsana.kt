package com.davidgrath.fitnessapp.data.entities

data class YogaAsana(
    val id: Int,
    val workoutId: Int,
    val asanaIdentifier: String,
    val timestamp: Long,
    val timeTaken: Long
)
