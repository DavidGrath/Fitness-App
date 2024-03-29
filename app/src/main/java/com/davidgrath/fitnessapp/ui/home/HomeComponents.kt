package com.davidgrath.fitnessapp.ui.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.navigateSingleTopTo
import com.davidgrath.fitnessapp.ui.cycling.CyclingActivity
import com.davidgrath.fitnessapp.ui.cycling.CyclingViewModel
import com.davidgrath.fitnessapp.ui.cycling.cyclingNavGraph
import com.davidgrath.fitnessapp.ui.gym.GymActivity
import com.davidgrath.fitnessapp.ui.gym.GymViewModel
import com.davidgrath.fitnessapp.ui.gym.gymNavGraph
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModel
import com.davidgrath.fitnessapp.ui.profile.ProfileScreen
import com.davidgrath.fitnessapp.ui.profile.ProfileViewModel
import com.davidgrath.fitnessapp.ui.running.RunningActivity
import com.davidgrath.fitnessapp.ui.running.RunningViewModel
import com.davidgrath.fitnessapp.ui.running.runningNavGraph
import com.davidgrath.fitnessapp.ui.settings.PrivacyPolicyScreen
import com.davidgrath.fitnessapp.ui.settings.SettingsScreen
import com.davidgrath.fitnessapp.ui.settings.SettingsViewModel
import com.davidgrath.fitnessapp.ui.settings.TermsAndConditionsScreen
import com.davidgrath.fitnessapp.ui.settings.settingsNavGraph
import com.davidgrath.fitnessapp.ui.swimming.SwimmingActivity
import com.davidgrath.fitnessapp.ui.swimming.SwimmingViewModel
import com.davidgrath.fitnessapp.ui.swimming.swimmingNavGraph
import com.davidgrath.fitnessapp.ui.walking.WalkingActivity
import com.davidgrath.fitnessapp.ui.walking.WalkingViewModel
import com.davidgrath.fitnessapp.ui.walking.walkingNavGraph
import com.davidgrath.fitnessapp.ui.yoga.YogaActivity
import com.davidgrath.fitnessapp.ui.yoga.YogaViewModel
import com.davidgrath.fitnessapp.ui.yoga.yogaNavGraph
import com.davidgrath.fitnessapp.util.SimpleResult


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    profileViewModel: ProfileViewModel,
    runningViewModel: RunningViewModel,
    walkingViewModel: WalkingViewModel,
    swimmingViewModel: SwimmingViewModel,
    cyclingViewModel: CyclingViewModel,
    gymViewModel: GymViewModel,
    yogaViewModel: YogaViewModel,
    settingsViewModel: SettingsViewModel
) {

    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavBar(navController)
        }
    ) { padding ->
        HomeNavHost(
            navController, homeViewModel,
            onboardingViewModel, profileViewModel,
            runningViewModel, walkingViewModel, swimmingViewModel,
            cyclingViewModel, gymViewModel, yogaViewModel, settingsViewModel,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun ChooseActivityScreen(
    homeViewModel: HomeViewModel,
    onNavigateOngoingWorkout: (String) -> Unit,
    onActivityChosen: (BasicNavScreen) -> Unit) {

    val chooseActivityScreenState = homeViewModel.chooseActivityStateLiveData.observeAsState().value?:HomeViewModel.ChooseActivityScreenState()

    LaunchedEffect(chooseActivityScreenState) {
        Log.d("HOME", chooseActivityScreenState.toString())
        if(!chooseActivityScreenState.hasReadOngoingWorkout) {
//            homeViewModel.getCurrentWorkout()
            if(chooseActivityScreenState.ongoingWorkout.isNotBlank()) {
                onNavigateOngoingWorkout(chooseActivityScreenState.ongoingWorkout)
                homeViewModel.setHasReadOngoingWorkout()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(R.string.home_header), style = MaterialTheme.typography.h4)
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            val context = LocalContext.current
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.swim, stringResource(R.string.home_label_swimming)) {
                    onActivityChosen(BasicNavScreen.SwimmingDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.run_fast, stringResource(R.string.home_label_running)) {
                    onActivityChosen(BasicNavScreen.RunningDashboardNav)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.shoe_sneaker, stringResource(R.string.home_label_walking)) {
                    onActivityChosen(BasicNavScreen.WalkingDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.bike, stringResource(R.string.home_label_cycling)) {
                    onActivityChosen(BasicNavScreen.CyclingDashboardNav)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.weight_lifter, stringResource(R.string.home_label_gym)) {
                    onActivityChosen(BasicNavScreen.GymDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.yoga, stringResource(R.string.home_label_yoga)) {
                    onActivityChosen(BasicNavScreen.YogaDashboardNav)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SimpleTile(
    iconResourceId: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(156.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        onClick = onClick,
        content = {
            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = iconResourceId), contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = title, color = MaterialTheme.colors.primary, fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun NavBar(
    navController: NavHostController,
) {
    BottomNavigation(
        backgroundColor = Color.White
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        Log.d("ROUTE", currentRoute.toString())
        //TODO Trying to implement per-section backstack. Continue later
        /*val (recentHomeRoute, setRecentHomeRoute) = rememberSaveable {
            mutableStateOf(BasicNavScreen.ChooseActivityNav.path)
        }
        val (recentProfileRoute, setRecentProfileRoute) = rememberSaveable {
            mutableStateOf(BasicNavScreen.ProfileNav.path)
        }
        val (recentSettingsRoute, setRecentSettingsRoute) = rememberSaveable {
            mutableStateOf(BasicNavScreen.SettingsNav.path)
        }*/
        val parts = currentRoute?.split("/")
        val root = parts?.get(0)?:""
        /*when(root) {
            BasicNavScreen.ChooseActivityNav.path -> {
                setRecentHomeRoute(currentRoute!!)
            }
            BasicNavScreen.ProfileNav.path -> {
                setRecentProfileRoute(currentRoute!!)
            }
            BasicNavScreen.SettingsNav.path -> {
                setRecentSettingsRoute(currentRoute!!)
            }
        }*/

        BottomNavigationItem(
            selected = root.equals(BasicNavScreen.ChooseActivityNav.path, true),
            onClick = { navController.navigate(BasicNavScreen.ChooseActivityNav.path) },
            icon = {
                Icon(painter = painterResource(id = R.drawable.home), contentDescription = "home")
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.Gray
        )
        BottomNavigationItem(
            selected = root.equals(BasicNavScreen.ProfileNav.path, true),
            onClick = { navController.navigateSingleTopTo(BasicNavScreen.ProfileNav.path) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.account),
                    contentDescription = "profile"
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.Gray
        )
        BottomNavigationItem(
            selected = root.equals(BasicNavScreen.SettingsNav.path, true),
            onClick = { navController.navigateSingleTopTo(BasicNavScreen.SettingsNav.path) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "settings"
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.Gray
        )
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    profileViewModel: ProfileViewModel,
    runningViewModel: RunningViewModel,
    walkingViewModel: WalkingViewModel,
    swimmingViewModel: SwimmingViewModel,
    cyclingViewModel: CyclingViewModel,
    gymViewModel: GymViewModel,
    yogaViewModel: YogaViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BasicNavScreen.ChooseActivityNav.path,
        modifier
    ) {
        composable(route = BasicNavScreen.ChooseActivityNav.path) {
            ChooseActivityScreen(homeViewModel, { workoutType ->
                when(workoutType) {
                    "CYCLING" -> navController.navigate(BasicNavScreen.CyclingWorkoutNav.path)
                    "WALKING" -> navController.navigate(BasicNavScreen.WalkingWorkoutNav.path)
                    "RUNNING" -> navController.navigate(BasicNavScreen.RunningWorkoutNav.path)
                    "SWIMMING" -> navController.navigate(BasicNavScreen.SwimmingWorkoutNav.path)
//                    "GYM" -> navController.navigate(BasicNavScreen.GymSetNav.getPathWithArgs())
//                    "YOGA" -> navController.navigate(BasicNavScreen.YogaAsanaNav.getPathWithArgs())
                }
            }) {
                navController.navigate(it.path)
            }
        }

        runningNavGraph(navController, runningViewModel)
        walkingNavGraph(navController, walkingViewModel)
        cyclingNavGraph(navController, cyclingViewModel)
        swimmingNavGraph(navController, swimmingViewModel)
        gymNavGraph(navController, gymViewModel)
        yogaNavGraph(navController, yogaViewModel)
        composable(route = BasicNavScreen.ProfileNav.path) {
            ProfileScreen(
                profileViewModel,
                {
                    navController.popBackStack()
                }
            )
        }
        settingsNavGraph(navController, settingsViewModel)
    }

}