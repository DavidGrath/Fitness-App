package com.davidgrath.fitnessapp.ui.swimming

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.FitnessAppTheme
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*@Composable
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
}*/


fun NavGraphBuilder.swimmingNavGraph(navController: NavHostController, swimmingViewModel: SwimmingViewModel) {
    navigation(startDestination = BasicNavScreen.SwimmingDashboardNav.allButLastSegment(),
        route = BasicNavScreen.SwimmingDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.SwimmingDashboardNav.path) {
            SwimmingDashboard(
                swimmingViewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.SwimmingHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.SwimmingWorkoutNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.SwimmingWorkoutNav.path)
                }
            )
        }
        composable(route = BasicNavScreen.SwimmingHistoryNav.path) {
            SwimmingHistory(
                swimmingViewModel,
                {
                    navController.popBackStack()
                }
            )

        }
        composable(route = BasicNavScreen.SwimmingWorkoutNav.path) {
            SwimmingWorkoutScreen(
                swimmingViewModel,
                onStartSwimming = {
                    swimmingViewModel.startSwimming()
                },
                onStopSwimming = {
                    swimmingViewModel.stopSwimming()
                },
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
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
    ) {}

}

@Composable
fun SwimmingDashboard(
    swimmingViewModel: SwimmingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateWorkoutScreen: () -> Unit,
    onNavigateOngoingWorkout: () -> Unit
) {
    val (isInitial, setIsInitial) = rememberSaveable {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = null) {
        setIsInitial(false)
    }

    val swimmingScreensState = swimmingViewModel.swimmingScreensStateLiveData.observeAsState().value?:SwimmingViewModel.SwimmingScreensState()

    LaunchedEffect(swimmingScreensState) {
        if(swimmingScreensState.isSwimming && isInitial) {
            onNavigateOngoingWorkout()
        }
    }
    val weekWorkouts = swimmingScreensState.pastWeekWorkouts
    val workoutSummary = swimmingScreensState.workoutSummary

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.swimming_header), expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("swimming"),
                bannerButtonText = stringResource(R.string.swimming_banner_button_text), onNavigateWorkoutScreen)
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
            Text(stringResource(R.string.swimming_workout_summary_show_more),
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
    swimmingViewModel: SwimmingViewModel,
//    currentMonth: Calendar,
//    setCurrentMonth: (Calendar) -> Unit,
    onNavigateBack: () -> Unit,
) {

    val swimmingScreensState = swimmingViewModel.swimmingScreensStateLiveData.observeAsState().value?:SwimmingViewModel.SwimmingScreensState()
    val workouts = swimmingScreensState.workouts

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.swimming_header), expanded = false, onBackClicked = onNavigateBack)
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
    swimmingViewModel: SwimmingViewModel,
    onStartSwimming: () -> Unit,
    onStopSwimming: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val swimmingScreensState = swimmingViewModel.swimmingScreensStateLiveData.observeAsState().value?:SwimmingViewModel.SwimmingScreensState()
    val isSwimming = swimmingScreensState.isSwimming
    val currentWorkout = swimmingScreensState.currentWorkout
    val elapsedTimeMillis = swimmingScreensState.elapsedTimeMillis

    Column(Modifier.fillMaxSize()) { // Don't know why I'm pointing this out now, but
        // fillMaxSize is basically match_parent+match_parent
        SimpleAppBar(title = stringResource(R.string.swimming_header), expanded = true, onBackClicked = onNavigateBack)
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
            Text(stringResource(R.string.swimming_workout_label_time))
            Spacer(modifier = Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("0", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                        Text("km", style = MaterialTheme.typography.body1.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                    }
                    Text(stringResource(R.string.swimming_workout_label_distance), style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    val resolvedCalories = if(isSwimming) {
                        currentWorkout.kCalBurned
                    } else {
                        0L
                    }
                    Text(resolvedCalories.toString(), style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.swimming_workout_label_calories_burned), style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
        val resolvedText = if(isSwimming) {
            stringResource(R.string.swimming_workout_button_stop_swimming)
        } else {
            stringResource(R.string.swimming_workout_button_start_swimming)
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