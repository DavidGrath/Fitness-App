package com.davidgrath.fitnessapp.ui.entities

import java.util.Date
import java.util.TimeZone

data class SwimmingWorkoutUI(
    val id: Long = 0,
    val date: Long = Date().time,
    val timeZoneId: String = TimeZone.getDefault().id,
    val duration: Long = 0,
    val kCalBurned: Int = 0
)