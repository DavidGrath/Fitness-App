package com.davidgrath.fitnessapp.ui.profile

data class ProfileScreenState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val gender: String = "",
    val height: Int = 0,
    val heightUnit: String = "",
    val birthDateDay: Int = 0,
    val birthDateMonth: Int = 0,
    val birthDateYear: Int = 0,
    val weight: Float = 0.0F,
    val weightUnit: String = "",
    val userAvatar: String = "",
    val userAvatarType: String = "none",
    val userAvatarFileExists: Boolean = false,
)