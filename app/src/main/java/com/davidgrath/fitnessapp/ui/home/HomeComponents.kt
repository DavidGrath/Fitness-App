package com.davidgrath.fitnessapp.ui.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.cycling.CyclingActivity
import com.davidgrath.fitnessapp.ui.gym.GymActivity
import com.davidgrath.fitnessapp.ui.profile.ProfileScreen
import com.davidgrath.fitnessapp.ui.running.RunningActivity
import com.davidgrath.fitnessapp.ui.settings.SettingsScreen
import com.davidgrath.fitnessapp.ui.swimming.SwimmingActivity
import com.davidgrath.fitnessapp.ui.walking.WalkingActivity


@Composable
fun HomeScreen() {

    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavBar(navController)
        }
    ) { padding ->
        HomeNavHost(
            navController,
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun ChooseActivityScreen(onActivityChosen: (BasicNavScreen) -> Unit) {
    Column(
        Modifier
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Choose Activity", style = MaterialTheme.typography.h4)
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            val context = LocalContext.current
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.swim, "Swimming") {
                    onActivityChosen(BasicNavScreen.SwimmingDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.run_fast, "Running") {
                    onActivityChosen(BasicNavScreen.RunningDashboardNav)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.shoe_sneaker, "Walking") {
                    onActivityChosen(BasicNavScreen.WalkingDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.bike, "Cycling") {
                    onActivityChosen(BasicNavScreen.CyclingDashboardNav)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SimpleTile(iconResourceId = R.drawable.weight_lifter, "Gym") {
                    onActivityChosen(BasicNavScreen.GymDashboardNav)
                }
                SimpleTile(iconResourceId = R.drawable.yoga, "Yoga") {
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
    BottomNavigation {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        Log.d("ROUTE", currentRoute.toString())
        val parts = currentRoute?.split("/")
        val root = parts?.get(0)?:""
        BottomNavigationItem(
            selected = root.equals("home", true),
            onClick = { navController.navigate(BasicNavScreen.ChooseActivityNav.path) },
            icon = {
                Icon(painter = painterResource(id = R.drawable.home), contentDescription = "home")
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.Gray
        )
        BottomNavigationItem(
            selected = root.equals("profile", true),
            onClick = { navController.navigate(BasicNavScreen.ProfileNav.path) },
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
            selected = root.equals("settings", true),
            onClick = { navController.navigate(BasicNavScreen.SettingsNav.path) },
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
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BasicNavScreen.ChooseActivityNav.path,
        modifier
    ) {
        composable(route = BasicNavScreen.ChooseActivityNav.path) {
            val context = LocalContext.current
            ChooseActivityScreen {
                when(it) {
                    BasicNavScreen.CyclingDashboardNav -> {
                        context.startActivity(Intent(context, CyclingActivity::class.java))
                    }
                    BasicNavScreen.GymDashboardNav -> {
                        context.startActivity(Intent(context, GymActivity::class.java))
                    }
                    BasicNavScreen.RunningDashboardNav -> {
                        context.startActivity(Intent(context, RunningActivity::class.java))
                    }
                    BasicNavScreen.SwimmingDashboardNav -> {
                        context.startActivity(Intent(context, SwimmingActivity::class.java))
                    }
                    BasicNavScreen.WalkingDashboardNav -> {
                        context.startActivity(Intent(context, WalkingActivity::class.java))
                    }
                    BasicNavScreen.YogaDashboardNav -> {}
                    else -> {
                        navController.navigate(it.path)
                    }
                }
            }
        }
        /*composable(route = BasicNavScreen.SwimmingDashboardNav.path) {
            SwimmingDashboard({
                              navController.popBackStack()
            }, {
                navController.navigate(BasicNavScreen.SwimmingHistoryNav.path)
            })
        }
        composable(route = BasicNavScreen.SwimmingHistoryNav.path) {
            SwimmingHistory {
                navController.popBackStack()
            }
        }*/
        composable(route = BasicNavScreen.CyclingDashboardNav.path) {
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .fillMaxSize()
            )
        }
        composable(route = BasicNavScreen.GymDashboardNav.path) {
            Box(
                modifier = Modifier
                    .background(Color.Yellow)
                    .fillMaxSize()
            )
        }
        composable(route = BasicNavScreen.RunningDashboardNav.path) {
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .fillMaxSize()
            )
        }
        composable(route = BasicNavScreen.WalkingDashboardNav.path) {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
            )
        }
        composable(route = BasicNavScreen.YogaDashboardNav.path) {
            Box(
                modifier = Modifier
                    .background(Color.Cyan)
                    .fillMaxSize()
            )
        }
        composable(route = BasicNavScreen.ProfileNav.path) {
            ProfileScreen()
        }
        composable(route = BasicNavScreen.SettingsNav.path) {
            SettingsScreen()
        }
    }

}