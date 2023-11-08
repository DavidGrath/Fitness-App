package com.davidgrath.fitnessapp.ui.gym

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.ui.FitnessAppTheme

class GymActivity : ComponentActivity() {

    lateinit var viewModel: GymViewModel
    private var binder: FitnessService.FitnessBinder? = null

    private val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FitnessService.FitnessBinder?
            viewModel.fitnessService = binder
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, FitnessService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, servConn, BIND_AUTO_CREATE)

        val gymRepository = (application as FitnessApp).gymRepository
        viewModel = ViewModelProvider(this, GymViewModelFactory(gymRepository)).get(
            GymViewModel::class.java)
        setContent {
            FitnessAppTheme {
//                GymScreen(viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(servConn)
    }

}