package com.davidgrath.fitnessapp.data.entities

data class GymRoutineTemplate(
    val routineName:String,
    val sets: List<GymSetTemplate>
) {
    data class GymSetTemplate(
        val identifier: String,
        val repCount: Int = -1,
    )
}

