package com.davidgrath.fitnessapp.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.UserDataRepositoryImpl
import com.davidgrath.fitnessapp.ui.FitnessAppTheme
import com.davidgrath.fitnessapp.ui.home.HomeActivity
import com.davidgrath.fitnessapp.util.SimpleResult

class OnboardingActivity : ComponentActivity() {

    lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDataRepository = UserDataRepositoryImpl(this)
        viewModel = ViewModelProvider(this, OnboardingViewModelFactory(userDataRepository)).get(OnboardingViewModel::class.java)
        viewModel.submitLiveData.observe(this) { result ->
            when(result) {
                is SimpleResult.Processing -> {

                }
                is SimpleResult.Success -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is SimpleResult.Failure -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        setContent {
            FitnessAppTheme {
                val screenState = viewModel.screenStateLiveData.observeAsState()

                val screenStateValue = screenState.value?: OnboardingScreenState()
                Log.d("SCRNSTA", screenStateValue.toString())
                OnboardingScreen(
                    screenState = screenStateValue,
                    stages = viewModel.getOnboardingStages(),
                    onFirstNameChange = viewModel::setFirstName,
                    onLastNameChange = viewModel::setLastName,
                    onEmailChange = viewModel::setEmail,
                    setGender = viewModel::setGender,
                    setHeight = { height -> viewModel.setHeight(height, screenStateValue.heightUnit)},
                    setHeightUnit = { heightUnit -> viewModel.setHeight(screenStateValue.height, heightUnit)},
                    setDay = { day -> viewModel.setBirthDate(day, screenState.value?.birthDateMonth?:1, screenState.value?.birthDateYear?:1984)},
                    setMonth = { month -> viewModel.setBirthDate(screenState.value?.birthDateDay?:1, month, screenState.value?.birthDateYear?:1984)},
                    setYear = { year -> viewModel.setBirthDate(screenState.value?.birthDateDay?:1, screenState.value?.birthDateMonth?:1, year)},
                    setWeight = { weight -> viewModel.setWeight(weight, screenStateValue.weightUnit)},
                    setWeightUnit = { weightUnit -> viewModel.setWeight(screenStateValue.weight, weightUnit)},
                    validateNameAndEmail = viewModel::validateNameAndEmail,
                    validateGender = viewModel::validateGender,
                    goNext = viewModel::goToNextPage,
                    goPrevious = viewModel::goToPreviousPage
                )
            }
        }
    }
}