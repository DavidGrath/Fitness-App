package com.davidgrath.fitnessapp.ui.home

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.UserDataRepositoryImpl
import com.davidgrath.fitnessapp.ui.FitnessAppTheme
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModel
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModelFactory

class HomeActivity : ComponentActivity() {

    lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        val userDataRepository = UserDataRepositoryImpl(this)
        viewModel = ViewModelProvider(this, OnboardingViewModelFactory(userDataRepository)).get(OnboardingViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContent {
            FitnessAppTheme {
                HomeScreen(viewModel)
            }
        }
    }
}