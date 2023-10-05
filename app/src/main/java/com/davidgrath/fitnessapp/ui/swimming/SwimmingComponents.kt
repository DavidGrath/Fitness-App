package com.davidgrath.fitnessapp.ui.swimming

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgrath.fitnessapp.data.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.FitnessAppTheme
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.util.SimpleResult
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun SwimmingScreen(
    viewModel: SwimmingViewModel
) {

    val navController = rememberNavController()
    Scaffold { padding ->
        SwimmingNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun SwimmingNavHost(
    navController: NavHostController,
    viewModel: SwimmingViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BasicNavScreen.SwimmingDashboardNav.path,
        modifier
    ) {
        composable(route = BasicNavScreen.SwimmingDashboardNav.path) {

            LaunchedEffect(key1 = null) {
                viewModel.getWorkoutsInPastWeek()
                viewModel.getFullWorkoutsSummary()
            }
            val weekWorkoutsResult = viewModel.pastWeekWorkoutsLiveData.observeAsState().value
            val workoutSummaryResult = viewModel.fullWorkoutSummaryLiveData.observeAsState().value

            val weekWorkouts = when(weekWorkoutsResult) {
                    is SimpleResult.Failure -> {
                        emptyList<SwimmingWorkout>()
                    }
                    is SimpleResult.Processing -> {
                        emptyList<SwimmingWorkout>()
                    }
                    is SimpleResult.Success -> {
                        weekWorkoutsResult.data
                    }
                    null -> {
                        emptyList<SwimmingWorkout>()
                    }
            }

            val workoutSummary = when(workoutSummaryResult) {
                is SimpleResult.Failure, is SimpleResult.Processing, null -> {
                    WorkoutSummary(0, 0, 0)
                }
                is SimpleResult.Success -> {
                    workoutSummaryResult.data
                }
            }

            SwimmingDashboard(
                weekWorkouts,
                workoutSummary,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.SwimmingHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.SwimmingWorkoutNav.path)
                }
            )
        }
        composable(route = BasicNavScreen.SwimmingHistoryNav.path) {
            LaunchedEffect(key1 = null) {
                viewModel.getWorkouts()
            }
            val workouts = viewModel.pastWorkoutsLiveData.observeAsState().value
            when(workouts) {
                is SimpleResult.Failure -> {}
                is SimpleResult.Processing -> {}
                is SimpleResult.Success -> {
                    SwimmingHistory(
//                        currentMonth, { cal ->
//                            setCurrentMonth(cal)
//                            viewModel.getWorkoutsInMonth(cal.time)
//                        },
                        {
                        navController.popBackStack()
                    }, workouts.data)
                }
                null -> {}
            }

        }
        composable(route = BasicNavScreen.SwimmingWorkoutNav.path) {
            LaunchedEffect(key1 = null) {
                viewModel.getIsSwimming()
            }
            val isSwimming = viewModel.isSwimmingLiveData.observeAsState().value
            val currentWorkout = viewModel.currentWorkoutLiveData.observeAsState().value
            SwimmingWorkoutScreen(
                elapsedTimeMillis = currentWorkout?.duration?:0,
                caloriesBurned = currentWorkout?.kCalBurned?:0,
                isSwimming = isSwimming?: false,
                onStartSwimming = {
                    viewModel.startSwimming()
                },
                onStopSwimming = {
                    viewModel.stopSwimming()
                },
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }

}

@Composable
fun SwimmingDashboard(
    weekWorkouts: List<SwimmingWorkout>,
    workoutSummary: WorkoutSummary,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateWorkoutScreen: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = "Swimming", expanded = true, onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            WelcomeBanner(bannerButtonText = "Start Swimming", onNavigateWorkoutScreen)
            Spacer(Modifier.height(8.dp))
            WorkoutSummaryComponent(summary = workoutSummary)
            Spacer(Modifier.height(8.dp))
            WeekHistoryComponent(
                startingDate = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, -6) },
                workoutDates = weekWorkouts.map { workout ->
                    Calendar.getInstance().also { it.timeInMillis = workout.date }
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Show More",
                Modifier
                    .clickable(onClick = onNavigateDetailedHistory)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun SwimmingHistory(
//    currentMonth: Calendar,
//    setCurrentMonth: (Calendar) -> Unit,
    onNavigateBack: () -> Unit,
    workouts: List<SwimmingWorkout>
) {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = "Swimming", expanded = false, onNavigateBack)
        val highlightedDates = workouts.map { w -> Calendar.getInstance().also { it.timeInMillis = w.date } }
        val (currentMonth, setCurrentMonth) = remember {
            mutableStateOf(Calendar.getInstance())
        }
        CalendarComponent(
            currentMonth = currentMonth,
            onSetCurrentMonth = setCurrentMonth,
            onDateClicked = {},
            highlightedDates = highlightedDates.toTypedArray(),
            firstDayOfWeek = Calendar.SUNDAY,
            timeZoneId = TimeZone.getDefault().id,
//            minDate = Calendar.getInstance().also { it.set(Calendar.MONTH, 0) },
//            maxDate = Calendar.getInstance()
        )
    }

}

@Composable
fun SwimmingWorkoutScreen(
    elapsedTimeMillis: Long,
    caloriesBurned: Int,
    isSwimming: Boolean,
    onStartSwimming: () -> Unit,
    onStopSwimming: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) { // Don't know why I'm pointing this out now, but
        // fillMaxSize is basically match_parent+match_parent
        SimpleAppBar(title = "Swimming", expanded = true, onNavigateBack)
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f), Arrangement.Center, Alignment.CenterHorizontally) {
            var timeString = ""
            val resolvedMillis = if(isSwimming) {
                elapsedTimeMillis
            } else {
                0L
            }
            resolvedMillis.toDuration(DurationUnit.MILLISECONDS).toComponents { hours, minutes, seconds, nanoseconds ->
                timeString = if(hours > 0) {
                    String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                } else {
                    String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                }

            }
            Text(timeString,
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Time")
            Spacer(modifier = Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("0", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                        Text("km", style = MaterialTheme.typography.body1.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                    }
                    Text("Distance", style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    //TODO Use class for state so as to reduce total lines for "resolved" parameters
                    val resolvedCalories = if(isSwimming) {
                        caloriesBurned
                    } else {
                        0L
                    }
                    Text(resolvedCalories.toString(), style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
                    Text("Calories Burned", style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
        val resolvedText = if(isSwimming) {
            "Stop Swimming"
        } else {
            "Start Swimming"
        }
        SimpleGradientButton({
            if(isSwimming) {
                onStopSwimming()
            } else {
                onStartSwimming()
            }
        }, Modifier
            .padding(vertical = 24.dp)
            .align(Alignment.CenterHorizontally),
            {
                Text(resolvedText,
                    style = MaterialTheme.typography.button.copy(fontSize = 18.sp),
                    color = Color.White
                )
            })
    }
}

@Preview
@Composable
fun SwimmingDashboardPreview() {
    FitnessAppTheme {
//        SwimmingDashboard({}, {})
    }
}