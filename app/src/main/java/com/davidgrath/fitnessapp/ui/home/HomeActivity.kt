package com.davidgrath.fitnessapp.ui.home

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.UserDataRepositoryImpl
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.ui.FitnessAppTheme
import com.davidgrath.fitnessapp.ui.cycling.CyclingViewModel
import com.davidgrath.fitnessapp.ui.cycling.CyclingViewModelFactory
import com.davidgrath.fitnessapp.ui.gym.GymViewModel
import com.davidgrath.fitnessapp.ui.gym.GymViewModelFactory
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModel
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModelFactory
import com.davidgrath.fitnessapp.ui.running.RunningViewModel
import com.davidgrath.fitnessapp.ui.running.RunningViewModelFactory
import com.davidgrath.fitnessapp.ui.swimming.SwimmingViewModel
import com.davidgrath.fitnessapp.ui.swimming.SwimmingViewModelFactory
import com.davidgrath.fitnessapp.ui.walking.WalkingViewModel
import com.davidgrath.fitnessapp.ui.walking.WalkingViewModelFactory
import com.davidgrath.fitnessapp.ui.yoga.YogaViewModel
import com.davidgrath.fitnessapp.ui.yoga.YogaViewModelFactory

class HomeActivity : ComponentActivity() {

    lateinit var homeViewModel: HomeViewModel
    lateinit var onboardingViewModel: OnboardingViewModel
    lateinit var runningViewModel: RunningViewModel
    lateinit var walkingViewModel: WalkingViewModel
    lateinit var swimmingViewModel: SwimmingViewModel
    lateinit var cyclingViewModel: CyclingViewModel
    lateinit var gymViewModel: GymViewModel
    lateinit var yogaViewModel: YogaViewModel

    private var binder: FitnessService.FitnessBinder? = null
    var isBoundToService = false

    private val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBoundToService = true
            binder = service as FitnessService.FitnessBinder?
            homeViewModel.fitnessService = binder
            runningViewModel.fitnessService = binder
            walkingViewModel.fitnessService = binder
            swimmingViewModel.fitnessService = binder
            cyclingViewModel.fitnessService = binder
            gymViewModel.fitnessService = binder
            yogaViewModel.fitnessService = binder
            setContent { //TODO This just looks wrong, but it seems onServiceConnected only gets
                // called after onCreate returns, so work with it for now
                FitnessAppTheme {
                    HomeScreen(homeViewModel,
                        onboardingViewModel, runningViewModel, walkingViewModel, swimmingViewModel,
                        cyclingViewModel, gymViewModel, yogaViewModel
                    )
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBoundToService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)
        val userDataRepository = UserDataRepositoryImpl(this)
        onboardingViewModel = ViewModelProvider(this, OnboardingViewModelFactory(userDataRepository)).get(OnboardingViewModel::class.java)

        val runningRepository = (application as FitnessApp).runningRepository
        val walkingRepository = (application as FitnessApp).walkingRepository
        val swimmingRepository = (application as FitnessApp).swimmingRepository
        val cyclingRepository = (application as FitnessApp).cyclingRepository
        val gymRepository = (application as FitnessApp).gymRepository
        val yogaRepository = (application as FitnessApp).yogaRepository

        runningViewModel = ViewModelProvider(this, RunningViewModelFactory(runningRepository)).get(
            RunningViewModel::class.java)
        walkingViewModel = ViewModelProvider(this, WalkingViewModelFactory(walkingRepository)).get(
            WalkingViewModel::class.java)
        swimmingViewModel = ViewModelProvider(this, SwimmingViewModelFactory(swimmingRepository)).get(
            SwimmingViewModel::class.java)
        cyclingViewModel = ViewModelProvider(this, CyclingViewModelFactory(cyclingRepository)).get(
            CyclingViewModel::class.java)
        gymViewModel = ViewModelProvider(this, GymViewModelFactory(gymRepository)).get(
            GymViewModel::class.java)
        yogaViewModel = ViewModelProvider(this, YogaViewModelFactory(yogaRepository)).get(
            YogaViewModel::class.java)

        val serviceIntent = Intent(this, FitnessService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, servConn, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if(isBoundToService) {
            unbindService(servConn)
            isBoundToService = false
        }
    }
}