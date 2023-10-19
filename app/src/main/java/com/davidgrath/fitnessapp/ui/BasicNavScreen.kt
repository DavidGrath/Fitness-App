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
    object CyclingHistoryNav : BasicNavScreen("home/cycling/history")
    object CyclingWorkoutNav : BasicNavScreen("home/cycling/workout")
    object GymDashboardNav : BasicNavScreen("home/gym")
    object GymHistoryNav : BasicNavScreen("home/gym/history")
    object GymRoutineListNav : BasicNavScreen("home/gym/routines")
    object GymRoutineSetsNav : BasicNavScreen("home/gym/routines/{routineId}/sets") {
        fun getPathWithArgs(routineId: Int) : String {
            return "home/gym/routines/$routineId/sets"
        }
    }
    object GymSetNav : BasicNavScreen("home/gym/routines/{routineId}/sets/{setId}") {
        fun getPathWithArgs(routineId: Int, setId: Int) : String {
            return "home/gym/routines/$routineId/sets/$setId"
        }
    }
    object YogaDashboardNav : BasicNavScreen("home/yoga")
    object YogaHistoryNav : BasicNavScreen("home/yoga/history")
    object YogaSessionListNav : BasicNavScreen("home/yoga/sessions")
    object YogaSessionAsanasNav : BasicNavScreen("home/yoga/sessions/{sessionId}/asanas") {
        fun getPathWithArgs(sessionId: Int) : String {
            return "home/yoga/sessions/$sessionId/asanas"
        }
    }
    object YogaAsanaNav : BasicNavScreen("home/yoga/sessions/{sessionId}/asanas/{asanaId}") {
        fun getPathWithArgs(sessionId: Int, asanaId: Int) : String {
            return "home/yoga/sessions/$sessionId/asanas/$asanaId"
        }
    }

    object ProfileNav : BasicNavScreen("profile")

    object SettingsNav : BasicNavScreen("settings")
    object PrivacyPolicyNav : BasicNavScreen("settings/privacy-policy")
    object TermsAndConditionsNav : BasicNavScreen("settings/terms-and-conditions")

}
