package com.davidgrath.fitnessapp.ui.cycling

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.MapViewComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.workoutNameToAssetMap
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*@Composable
fun CyclingScreen(
    viewModel: CyclingViewModel
) {

    val navController = rememberNavController()
    Scaffold { padding ->
        CyclingNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}*/

fun NavGraphBuilder.cyclingNavGraph(navController: NavHostController, cyclingViewModel: CyclingViewModel) {
//    navigation(startDestination = BasicNavScreen.CyclingDashboardNav.path, "cycling") {
    navigation(startDestination = BasicNavScreen.CyclingDashboardNav.allButLastSegment(),
        route = BasicNavScreen.CyclingDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.CyclingDashboardNav.path) {

            LaunchedEffect(key1 = null) {
                cyclingViewModel.getWorkoutsInPastWeek()
                cyclingViewModel.getFullWorkoutsSummary()
            }
            val weekWorkoutsResult = cyclingViewModel.pastWeekWorkoutsLiveData.observeAsState().value
            val workoutSummaryResult = cyclingViewModel.fullWorkoutSummaryLiveData.observeAsState().value

            val weekWorkouts = when(weekWorkoutsResult) {
                is SimpleResult.Failure -> {
                    emptyList<CyclingWorkout>()
                }
                is SimpleResult.Processing -> {
                    emptyList<CyclingWorkout>()
                }
                is SimpleResult.Success -> {
                    weekWorkoutsResult.data
                }
                null -> {
                    emptyList<CyclingWorkout>()
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

            CyclingDashboard(
                weekWorkouts,
                workoutSummary,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.CyclingHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.CyclingWorkoutNav.path)
                }
            )
        }
        composable(route = BasicNavScreen.CyclingHistoryNav.path) {
            LaunchedEffect(key1 = null) {
                cyclingViewModel.getWorkouts()
            }
            val workouts = cyclingViewModel.pastWorkoutsLiveData.observeAsState().value
            when(workouts) {
                is SimpleResult.Failure -> {}
                is SimpleResult.Processing -> {}
                is SimpleResult.Success -> {
                    CyclingHistory(
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
        composable(route = BasicNavScreen.CyclingWorkoutNav.path) {
            LaunchedEffect(key1 = null) {
                cyclingViewModel.getIsCycling()
                cyclingViewModel.getTimeElapsed()
            }
            val isCycling = cyclingViewModel.isCyclingLiveData.observeAsState().value
            val currentWorkout = cyclingViewModel.currentWorkoutLiveData.observeAsState().value
            val locationData = cyclingViewModel.locationDataLiveData.observeAsState().value
            val timeElapsed = cyclingViewModel.timeElapsedLiveData.observeAsState().value
            CyclingWorkoutScreen(
                elapsedTimeMillis = timeElapsed?:0,
                caloriesBurned = currentWorkout?.kCalBurned?:0,
                isCycling = isCycling?: false,
                locationData = locationData?: emptyList(),
                onStartCycling = {
                    cyclingViewModel.startCycling()
                },
                onStopCycling = {
                    cyclingViewModel.stopCycling()
                },
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}

@Composable
fun CyclingDashboard(
    weekWorkouts: List<CyclingWorkout>,
    workoutSummary: WorkoutSummary,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateWorkoutScreen: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = "Cycling", expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("cycling"),
                bannerButtonText = "Start Cycling", onNavigateWorkoutScreen)
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
fun CyclingHistory(
//    currentMonth: Calendar,
//    setCurrentMonth: (Calendar) -> Unit,
    onNavigateBack: () -> Unit,
    workouts: List<CyclingWorkout>
) {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = "Cycling", expanded = false, onBackClicked = onNavigateBack)
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
fun CyclingWorkoutScreen(
    elapsedTimeMillis: Long,
    caloriesBurned: Int,
    isCycling: Boolean,
    locationData: List<LocationDataUI>,
    onStartCycling: () -> Unit,
    onStopCycling: () -> Unit,
    onNavigateBack: () -> Unit
) {

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
                        "Cycling",
                        Modifier
                            .padding(vertical = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary
                    )


                    var timeString = ""
                    val resolvedMillis = if (isCycling) {
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
                    Text("Time")
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "0",
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
                                "Distance",
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //TODO Use class for state so as to reduce total lines for "resolved" parameters
                            val resolvedCalories = if (isCycling) {
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
                                "Calories Burned",
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
                                "Avg Pace (min/km)",
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
                isCycling, locationData,
                Modifier.fillMaxSize()
            )
            val resolvedText = if (isCycling) {
                "Stop Cycling"
            } else {
                "Start Cycling"
            }
            SimpleGradientButton({
                if (isCycling) {
                    onStopCycling()
                } else {
                    onStartCycling()
                }
            }, Modifier
                .padding(vertical = 24.dp)
                .align(Alignment.Center)
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
//            val resolvedText = if (isCycling) {
//                "Stop Cycling"
//            } else {
//                "Start Cycling"
//            }
//            SimpleGradientButton({
//                if (isCycling) {
//                    onStopCycling()
//                } else {
//                    onStartCycling()
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