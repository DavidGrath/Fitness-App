package com.davidgrath.fitnessapp.ui.yoga

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

class YogaActivity : ComponentActivity() {

    lateinit var viewModel: YogaViewModel
    private var binder: FitnessService.FitnessBinder? = null

    private val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FitnessService.FitnessBinder?
            viewModel.fitnessBinder = binder
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, FitnessService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, servConn, BIND_AUTO_CREATE)

        val yogaRepository = (application as FitnessApp).yogaRepository
        viewModel = ViewModelProvider(this, YogaViewModelFactory(yogaRepository)).get(
            YogaViewModel::class.java)
        setContent {
            FitnessAppTheme {
//                YogaScreen(viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(servConn)
    }
}
