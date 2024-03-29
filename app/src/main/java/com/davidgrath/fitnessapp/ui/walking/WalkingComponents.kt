package com.davidgrath.fitnessapp.ui.walking

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
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.MapViewComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.ui.components.animateAlignmentAsState
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*@Composable
fun WalkingScreen(
    viewModel: WalkingViewModel
) {

    val navController = rememberNavController()
    Scaffold { padding ->
        WalkingNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}*/
fun NavGraphBuilder.walkingNavGraph(navController: NavHostController, walkingViewModel: WalkingViewModel) {
    navigation(BasicNavScreen.WalkingDashboardNav.allButLastSegment(), BasicNavScreen.WalkingDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.WalkingDashboardNav.path) {
            WalkingDashboard(
                walkingViewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.WalkingHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.WalkingWorkoutNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.WalkingWorkoutNav.path)
                }
            )
        }
        composable(route = BasicNavScreen.WalkingHistoryNav.path) {
            WalkingHistory(
                walkingViewModel,
                {
                    navController.popBackStack()
                }
            )
        }
        composable(route = BasicNavScreen.WalkingWorkoutNav.path) {
            WalkingWorkoutScreen(
                walkingViewModel,
                onStartWalking = {
                    walkingViewModel.startWalking()
                },
                onStopWalking = {
                    walkingViewModel.stopWalking()
                },
                onNavigateBack = {
                    navController.popBackStack()
                })
        }

    }
}

@Composable
fun WalkingDashboard(
    walkingViewModel: WalkingViewModel,
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

    val walkingScreensState = walkingViewModel.walkingScreensStateLiveData.observeAsState()
        .value?: WalkingViewModel.WalkingScreensState()

    LaunchedEffect(walkingScreensState) {
        if(walkingScreensState.isWalking && isInitial) {
            onNavigateOngoingWorkout()
        }
    }
    val weekWorkouts = walkingScreensState.pastWeekWorkouts
    val workoutSummary = walkingScreensState.workoutSummary

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.walking_header), expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("walking"),
                bannerButtonText = stringResource(R.string.walking_banner_button_text), onNavigateWorkoutScreen)
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
                stringResource(R.string.walking_workout_summary_show_more),
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
fun WalkingHistory(
    walkingViewModel: WalkingViewModel,
    onNavigateBack: () -> Unit,
) {

    val walkingScreensState = walkingViewModel.walkingScreensStateLiveData.observeAsState()
        .value?:WalkingViewModel.WalkingScreensState()
    val workouts = walkingScreensState.workouts


    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.walking_header), expanded = false, onBackClicked = onNavigateBack)
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
fun WalkingWorkoutScreen(
    walkingViewModel: WalkingViewModel,
    onStartWalking: () -> Unit,
    onStopWalking: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val walkingScreensState = walkingViewModel.walkingScreensStateLiveData.observeAsState()
        .value?:WalkingViewModel.WalkingScreensState()

    val isWalking = walkingScreensState.isWalking
    val currentWorkout = walkingScreensState.currentWorkout
    val locationData = walkingScreensState.locationData
    val elapsedTimeMillis = walkingScreensState.elapsedTimeMillis

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
                        stringResource(R.string.walking_header),
                        Modifier
                            .padding(vertical = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary
                    )


                    var timeString = ""
                    val resolvedMillis = if (isWalking) {
                        elapsedTimeMillis
                    } else {
                        0L
                    }
                    resolvedMillis.toDuration(DurationUnit.MILLISECONDS)
                        .toComponents { hours, minutes, seconds, nanoseconds ->
                            timeString = if (hours > 0) {
                                String.format("%02d", hours) + ":" + String.format(
                                    "%02d",
                                    minutes
                                ) + ":" + String.format("%02d", seconds)
                            } else {
                                String.format("%02d", minutes) + ":" + String.format(
                                    "%02d",
                                    seconds
                                )
                            }

                        }
                    Text(
                        timeString,
                        style = MaterialTheme.typography.h3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.walking_workout_label_time))
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                val resolvedKm = if (isWalking) {
                                    String.format("%.2f", walkingScreensState.currentWorkout.totalDistanceKm)
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
                                stringResource(R.string.walking_workout_label_distance),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val resolvedCalories = if (isWalking) {
                                currentWorkout.kCalBurned
                            } else {
                                0
                            }
                            Text(
                                resolvedCalories.toString(),
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.walking_workout_label_calories_burned),
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
                                stringResource(R.string.walking_workout_label_average_pace),
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
                isWalking, locationData,
                Modifier.fillMaxSize()
            )
            val resolvedText = if (isWalking) {
                stringResource(R.string.walking_workout_button_stop_walking)
            } else {
                stringResource(R.string.walking_workout_button_start_walking)
            }
            val alignment = if(isWalking) {
                Alignment.BottomCenter
            } else {
                Alignment.Center
            }
            val alignmentAnimatedValue = animateAlignmentAsState(alignment)
            SimpleGradientButton({
                if (isWalking) {
                    onStopWalking()
                } else {
                    onStartWalking()
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