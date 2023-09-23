package com.davidgrath.fitnessapp.data.entities

data class YogaSessionTemplate(
    val sessionName:String,
    val sessionDescription: String,
    val asanas: List<YogaAsanaTemplate>
) {
    data class YogaAsanaTemplate(
        val identifier: String,
        val durationMillis: Int = 30_000,
    )
}