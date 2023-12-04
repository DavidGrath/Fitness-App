package com.davidgrath.fitnessapp.util

import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.RunningWorkout
import com.davidgrath.fitnessapp.framework.database.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
import com.davidgrath.fitnessapp.ui.entities.CyclingWorkoutUI
import com.davidgrath.fitnessapp.ui.entities.RunningWorkoutUI
import com.davidgrath.fitnessapp.ui.entities.SwimmingWorkoutUI
import com.davidgrath.fitnessapp.ui.entities.WalkingWorkoutUI
import com.davidgrath.fitnessapp.ui.entities.YogaWorkoutUI

fun runningWorkoutToRunningWorkoutUI(runningWorkout: RunningWorkout): RunningWorkoutUI {
    return RunningWorkoutUI(
        runningWorkout.id ?: 0,
        runningWorkout.date,
        runningWorkout.timeZoneId,
        runningWorkout.duration,
        runningWorkout.totalDistanceKm,
        runningWorkout.kCalBurned
    )
}

fun swimmingWorkoutToSwimmingWorkoutUI(swimmingWorkout: SwimmingWorkout): SwimmingWorkoutUI {
    return SwimmingWorkoutUI(
        swimmingWorkout.id ?: 0,
        swimmingWorkout.date,
        swimmingWorkout.timeZoneId,
        swimmingWorkout.duration,
        swimmingWorkout.kCalBurned
    )
}

fun walkingWorkoutToWalkingWorkoutUI(walkingWorkout: WalkingWorkout): WalkingWorkoutUI {
    return WalkingWorkoutUI(
        walkingWorkout.id ?: 0,
        walkingWorkout.date,
        walkingWorkout.timeZoneId,
        walkingWorkout.duration,
        walkingWorkout.totalDistanceKm,
        walkingWorkout.kCalBurned
    )
}

fun yogaWorkoutToYogaWorkoutUI(yogaWorkout: YogaWorkout): YogaWorkoutUI {
    return YogaWorkoutUI(
        yogaWorkout.id ?: 0,
        yogaWorkout.date,
        yogaWorkout.timeZoneId,
        yogaWorkout.name,
        yogaWorkout.duration,
        yogaWorkout.kCalBurned
    )
}

fun cyclingWorkoutToCyclingWorkoutUI(cyclingWorkout: CyclingWorkout): CyclingWorkoutUI {
    return CyclingWorkoutUI(
        cyclingWorkout.id ?: 0,
        cyclingWorkout.date,
        cyclingWorkout.timeZoneId,
        cyclingWorkout.duration,
        cyclingWorkout.totalDistanceKm,
        cyclingWorkout.kCalBurned
    )
}