package com.davidgrath.fitnessapp.ui.running

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.SimpleAssetString
import com.davidgrath.fitnessapp.framework.database.entities.RunningWorkout
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.MapViewComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.ui.components.animateAlignmentAsState
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.millisToTimeString
import com.davidgrath.fitnessapp.util.workoutNameToAssetMap
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*@Composable
fun RunningScreen(
    viewModel: RunningViewModel
) {

    val navController = rememberNavController()
    Scaffold { padding ->
        RunningNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}*/

fun NavGraphBuilder.runningNavGraph(
    navController: NavHostController,
    viewModel: RunningViewModel,
) {
    navigation(startDestination = BasicNavScreen.RunningDashboardNav.allButLastSegment(),
        route = BasicNavScreen.RunningDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.RunningDashboardNav.path) {
            RunningDashboard(
                viewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.RunningHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.RunningWorkoutNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.RunningWorkoutNav.path)
                }

            )
        }
        composable(route = BasicNavScreen.RunningHistoryNav.path) {
            RunningHistory(
                viewModel,
                {
                    navController.popBackStack()
                }
            )
        }
        composable(route = BasicNavScreen.RunningWorkoutNav.path) {

            RunningWorkoutScreen(
                viewModel,
                onStartRunning = {
                    viewModel.startRunning()
                },
                onStopRunning = {
                    viewModel.stopRunning()
                },
                onNavigateBack = {
                    navController.popBackStack()
                })
        }

    }
}

@Composable
fun RunningNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BasicNavScreen.RunningDashboardNav.path,
        modifier
    ) {}

}

@Composable
fun RunningDashboard(
    viewModel: RunningViewModel,
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

    val runningScreensState = viewModel.runningScreensStateLiveData.observeAsState().value?: RunningViewModel.RunningScreensState()

    LaunchedEffect(runningScreensState) {
        if(runningScreensState.isRunning && isInitial) {
            onNavigateOngoingWorkout()
        }
    }

    val weekWorkouts = runningScreensState.pastWeekWorkouts
    val workoutSummary = runningScreensState.workoutSummary

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.running_header), expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("running"),
                bannerButtonText = stringResource(R.string.running_banner_button_text), onNavigateWorkoutScreen)
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
                stringResource(R.string.running_workout_summary_show_more),
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
fun RunningHistory(
    viewModel: RunningViewModel,
    onNavigateBack: () -> Unit,
) {

    val runningScreensState = viewModel.runningScreensStateLiveData.observeAsState().value?: RunningViewModel.RunningScreensState()
    val workouts = runningScreensState.workouts

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.running_header), expanded = false, onBackClicked = onNavigateBack)
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
fun RunningWorkoutScreen(
    viewModel: RunningViewModel,
    onStartRunning: () -> Unit,
    onStopRunning: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val runningScreensState = viewModel.runningScreensStateLiveData.observeAsState().value?: RunningViewModel.RunningScreensState()
    val isRunning = runningScreensState.isRunning
    val locationData = runningScreensState.locationData
    val elapsedTimeMillis = runningScreensState.elapsedTimeMillis
    val caloriesBurned = runningScreensState.currentWorkout.kCalBurned

    Column(Modifier.fillMaxSize()) {
        Surface(elevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth(), Arrangement.Center, Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = "back",
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.Start)
                            .rotate(180F)
                            .clickable {
                                onNavigateBack()
                            }

                    )
                    Text(
                        stringResource(R.string.running_header),
                        Modifier
                            .padding(vertical = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary
                    )


                    val timeString = if (isRunning) {
                        millisToTimeString(elapsedTimeMillis)
                    } else {
                        "00:00"
                    }
                    Text(
                        timeString,
                        style = MaterialTheme.typography.h3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.running_workout_label_time))
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                val resolvedKm = if (isRunning) {
                                    String.format("%.2f", runningScreensState.currentWorkout.totalDistanceKm)
                                } else {
                                    "0.00"
                                }
                                Text(
                                    resolvedKm,
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    "km",
                                    style = MaterialTheme.typography.body1.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                stringResource(R.string.running_workout_label_distance),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val resolvedCalories = if (isRunning) {
                                caloriesBurned
                            } else {
                                0L
                            }
                            Text(
                                resolvedCalories.toString(),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.running_workout_label_calories_burned),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "00:00",
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.running_workout_label_average_pace),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            MapViewComponent(
                isRunning, locationData,
                Modifier.fillMaxSize()
            )
            val resolvedText = if (isRunning) {
                stringResource(R.string.running_workout_button_stop_running)
            } else {
                stringResource(R.string.running_workout_button_start_running)
            }
            val alignment = if(isRunning) {
                Alignment.BottomCenter
            } else {
                Alignment.Center
            }
            val alignmentAnimatedValue = animateAlignmentAsState(alignment)
            SimpleGradientButton({
                if (isRunning) {
                    onStopRunning()
                } else {
                    onStartRunning()
                }
            }, Modifier
                .padding(vertical = 24.dp)
                .align(alignmentAnimatedValue.value)
                .zIndex(1f),
                {
                    Text(
                        resolvedText,
                        style = MaterialTheme.typography.button.copy(fontSize = 18.sp),
                        color = Color.White
                    )
                })
        }
//        Box(modifier = Modifier.weight(1f)) {
//            LazyColumn(Modifier.fillMaxSize()) {
//                items(locationData) {
//                    TempLocationListItem(locationData = it)
//                }
//            }
//            val resolvedText = if (isRunning) {
//                "Stop Running"
//            } else {
//                "Start Running"
//            }
//            SimpleGradientButton({
//                if (isRunning) {
//                    onStopRunning()
//                } else {
//                    onStartRunning()
//                }
//            }, Modifier
//                .padding(vertical = 24.dp)
//                .align(Alignment.Center)
//                .zIndex(1f),
//                {
//                    Text(
//                        resolvedText,
//                        style = MaterialTheme.typography.button.copy(fontSize = 18.sp),
//                        color = Color.White
//                    )
//                })
//        }
    }
}