package com.davidgrath.fitnessapp.ui.onboarding

import com.davidgrath.fitnessapp.util.inchesToFeetAndInches

data class OnboardingScreenState(
    val pageCount: Int = 0,
    val currentPageIndex: Int = 0,
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
    val weightUnit: String = ""
) {
    override fun toString(): String {
        if(heightUnit.equals("inches", true)) {
            return "OnboardingScreenState(pageCount=$pageCount, currentPageIndex=$currentPageIndex, firstName='$firstName', lastName='$lastName', email='$email', gender='$gender', height=$height: ${inchesToFeetAndInches(height)}, heightUnit='$heightUnit', birthDateDay=$birthDateDay, birthDateMonth=$birthDateMonth, birthDateYear=$birthDateYear, weight=$weight, weightUnit='$weightUnit')"
        } else {
            return "OnboardingScreenState(pageCount=$pageCount, currentPageIndex=$currentPageIndex, firstName='$firstName', lastName='$lastName', email='$email', gender='$gender', height=$height, heightUnit='$heightUnit', birthDateDay=$birthDateDay, birthDateMonth=$birthDateMonth, birthDateYear=$birthDateYear, weight=$weight, weightUnit='$weightUnit')"
        }
    }
}