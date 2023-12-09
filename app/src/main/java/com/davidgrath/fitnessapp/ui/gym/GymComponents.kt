package com.davidgrath.fitnessapp.ui.gym

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.data.entities.GymSetTutorial
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.CalendarComponent
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.SimpleGradientButton
import com.davidgrath.fitnessapp.ui.components.WeekHistoryComponent
import com.davidgrath.fitnessapp.ui.components.WelcomeBanner
import com.davidgrath.fitnessapp.ui.components.WorkoutSummaryComponent
import com.davidgrath.fitnessapp.util.Constants.BULLET
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.setIdentifierToIconMap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import android.graphics.Color as AndroidColor

/*@Composable
fun GymScreen(
    viewModel: GymViewModel
) {

    val navController = rememberNavController()
    Scaffold { padding ->
        GymNavHost(
            navController,
            viewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}*/

fun NavGraphBuilder.gymNavGraph(navController: NavHostController, gymViewModel: GymViewModel) {
    navigation(startDestination = BasicNavScreen.GymDashboardNav.allButLastSegment(),
        route = BasicNavScreen.GymDashboardNav.lastSegment()) {

        composable(route = BasicNavScreen.GymDashboardNav.path) {
            GymDashboardScreen(
                gymViewModel,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(BasicNavScreen.GymHistoryNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.GymRoutineListNav.path)
                },
                { r, s ->
                    navController.navigate(BasicNavScreen.GymSetNav.getPathWithArgs(r, s))
                }
            )
        }
        composable(route = BasicNavScreen.GymHistoryNav.path) {
            GymHistoryScreen(
                gymViewModel,
                {
                    navController.popBackStack()
                })
        }
        composable(route = BasicNavScreen.GymRoutineListNav.path) {
            GymRoutineListScreen(
                {
                    navController.popBackStack()
                },
                {
                    val pathWithArgs = BasicNavScreen.GymRoutineSetsNav.getPathWithArgs(it)
                    navController.navigate(pathWithArgs)
                })
        }
        composable(route = BasicNavScreen.GymRoutineSetsNav.path,
            arguments = listOf(navArgument("routineId") {type = NavType.IntType})
        ) { backStackEntry ->
            val routineIndex = backStackEntry.arguments!!.getInt("routineId")
            GymRoutineSetsScreen(
                routineIndex,
                gymViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateFirstSet = { i ->
                    val pathWithArgs = BasicNavScreen.GymSetNav.getPathWithArgs(routineIndex, 0)
                    navController.navigate(pathWithArgs)
                }
            )
        }
        composable(route = BasicNavScreen.GymSetNav.path,
            arguments = listOf(
                navArgument("routineId") {type = NavType.IntType},
                navArgument("setId") {type = NavType.IntType},
            )
        ) { backStackEntry ->

            val routineIndex = backStackEntry.arguments!!.getInt("routineId")
            val setIndex = backStackEntry.arguments!!.getInt("setId")

            GymSetScreen(
                gymRoutineIndex = routineIndex,
                gymSetIndex = setIndex,
                viewModel = gymViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                    //TODO Cancel workout
                    //If I implement cancelling the current workout by using the up arrow, then I'd have
                    //to also add a custom BackHandler for a consistent experience
                },
                onNavigateToSet = { gymRoutineIndex, gymSetIndex ->
                    val pathWithArgs = BasicNavScreen.GymSetNav.getPathWithArgs(gymRoutineIndex, gymSetIndex)
                    navController.popBackStack(navController.currentBackStackEntry!!.destination.route!!, true)
                    navController.navigate(pathWithArgs)
                },
                onNavigateRoutinesScreen = {
                    navController.navigate(BasicNavScreen.GymRoutineListNav.path) {
                        launchSingleTop = true
                        popUpTo(BasicNavScreen.GymRoutineListNav.path)
                    }
                }
            )
        }

    }
}

@Composable
fun GymDashboardScreen(
    viewModel: GymViewModel,
    onNavigateBack: () -> Unit,
    onNavigateDetailedHistory: () -> Unit,
    onNavigateRoutinesScreen: () -> Unit,
    onNavigateOngoingWorkout: (routineIndex: Int, setIndex: Int) -> Unit
) {
    val (isInitial, setIsInitial) = rememberSaveable {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = null) {
        setIsInitial(false)
    }

    val gymScreensState = viewModel.gymScreenStateLiveData.observeAsState().value?: GymViewModel.GymScreensState()
    LaunchedEffect(gymScreensState) {
        if(gymScreensState.isDoingGym && isInitial) {
            val routineIndex = if(gymScreensState.currentRoutineIndex >= 0) {
                gymScreensState.currentRoutineIndex
            } else {
                0
            }
            val setIndex = if(gymScreensState.currentSetIndex >= 0) {
                gymScreensState.currentSetIndex
            } else {
                0
            }
            onNavigateOngoingWorkout(routineIndex, setIndex)
        }
    }

    val weekWorkouts = gymScreensState.pastWeekWorkouts
    val workoutSummary = gymScreensState.workoutSummary


    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.gym_header), expanded = true, onBackClicked =  onNavigateBack)
        Spacer(Modifier.height(4.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
            WelcomeBanner(fitnessApp.resourceProvider.provideWorkoutBanner("gym"),
                bannerButtonText = stringResource(R.string.gym_banner_button_text), onNavigateRoutinesScreen)
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
                stringResource(R.string.gym_workout_summary_show_more),
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
fun GymHistoryScreen(
    viewModel: GymViewModel,
    onNavigateBack: () -> Unit
) {
    val gymScreensState = viewModel.gymScreenStateLiveData.observeAsState().value?:GymViewModel.GymScreensState()
    val workouts = gymScreensState.pastWorkouts
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        SimpleAppBar(title = stringResource(R.string.gym_header), expanded = false, onBackClicked = onNavigateBack)
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
fun GymRoutineListScreen(
    onNavigateBack: () -> Unit,
    onRoutineSelected: (index: Int) -> Unit
) {
    val context = LocalContext.current
    val application = (context.applicationContext as FitnessApp)
    val routines = application.defaultGymRoutineTemplates

    Column(Modifier.fillMaxSize()) {
        SimpleAppBar(title = stringResource(R.string.gym_header), expanded = true, onBackClicked = onNavigateBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            itemsIndexed(routines, { index, item -> index }) { index, item ->

                val fitnessApp = (LocalContext.current.applicationContext as FitnessApp)
                SimpleGymRoutineItem(
                    fitnessApp.resourceProvider.provideGymRoutineImage(item.routineName.lowercase()),
                    index = index,
                    routineTemplate = item,
                    onRoutineSelected = onRoutineSelected,
                    modifier = Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun GymRoutineSetsScreen(
    selectedRoutineIndex: Int,
    viewModel: GymViewModel,
    onNavigateBack: () -> Unit,
    onNavigateFirstSet: (routineIndex: Int) -> Unit
) {
    val context = LocalContext.current
    val routineTemplates = (context.applicationContext as FitnessApp).defaultGymRoutineTemplates
    val setTutorials = (context.applicationContext as FitnessApp).gymSetTutorials
    val setTitleMap = (context.applicationContext as FitnessApp).setIdentifierTitles
    val routineTemplate = routineTemplates[selectedRoutineIndex]
    val coroutineScope = rememberCoroutineScope()
    val (currentTutorial, setCurrentTutorial) = remember {
        mutableStateOf<GymSetTutorial?>(null)
    }
    val (currentSetIdentifier, setCurrentSetIdentifier) = remember {
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
                val title = setTitleMap[currentSetIdentifier]?._default ?: "Unknown"
                if (currentTutorial.youtubeVideoId != null) {
                    Text(
                        stringResource(R.string.gym_tutotial_heading_video),
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(8.dp))
                    if(!(tempVideoDetails?.thumbnailUrl.isNullOrEmpty())) {
                        GlideImage(model = tempVideoDetails?.thumbnailUrl, contentDescription = "yt thumbnail",
                        Modifier.fillMaxWidth()
                            .clickable {
                                val viewIntent = Intent(Intent.ACTION_VIEW)
                                val uri = "https://youtu.be/${currentTutorial.youtubeVideoId}"
                                viewIntent.data = Uri.parse(uri)
                                context.startActivity(viewIntent)
                            },
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
                    Text(BULLET + " " + it,
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
                    Text(stringResource(R.string.gym_tutorial_button_close), style = MaterialTheme.typography.button.copy(Color.White))
                }
            }
        }
    ) {
        Column() {
            SimpleAppBar(title = "", expanded = false, onBackClicked = onNavigateBack)
            Box {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val fitnessApp = context.applicationContext as FitnessApp
                        val key = routineTemplate.routineName.lowercase()
                        GlideImage(
                            model = fitnessApp.resourceProvider.provideGymRoutineImage(key),
                            contentDescription = "",
                            loading = placeholder(ColorDrawable(AndroidColor.YELLOW)),
                            failure = placeholder(ColorDrawable(AndroidColor.RED)),
                            colorFilter = ColorFilter.tint(Color.Gray, BlendMode.Multiply),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .zIndex(-1f),
                        ) {
                            it.centerCrop()
                        }
                        Text(
                            routineTemplate.routineName,
                            style = MaterialTheme.typography.h4.copy(color = Color.White)
                        )
                    }
                    LazyColumn(contentPadding = PaddingValues(bottom = 48.dp)) {
                        itemsIndexed(
                            routineTemplate.sets,
                            { index, item -> index }) { index, item ->
                            SimpleGymSetItem(
                                gymSet = item,
                                onViewTutorial = {
                                    val tutorial = setTutorials[it]!!
                                    setCurrentTutorial(tutorial)
                                    setCurrentSetIdentifier(it)
                                    tutorial.youtubeVideoId?.let {
                                        viewModel.tempFetchVideoDetails(it)
                                    }
                                    coroutineScope.launch {
                                        sheetState.show()
                                    }
                                }, modifier = Modifier
                            )
                            if (index < routineTemplate.sets.size - 1) {
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
                            viewModel.addWorkoutLiveData.observe(lifecycleOwner) {
                                if(it is SimpleResult.Success) {
                                    onNavigateFirstSet(selectedRoutineIndex)
                                }
                            }
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
                        stringResource(R.string.gym_routine_sets_button_start_workout),
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GymSetScreen(
    gymRoutineIndex: Int,
    gymSetIndex: Int,
    viewModel: GymViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSet: (gymRoutineIndex: Int, gymSetIndex: Int) -> Unit,
    onNavigateRoutinesScreen: () -> Unit
) {
    val context = LocalContext.current
    val routineTemplates = (context.applicationContext as FitnessApp).defaultGymRoutineTemplates
    val setTitleMap = (context.applicationContext as FitnessApp).setIdentifierTitles

    val currentRoutine = routineTemplates[gymRoutineIndex]
    val currentGymSet = currentRoutine.sets[gymSetIndex]
    val routineTitle = currentRoutine.routineName
    val setTitle = setTitleMap[currentGymSet.identifier]?._default ?: "Unknown"
    val gymScreensState = viewModel.gymScreenStateLiveData.observeAsState().value?: GymViewModel.GymScreensState()
    val repCount = gymScreensState.currentRepCount
    /*val (repCount, setRepCount) = rememberSaveable {
        mutableStateOf(0)
    }
    val incrementRepCount = { amount: Int ->
            Log.d("GymSetScreen", "repCount: ${repCount}, amount: $amount")
            if (repCount + amount <= 99) {
                setRepCount(repCount + amount)
            } else {
                setRepCount(99)
            }
    }
    val decrementRepCount = { amount: Int ->
            Log.d("GymSetScreen", "repCount: ${repCount}, amount: $amount")
            if (repCount - amount >= 0) {
                setRepCount(repCount - amount)
            } else {
                setRepCount(0)
            }
    }*/

    LaunchedEffect(key1 = null) {
        if(viewModel.setProgress < gymSetIndex) {
            viewModel.startSet(gymSetIndex, currentGymSet.identifier)
        }
    }

    LaunchedEffect(gymScreensState) {
        //I use -1 as the default value, so take note for any possible side-effects
        if(gymScreensState.currentRoutineIndex == gymRoutineIndex &&
            gymScreensState.currentSetIndex >= 0 &&
            gymScreensState.currentSetIndex != gymSetIndex) {
            onNavigateToSet(gymScreensState.currentRoutineIndex, gymScreensState.currentSetIndex)
        }
    }

    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        SimpleAppBar(title = routineTitle, expanded = false, textColor = Color.Black, onBackClicked = onNavigateBack)
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            val iconId = setIdentifierToIconMap[currentGymSet.identifier]
            if (iconId == null) {
                Box(
                    modifier = Modifier
                        .size(144.dp)
                        .background(Color.Yellow)
                )
            } else {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    Modifier.size(144.dp),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(setTitle, style = MaterialTheme.typography.h5)
        }
        Divider(color = Color.Gray.copy(0.5f))
        val canGoPrevious = gymSetIndex > 0
        val canGoNext = gymSetIndex < currentRoutine.sets.size - 1
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                val counterText = String.format("%02d", repCount)
                Text(counterText, style = MaterialTheme.typography.h3)
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.plus_circle),
                        contentDescription = "increment",
                        Modifier.shortAndLongPress({
                            viewModel.incrementRepCount(1)
                        }, {
                            viewModel.incrementRepCount(3)
                        })

                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.minus_circle),
                        contentDescription = "decrement",
                        Modifier.shortAndLongPress({
                            viewModel.decrementRepCount(1)
                        }, {
                            viewModel.decrementRepCount(3)
                        })
                    )
                }
            }
            val endSetResult = viewModel.endSetLiveData.observeAsState().value
            val endWorkoutResult = viewModel.endCurrentWorkoutLiveData.observeAsState().value
            val nextButtonEnabled = if (canGoNext) {
                endSetResult !is SimpleResult.Processing
            } else {
                endWorkoutResult !is SimpleResult.Processing
            }
            SimpleGradientButton(onClicked = {
                coroutineScope.launch {
                    if (canGoNext) {
                        viewModel.endSet(repCount)
                        viewModel.endSetLiveData.observe(lifecycleOwner) {
                            if(it is SimpleResult.Success) {
                                viewModel.setRoutineAndSetIndex(gymRoutineIndex, gymSetIndex + 1)
                            }
                        }
                    } else {
                        viewModel.endCurrentWorkout(repCount)
                        viewModel.endCurrentWorkoutLiveData.observe(lifecycleOwner) {
                            if(it is SimpleResult.Success) {
                                onNavigateRoutinesScreen()
                            }
                        }
                    }

                }

            },
                modifier = Modifier, 16.dp, nextButtonEnabled) {
                Row {
                    if(canGoNext) {
                        Text(
                            stringResource(R.string.gym_set_button_next),
                            style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.arrow_right),
                            contentDescription = "next",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    } else {
                        Text(
                            stringResource(R.string.gym_set_button_finish),
                            style = MaterialTheme.typography.h5.copy(color = Color.White, 18.sp)
                        )
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
                Text(stringResource(R.string.gym_set_button_previous))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(Modifier.clickable {
                if (canGoNext) {
                    viewModel.endSet(0)
                    viewModel.endSetLiveData.observe(lifecycleOwner) {
                        if(it is SimpleResult.Success) {
                            viewModel.setRoutineAndSetIndex(gymRoutineIndex, gymSetIndex + 1)
                        }
                    }
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.skip_next_previous),
                    contentDescription = "skip",
                )
                Text(stringResource(R.string.gym_set_button_skip))
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SimpleGymRoutineItem(
    photoResource: Any,
    index: Int,
    routineTemplate: GymRoutineTemplate,
    onRoutineSelected: (index: Int) -> Unit,
    modifier: Modifier
) {
    Box(Modifier
        .fillMaxWidth()
        .height(168.dp)
        .clickable {
            onRoutineSelected(index)
        }
        .then(modifier)) {
        GlideImage(
            model = photoResource,
            contentDescription = "",
            loading = placeholder(ColorDrawable(AndroidColor.YELLOW)),
            failure = placeholder(ColorDrawable(AndroidColor.RED)),
            colorFilter = ColorFilter.tint(Color.Gray, BlendMode.Multiply),
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp),
        ) {
            it.centerCrop()
        }
        Text(
            routineTemplate.routineName,
            Modifier
                .align(Alignment.Center)
                .zIndex(1f),
            style = MaterialTheme.typography.h4.copy(color = Color.White)
        )
    }
}

@Composable
fun SimpleGymSetItem(
    gymSet: GymRoutineTemplate.GymSetTemplate,
    onViewTutorial: (setIdentifier: String) -> Unit,
    modifier: Modifier
) {
    Row(
        Modifier
            .clickable {
                onViewTutorial(gymSet.identifier)
            }
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 32.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconId = setIdentifierToIconMap[gymSet.identifier]
        if(iconId == null) {
            Box(modifier = Modifier
                .size(96.dp)
                .background(Color.Yellow))
        } else {
            Image(painter = painterResource(id = iconId),
                contentDescription = null,
                Modifier.size(96.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(16.dp))
        val context = LocalContext.current
        val setTitleMap = (context.applicationContext as FitnessApp).setIdentifierTitles
        val title = setTitleMap[gymSet.identifier]?._default ?: "Unknown"
        Text(title,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
    }
}

// Copied and modified from https://stackoverflow.com/questions/75912910/migrating-from-foreachgesture-to-awaiteachgesture
// and androidx.compose.foundation.gestures.TapGestureDetector.kt
fun Modifier.shortAndLongPress(shortPress: (() -> Unit)?, longPress: (() -> Unit)?): Modifier {
    return pointerInput(Unit) {
        coroutineScope {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                try {
                    val u = withTimeout(viewConfiguration.longPressTimeoutMillis) {
                        waitForUpOrCancellation()
                    }
                    if (u != null) {
                        shortPress?.invoke()
                    } else {

                    }
                } catch (e: PointerEventTimeoutCancellationException) {
                    val job = launch {
                        var currentDelay = 400L
                        while (down.pressed) {
                            delay(currentDelay)
                            longPress?.invoke()
                            if (currentDelay - 50L >= 150L) {
                                currentDelay -= 50L
                            }
                        }
                    }
                    waitForUpOrCancellation()
                    job.cancel()
                }
            }
        }

    }
}