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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Observer
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
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
    navigation(startDestination = BasicNavScreen.CyclingDashboardNav.path,
        route = BasicNavScreen.CyclingDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.CyclingDashboardNav.path) {
            CyclingDashboard(
                cyclingViewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.CyclingHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.CyclingWorkoutNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.CyclingWorkoutNav.path)
                }
            )
        }
        composable(route = BasicNavScreen.CyclingHistoryNav.path) {
            CyclingHistory(
                cyclingViewModel,
                {
                    navController.popBackStack()
                })
        }
        composable(route = BasicNavScreen.CyclingWorkoutNav.path) {
            CyclingWorkoutScreen(
                cyclingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}

@Composable
fun CyclingDashboard(
    cyclingViewModel: CyclingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateWorkoutScreen: () -> Unit,
    onNavigateOngoingWorkout: () -> Unit
) {
    val (isInitial, setIsInitial) = rememberSaveable {
        mutableStateOf(true)
    }
    val cyclingScreenState =
        cyclingViewModel.cyclingScreenStateLiveData.observeAsState().value?: CyclingViewModel.CyclingScreenState()

    LaunchedEffect(null) {
        setIsInitial(false)
    }

    LaunchedEffect(cyclingScreenState) {
        if(cyclingScreenState.isCycling && isInitial) {
            onNavigateOngoingWorkout()
        }
    }
    val weekWorkouts = cyclingScreenState.pastWeekWorkouts
    val workoutSummary = cyclingScreenState.workoutSummary


    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.cycling_header), expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("cycling"),
                bannerButtonText = stringResource(R.string.cycling_banner_button_text), onNavigateWorkoutScreen)
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
                stringResource(R.string.cycling_workout_summary_show_more),
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
    cyclingViewModel: CyclingViewModel,
    onNavigateBack: () -> Unit
) {

    val cyclingScreenState = cyclingViewModel.cyclingScreenStateLiveData.observeAsState().value?:CyclingViewModel.CyclingScreenState()
    val workouts = cyclingScreenState.workouts

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.cycling_header), expanded = false, onBackClicked = onNavigateBack)
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
    cyclingViewModel: CyclingViewModel,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(key1 = null) {
        cyclingViewModel.getIsCycling()
        cyclingViewModel.getTimeElapsed()
    }
    val cyclingScreenState = cyclingViewModel.cyclingScreenStateLiveData.observeAsState().value?: CyclingViewModel.CyclingScreenState()
    val isCycling = cyclingScreenState.isCycling
    val caloriesBurned = cyclingScreenState.currentWorkout.kCalBurned
    val locationData = cyclingScreenState.locationData
    val elapsedTimeMillis = cyclingScreenState.elapsedTimeMillis

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
                        stringResource(R.string.cycling_header),
                        Modifier
                            .padding(vertical = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary
                    )

                    val timeString = if(isCycling) {
                        millisToTimeString(elapsedTimeMillis)
                    } else {
                        "00:00"
                    }
                    Text(
                        timeString,
                        style = MaterialTheme.typography.h3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.cycling_workout_label_time))
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                val resolvedKm = if (isCycling) {
                                    String.format("%.2f", cyclingScreenState.currentWorkout.totalDistanceKm)
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
                                stringResource(R.string.cycling_workout_label_distance),
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
                                stringResource(R.string.cycling_workout_label_calories_burned),
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
                                stringResource(R.string.cycling_workout_label_average_pace),
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
                stringResource(R.string.cycling_workout_button_stop_cycling)
            } else {
                stringResource(R.string.cycling_workout_button_start_cycling)
            }
            val alignment = if(isCycling) {
                Alignment.BottomCenter
            } else {
                Alignment.Center
            }
            val alignmentAnimatedValue = animateAlignmentAsState(alignment)
            SimpleGradientButton({
                if (isCycling) {
                    cyclingViewModel.stopCycling()
                } else {
                    cyclingViewModel.startCycling()
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