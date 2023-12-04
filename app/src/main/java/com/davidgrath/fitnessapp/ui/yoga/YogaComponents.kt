package com.davidgrath.fitnessapp.ui.yoga

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Observer
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.data.entities.YogaAsanaTutorial
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.SimpleAssetString
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.workoutNameToAssetMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*@Composable
fun YogaScreen(
    viewModel: YogaViewModel
) {
    val navController = rememberNavController()
    Scaffold { padding ->
        YogaNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}*/

fun NavGraphBuilder.yogaNavGraph(navController: NavHostController, yogaViewModel: YogaViewModel) {
    navigation(BasicNavScreen.YogaDashboardNav.allButLastSegment(), BasicNavScreen.YogaDashboardNav.lastSegment()) {

        composable(BasicNavScreen.YogaDashboardNav.path) {
            YogaDashboardScreen(
                yogaViewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.YogaHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.YogaSessionListNav.path)
                },
                { sessionIndex, asanaIndex ->
                    navController.navigate(BasicNavScreen.YogaAsanaNav.getPathWithArgs(sessionIndex, asanaIndex))
                }
            )
        }
        composable(BasicNavScreen.YogaHistoryNav.path) {
            YogaHistoryScreen(
                yogaViewModel,
                {
                    navController.popBackStack()
                })
        }
        composable(BasicNavScreen.YogaSessionListNav.path) {
            YogaSessionListScreen(
                {
                    navController.popBackStack()
                },
                {
                    val pathWithArgs = BasicNavScreen.YogaSessionAsanasNav.getPathWithArgs(it)
                    navController.navigate(pathWithArgs)
                }
            )
        }
        composable(BasicNavScreen.YogaSessionAsanasNav.path,
            listOf(navArgument("sessionId") {type = NavType.IntType})
        ) { backStackEntry ->
            val sessionIndex = backStackEntry.arguments!!.getInt("sessionId")
            YogaSessionAsanasScreen(
                selectedSessionIndex = sessionIndex,
                viewModel = yogaViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateFirstAsana = {
                    val pathWithArgs = BasicNavScreen.YogaAsanaNav.getPathWithArgs(it, 0)
                    navController.navigate(pathWithArgs)
                }
            )
        }
        composable(BasicNavScreen.YogaAsanaNav.path,
            arguments = listOf(
                navArgument("sessionId") {type = NavType.IntType},
                navArgument("asanaId") {type = NavType.IntType},
            )
        ) { backStackEntry ->
            val sessionIndex = backStackEntry.arguments!!.getInt("sessionId")
            val asanaIndex = backStackEntry.arguments!!.getInt("asanaId")

            YogaAsanaScreen(
                sessionIndex,
                asanaIndex,
                yogaViewModel,
                {
                    navController.popBackStack()
                },
                { yogaSessionIndex, yogaAsanaIndex ->
                    val pathWithArgs = BasicNavScreen.YogaAsanaNav.getPathWithArgs(yogaSessionIndex, yogaAsanaIndex)
                    navController.popBackStack(navController.currentBackStackEntry!!.destination.route!!, true)
                    navController.navigate(pathWithArgs)
                },
                {
                    navController.navigate(BasicNavScreen.YogaSessionListNav.path) {
                        launchSingleTop = true
                        popUpTo(BasicNavScreen.YogaSessionListNav.path)
                    }
                }
            )
        }

    }
}

@Composable
fun YogaDashboardScreen(
    viewModel: YogaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateSessionsScreen: () -> Unit,
    onNavigateOngoingWorkout: (sessionIndex: Int, asanaIndex: Int) -> Unit
) {
    val (isInitial, setIsInitial) = rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = null) {
        setIsInitial(false)
    }
    val yogaScreensState =
        viewModel.yogaScreensStateLiveData.observeAsState().value?: YogaViewModel.YogaScreensState()

    LaunchedEffect(yogaScreensState) {
        if(yogaScreensState.isDoingYoga && isInitial) {
            val sessionIndex = if(yogaScreensState.currentSessionIndex >= 0) {
                yogaScreensState.currentSessionIndex
            } else {
                0
            }
            val asanaIndex = if(yogaScreensState.currentAsanaIndex >= 0) {
                yogaScreensState.currentAsanaIndex
            } else {
                0
            }
            onNavigateOngoingWorkout(sessionIndex, asanaIndex)
        }
    }
    val weekWorkouts = yogaScreensState.pastWeekWorkouts
    val workoutSummary = yogaScreensState.workoutSummary

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.yoga_header), expanded = true, onBackClicked = onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("yoga"),
                bannerButtonText = stringResource(R.string.yoga_banner_button_text), onNavigateSessionsScreen)
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
                stringResource(R.string.yoga_workout_summary_show_more),
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
fun YogaHistoryScreen(
    viewModel: YogaViewModel,
    onNavigateBack: () -> Unit
) {

    val yogaScreensState =
        viewModel.yogaScreensStateLiveData.observeAsState().value?:YogaViewModel.YogaScreensState()
    val workouts = yogaScreensState.pastWorkouts
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.yoga_header), expanded = false, onBackClicked = onNavigateBack)
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
fun YogaSessionListScreen(
    onNavigateBack: () -> Unit,
    onSessionSelected: (index: Int) -> Unit
) {
    val context = LocalContext.current
    val application = (context.applicationContext as FitnessApp)
    val sessions = application.defaultYogaSessionTemplates

    Column(Modifier.fillMaxSize()) {
        SimpleAppBar(title = stringResource(R.string.yoga_header), expanded = true, onBackClicked = onNavigateBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            itemsIndexed(sessions, { index, item -> index }) { index, item ->
                SimpleYogaSessionItem(
                    index = index,
                    sessionTemplate = item,
                    onSessionSelected = onSessionSelected,
                    modifier = Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun YogaSessionAsanasScreen(
    selectedSessionIndex: Int,
    viewModel: YogaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateFirstAsana: (sessionIndex: Int) -> Unit
) {
    val context = LocalContext.current
    val sessionTemplates = (context.applicationContext as FitnessApp).defaultYogaSessionTemplates
    val asanaTutorials = (context.applicationContext as FitnessApp).yogaAsanaTutorials
    val asanaTitleMap = (context.applicationContext as FitnessApp).asanaIdentifierTitles
    val sessionTemplate = sessionTemplates[selectedSessionIndex]
    val coroutineScope = rememberCoroutineScope()
    val (currentTutorial, setCurrentTutorial) = remember {
        mutableStateOf<YogaAsanaTutorial?>(null)
    }
    val (currentAsanaIdentifier, setCurrentAsanaIdentifier) = remember {
        mutableStateOf<String>("")
    }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val tempVideoDetails = viewModel.tempVideoDetailsLiveData.observeAsState().value

    ModalBottomSheetLayout(sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentTutorial == null) {
                    return@ModalBottomSheetLayout
                }
                val title = asanaTitleMap[currentAsanaIdentifier]?._default ?: "Unknown"
                if (currentTutorial.youtubeVideoId != null) {
                    Text(
                        stringResource(R.string.yoga_tutorial_heading_video),
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(8.dp))
                    if(!(tempVideoDetails?.thumbnailUrl.isNullOrEmpty())) {
                        GlideImage(model = tempVideoDetails?.thumbnailUrl, contentDescription = "yt thumbnail",
                            Modifier.fillMaxWidth(),
                        ) {
                            it.override(tempVideoDetails!!.thumbnailWidth!!, tempVideoDetails!!.thumbnailHeight!!)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color.Green)
                                .size(480.dp, 360.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!tempVideoDetails?.title.isNullOrEmpty()) {
                                Text(tempVideoDetails?.title!!)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(title,
                    Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                currentTutorial.steps.map {
                    Text(
                        Constants.BULLET + " " + it,
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body1)
                }
                Spacer(modifier = Modifier.height(8.dp))
                SimpleGradientButton(
                    onClicked = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    }, modifier = Modifier) {
                    Text(stringResource(R.string.yoga_tutorial_button_close), style = MaterialTheme.typography.button.copy(Color.White))
                }
            }
        }
    ) {
        Column() {
            SimpleAppBar(title = "", expanded = false, onBackClicked = onNavigateBack)
            Box {
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red)
                            .padding(horizontal = 16.dp)
                            .height(168.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            sessionTemplate.sessionName,
                            style = MaterialTheme.typography.h4.copy(color = Color.White)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            sessionTemplate.sessionDescription,
                            style = MaterialTheme.typography.body1.copy(color = Color.White)
                        )
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 48.dp)) {
                        itemsIndexed(
                            sessionTemplate.asanas,
                            { index, item -> index }) { index, item ->
                            SimpleYogaAsanaItem(
                                yogaAsana = item,
                                onViewTutorial = {
                                    val tutorial = asanaTutorials[it]!!
                                    setCurrentTutorial(tutorial)
                                    setCurrentAsanaIdentifier(it)
                                    tutorial.youtubeVideoId?.let {
                                        viewModel.tempFetchVideoDetails(it)
                                    }
                                    coroutineScope.launch {
                                        sheetState.show()
                                    }
                                }, modifier = Modifier
                            )
                            if (index < sessionTemplate.asanas.size - 1) {
                                Divider(color = Color.Gray.copy(.6f))
                            }
                        }
                    }
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                SimpleGradientButton(
                    {
                        coroutineScope.launch {
                            viewModel.addWorkout()
                            viewModel.addWorkoutLiveData.observe(lifecycleOwner, object: Observer<SimpleResult<Unit>> {
                                override fun onChanged(value: SimpleResult<Unit>) {
                                    if(value is SimpleResult.Success) {
                                        viewModel.setSessionAndAsanaIndex(selectedSessionIndex, 0)
                                        onNavigateFirstAsana(selectedSessionIndex)
                                        viewModel.addWorkoutLiveData.removeObserver(this)
                                    }
                                }
                            })
                        }
                    },
                    Modifier
                        .zIndex(1f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    16.dp,
                    true
                ) {
                    Text(
                        stringResource(R.string.yoga_workout_button_start),
                        style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                    )
                }
            }
        }
        BackHandler(sheetState.isVisible) {
            coroutineScope.launch {
                sheetState.hide()
            }
        }
    }
}


@Composable
fun YogaAsanaScreen(
    yogaSessionIndex: Int,
    yogaAsanaIndex: Int,
    viewModel: YogaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAsana: (yogaSessionIndex: Int, yogaAsanaIndex: Int) -> Unit,
    onNavigateSessionsScreen: () -> Unit
) {

    val context = LocalContext.current
    val sessionTemplates = (context.applicationContext as FitnessApp).defaultYogaSessionTemplates
    val asanaTitleMap = (context.applicationContext as FitnessApp).asanaIdentifierTitles

    val currentSession = sessionTemplates[yogaSessionIndex]
    val currentYogaAsana = currentSession.asanas[yogaAsanaIndex]
    val sessionTitle = currentSession.sessionName
    val asanaTitle = asanaTitleMap[currentYogaAsana.identifier]?._default ?: "Unknown"
    val yogaScreensState =
        viewModel.yogaScreensStateLiveData.observeAsState().value?:YogaViewModel.YogaScreensState()

    LaunchedEffect(key1 = null) {
        if(viewModel.asanasProgress < yogaAsanaIndex
//            && yogaAsanaState.identifier != currentSession.asanas[yogaScreensState.currentAsanaIndex].identifier
        ) {
            viewModel.startAsana(
                yogaAsanaIndex,
                currentSession.asanas[yogaAsanaIndex].identifier,
                currentSession.asanas[yogaAsanaIndex].durationMillis
            )
        }
    }

    LaunchedEffect(yogaScreensState) {
        //I use -1 as the default value, so take note for any possible side-effects
        if (yogaScreensState.currentSessionIndex == yogaSessionIndex &&
            yogaScreensState.currentAsanaIndex >= 0 &&
            yogaScreensState.currentAsanaIndex != yogaAsanaIndex) {
            onNavigateToAsana(
                yogaScreensState.currentSessionIndex,
                yogaScreensState.currentAsanaIndex
            )
        }
    }

    val yogaAsanaState = viewModel.yogaAsanaStateLiveData.observeAsState().value?: YogaAsanaState(0, false)

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        SimpleAppBar(title = sessionTitle, expanded = false, textColor = Color.Black, onBackClicked = onNavigateBack)
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(144.dp)
                    .background(Color.Yellow)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(asanaTitle, style = MaterialTheme.typography.h5)
        }
        Divider(color = Color.Gray.copy(0.5f))
        val canGoPrevious = yogaAsanaIndex > 0
        val canGoNext = yogaAsanaIndex < currentSession.asanas.size - 1
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var counterText = "00:00"
            val duration = yogaAsanaState.timeLeft.toDuration(DurationUnit.MILLISECONDS)
            duration.toComponents { hours, minutes, seconds, nanoseconds ->
                val roundedSeconds = if(nanoseconds / 1_000_000 > 0) {
                    seconds + 1
                } else {
                    seconds
                }
                val formatted = if(hours > 0) {
                    String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", roundedSeconds)
                } else {
                    String.format("%02d", minutes) + ":" + String.format("%02d", roundedSeconds)
                }
                counterText = formatted
            }
            Text(counterText, style = MaterialTheme.typography.h3)

            val endAsanaResult = viewModel.endAsanaLiveData.observeAsState().value
            val endWorkoutResult = viewModel.endCurrentWorkoutLiveData.observeAsState().value

            val nextButtonEnabled = if (canGoNext) {
                endAsanaResult !is SimpleResult.Processing
            } else {
                endWorkoutResult !is SimpleResult.Processing
            }
            SimpleGradientButton(onClicked = {
                coroutineScope.launch {
                    if(yogaAsanaState.timeLeft > 0) {
                        if(yogaAsanaState.isPaused) {
                            viewModel.resumeAsana()
                        } else {
                            viewModel.pauseAsana()
                        }
                    } else {
                        if (canGoNext) {
                            viewModel.endAsana()
                            viewModel.endAsanaLiveData.observe(lifecycleOwner) {
                                if (it is SimpleResult.Success) {
                                    viewModel.setSessionAndAsanaIndex(yogaSessionIndex, yogaAsanaIndex + 1)
                                }
                            }
                        } else {
                            viewModel.endCurrentWorkout()
                            viewModel.endCurrentWorkoutLiveData.observe(lifecycleOwner) {
                                if (it is SimpleResult.Success) {
                                    onNavigateSessionsScreen()
                                }
                            }
                        }
                    }
                }

            },
                modifier = Modifier, 16.dp, nextButtonEnabled) {
                Row {
                    if(yogaAsanaState.timeLeft > 0) {
                        if(yogaAsanaState.isPaused) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_play_arrow_24_white),
                                contentDescription = "play",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.yoga_asana_button_resume),
                                style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_pause_24_white),
                                contentDescription = "pause",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.yoga_asana_button_pause),
                                style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                            )
                        }
                    } else {
                        if(canGoNext) {
                            Text(
                                stringResource(R.string.yoga_asana_button_next),
                                style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                            )
                        } else {
                            Text(
                                stringResource(R.string.yoga_asana_button_finish),
                                style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                            )
                        }
                    }
                }
            }
        }

        val previousModifier = if(canGoPrevious) {
            Modifier.clickable {

            }
        } else {
            Modifier.alpha(0.6F)
        }
        Row(Modifier.padding(32.dp)) {
            Row(previousModifier) {
                Image(
                    painter = painterResource(id = R.drawable.skip_next_previous),
                    contentDescription = "previous",
                    Modifier
                        .rotate(180F),
                )
                Text(stringResource(R.string.yoga_asana_button_previous))
            }
            Spacer(modifier = Modifier.weight(1f))
            val skipResult = viewModel.skipLiveData.observeAsState().value
            val canSkip = (skipResult !is SimpleResult.Processing) and (yogaAsanaState.timeLeft > 0)
            val skipClickModifier = if(canSkip) {
                Modifier.clickable {
//                    if(canGoNext) {
                        viewModel.skipAsana()

                        val observer = object: Observer<SimpleResult<Unit>> {
                            override fun onChanged(value: SimpleResult<Unit>) {
                                if (value is SimpleResult.Success && canGoNext) {
                                    viewModel.setSessionAndAsanaIndex(yogaSessionIndex, yogaAsanaIndex + 1)
                                    viewModel.skipLiveData.removeObserver(this)
                                }
                            }
//                        }
                    }
                    viewModel.skipLiveData.observe(lifecycleOwner, observer)
                }
            } else {
                Modifier
            }
            Row(skipClickModifier) {
                Image(
                    painter = painterResource(id = R.drawable.skip_next_previous),
                    contentDescription = "skip",
                )
                Text(stringResource(R.string.yoga_asana_button_skip))
            }
        }
    }

}

@Composable
fun SimpleYogaSessionItem(
//    photoRawResId: Int,
    index: Int,
    sessionTemplate: YogaSessionTemplate,
    onSessionSelected: (index: Int) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .height(168.dp)
            .clickable {
                onSessionSelected(index)
            }
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(sessionTemplate.sessionName,
            style = MaterialTheme.typography.h4.copy(color = Color.White)
        )
    }
}

@Composable
fun SimpleYogaAsanaItem(
    yogaAsana: YogaSessionTemplate.YogaAsanaTemplate,
    onViewTutorial: (asanaIdentifier: String) -> Unit,
    modifier: Modifier
) {
    Row(
        Modifier
            .clickable {
                onViewTutorial(yogaAsana.identifier)
            }
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 32.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier
            .size(96.dp)
            .background(Color.Yellow))
        Spacer(modifier = Modifier.width(16.dp))
        val context = LocalContext.current
        val asanaTitleMap = (context.applicationContext as FitnessApp).asanaIdentifierTitles
        val title = asanaTitleMap[yogaAsana.identifier]?._default ?: "Unknown"
        Text(title,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
    }
}