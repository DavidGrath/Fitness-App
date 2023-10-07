package com.davidgrath.fitnessapp.ui

sealed class BasicNavScreen(val path: String) {
    object ChooseActivityNav : BasicNavScreen("home")
    object SwimmingDashboardNav : BasicNavScreen("home/swimming")
    object SwimmingHistoryNav : BasicNavScreen("home/swimming/history")
    object SwimmingWorkoutNav : BasicNavScreen("home/swimming/workout")
    object RunningDashboardNav : BasicNavScreen("home/running")
    object RunningHistoryNav : BasicNavScreen("home/running/history")
    object RunningWorkoutNav : BasicNavScreen("home/running/workout")
    object WalkingDashboardNav : BasicNavScreen("home/walking")
    object WalkingHistoryNav : BasicNavScreen("home/walking/history")
    object WalkingWorkoutNav : BasicNavScreen("home/walking/workout")
    object CyclingDashboardNav : BasicNavScreen("home/cycling")
    object GymDashboardNav : BasicNavScreen("home/gym")
    object YogaDashboardNav : BasicNavScreen("home/yoga")

    object ProfileNav : BasicNavScreen("profile")

    object SettingsNav : BasicNavScreen("settings")

}
