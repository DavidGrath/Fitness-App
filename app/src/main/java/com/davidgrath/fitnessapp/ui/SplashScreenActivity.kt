package com.davidgrath.fitnessapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.davidgrath.fitnessapp.MainActivity
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.home.HomeActivity
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingActivity
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles

class SplashScreenActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, MODE_PRIVATE)
        val userToken = preferences.getString(PreferencesTitles.EMAIL, null)?:""
        val isAuth = userToken.isNotBlank()
        val activityIntent: Intent
        if(isAuth) {
            activityIntent = Intent(this, HomeActivity::class.java)
        } else {
            activityIntent = Intent(this, OnboardingActivity::class.java)
        }
        startActivity(activityIntent)
        finish()
    }
}