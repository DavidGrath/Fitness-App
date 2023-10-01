package com.davidgrath.fitnessapp.ui

sealed class BasicNavScreen(val path: String) {
    object ChooseActivityNav : BasicNavScreen("home")
    object SwimmingDashboardNav : BasicNavScreen("home/swimming")
    object SwimmingHistoryNav : BasicNavScreen("home/swimming/history")
    object SwimmingWorkoutNav : BasicNavScreen("home/swimming/workout")
    object RunningDashboardNav : BasicNavScreen("home/running")
    object WalkingDashboardNav : BasicNavScreen("home/walking")
    object CyclingDashboardNav : BasicNavScreen("home/cycling")
    object GymDashboardNav : BasicNavScreen("home/gym")
    object YogaDashboardNav : BasicNavScreen("home/yoga")

    object ProfileNav : BasicNavScreen("profile")

    object SettingsNav : BasicNavScreen("settings")

}
