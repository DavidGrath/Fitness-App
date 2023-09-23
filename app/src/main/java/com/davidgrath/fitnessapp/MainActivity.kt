package com.davidgrath.fitnessapp

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.davidgrath.fitnessapp.data.CyclingRepository
import com.davidgrath.fitnessapp.data.RunningRepository
import com.davidgrath.fitnessapp.data.SwimmingRepository
import com.davidgrath.fitnessapp.data.WalkingRepository
import com.davidgrath.fitnessapp.data.entities.CyclingLocationData
import com.davidgrath.fitnessapp.data.entities.RunningLocationData
import com.davidgrath.fitnessapp.data.entities.WalkingLocationData
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.ui.home.HomeActivity
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), OnClickListener {

    private var binder: FitnessService.FitnessBinder? = null
    private lateinit var runningRepository: RunningRepository
    private lateinit var walkingRepository: WalkingRepository
    private lateinit var cyclingRepository: CyclingRepository
    private lateinit var swimmingRepository: SwimmingRepository

    val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FitnessService.FitnessBinder?
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button_main_start_running).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_stop_running).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_start_walking).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_stop_walking).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_start_cycling).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_stop_cycling).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_start_swimming).setOnClickListener(this)
        findViewById<Button>(R.id.button_main_stop_swimming).setOnClickListener(this)
        runningRepository = (application as FitnessApp).runningRepository
        walkingRepository = (application as FitnessApp).walkingRepository
        cyclingRepository = (application as FitnessApp).cyclingRepository
        swimmingRepository = (application as FitnessApp).swimmingRepository
        val intent = Intent(this, FitnessService::class.java)
        startService(intent)
        bindService(intent, servConn, BIND_AUTO_CREATE)*/
//        startActivity(Intent(this, GymTestActivity::class.java))
//        finish()
        startActivity(Intent(this, YogaTestActivity::class.java))
        finish()
        /*setContent {
            MainContent()
        }*/

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_CODE_LOCATION -> {
                if(grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {

                        } else {
                            val permissionTitle = "Location Permission Denied"
                            val permissionExplanation = "The app needs location permission in order to record your trip data"
                            Toast.makeText(this, permissionTitle, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            PERMISSION_CODE_NOTIFICATIONS -> {
                if(grantResults.isNotEmpty()) {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {

                        } else {
                            val permissionTitle = "Notification Permission Denied"
                            val permissionExplanation = "The app needs notification permission in order to notify you in the background"
                            Toast.makeText(this, permissionTitle, Toast.LENGTH_SHORT).show()
                        }
                    } else {

                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id) {
                R.id.button_main_start_running -> {
                    val tiramisuPermissionNotGranted =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    val permissionNotGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            tiramisuPermissionNotGranted
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNotGranted) {
                         requestLocationPermissions()
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            requestNotificationPermissions()
//                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                                !powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
//                                val permissionTitle = "Battery Optimization"
//                                val permissionExplanation = "App needs to be exempted from battery optimization"
//                                val dialog = BatteryOptimizationDialogFragment.newInstance(permissionTitle, permissionExplanation)
//                                dialog.show(childFragmentManager, BatteryOptimizationDialogFragment.TAG)
                    } else {

                        val locationRequest = LocationRequest.create()
                        val builder = LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                        val settingsClient = LocationServices.getSettingsClient(this)
                        settingsClient.checkLocationSettings(builder.build())
                            .addOnFailureListener {
                                if(it is ResolvableApiException) {
                                    it.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS)
                                }
                            }
                            .addOnSuccessListener { response ->

                                val dd = binder!!.startRunningWorkout()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ id ->
                                        val adapter = ArrayAdapter<RunningLocationData>(this, android.R.layout.simple_list_item_1)
                                        findViewById<ListView>(R.id.list_view_main).adapter = adapter
                                        val dis = runningRepository.getWorkout(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe {
                                                findViewById<TextView>(R.id.text_view_main_workout).text = it.toString()
                                            }
                                        val disposable = runningRepository.getWorkoutLocationData(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe { locationDataList ->
                                                adapter.clear()
                                                for(locationData in locationDataList) {
                                                    adapter.insert(locationData, adapter.count)

                                                }
                                                adapter.notifyDataSetChanged()
                                            }
                                    }, {

                                    })
                            }.addOnFailureListener {

                            }


                    }
                }
                R.id.button_main_start_walking -> {
                    val tiramisuPermissionNotGranted =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    val permissionNotGranted =
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                tiramisuPermissionNotGranted
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNotGranted) {
                        requestLocationPermissions()
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermissions()
//                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                                !powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
//                                val permissionTitle = "Battery Optimization"
//                                val permissionExplanation = "App needs to be exempted from battery optimization"
//                                val dialog = BatteryOptimizationDialogFragment.newInstance(permissionTitle, permissionExplanation)
//                                dialog.show(childFragmentManager, BatteryOptimizationDialogFragment.TAG)
                    } else {
                        val locationRequest = LocationRequest.create()
                        val builder = LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                        val settingsClient = LocationServices.getSettingsClient(this)
                        settingsClient.checkLocationSettings(builder.build())
                            .addOnFailureListener {
                                if(it is ResolvableApiException) {
                                    it.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS)
                                }
                            }
                            .addOnSuccessListener { response ->

                                val dd = binder!!.startWalkingWorkout()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ id ->
                                        val adapter = ArrayAdapter<WalkingLocationData>(this, android.R.layout.simple_list_item_1)
                                        findViewById<ListView>(R.id.list_view_main).adapter = adapter
                                        val dis = walkingRepository.getWorkout(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe {
                                                findViewById<TextView>(R.id.text_view_main_workout).text = it.toString()
                                            }
                                        val disposable = walkingRepository.getWorkoutLocationData(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe { locationDataList ->
                                                adapter.clear()
                                                for(locationData in locationDataList) {
                                                    adapter.insert(locationData, adapter.count)
                                                }
                                                adapter.notifyDataSetChanged()
                                            }
                                    }, {

                                    })

                            }.addOnFailureListener {

                            }


                    }
                }
                R.id.button_main_start_cycling -> {
                    val tiramisuPermissionNotGranted =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    val permissionNotGranted =
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                tiramisuPermissionNotGranted
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNotGranted) {
                        requestLocationPermissions()
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermissions()
//                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                                !powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
//                                val permissionTitle = "Battery Optimization"
//                                val permissionExplanation = "App needs to be exempted from battery optimization"
//                                val dialog = BatteryOptimizationDialogFragment.newInstance(permissionTitle, permissionExplanation)
//                                dialog.show(childFragmentManager, BatteryOptimizationDialogFragment.TAG)
                    } else {
                        val locationRequest = LocationRequest.create()
                        val builder = LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                        val settingsClient = LocationServices.getSettingsClient(this)
                        settingsClient.checkLocationSettings(builder.build())
                            .addOnFailureListener {
                                if(it is ResolvableApiException) {
                                    it.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS)
                                }
                            }
                            .addOnSuccessListener { response ->
                                val dd = binder!!.startCyclingWorkout()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ id ->
                                        val adapter = ArrayAdapter<CyclingLocationData>(this, android.R.layout.simple_list_item_1)
                                        findViewById<ListView>(R.id.list_view_main).adapter = adapter
                                        val dis = cyclingRepository.getWorkout(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe {
                                                findViewById<TextView>(R.id.text_view_main_workout).text = it.toString()
                                            }
                                        val disposable = cyclingRepository.getWorkoutLocationData(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe { locationDataList ->
                                                adapter.clear()
                                                for(locationData in locationDataList) {
                                                    adapter.insert(locationData, adapter.count)
                                                }
                                                adapter.notifyDataSetChanged()
                                            }
                                    }, {

                                    })
                            }.addOnFailureListener {

                            }


                    }
                }
                R.id.button_main_start_swimming -> {
                    val tiramisuPermissionNotGranted =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    val permissionNotGranted =
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                tiramisuPermissionNotGranted
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNotGranted) {
                        requestLocationPermissions()
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermissions()
//                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                                !powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
//                                val permissionTitle = "Battery Optimization"
//                                val permissionExplanation = "App needs to be exempted from battery optimization"
//                                val dialog = BatteryOptimizationDialogFragment.newInstance(permissionTitle, permissionExplanation)
//                                dialog.show(childFragmentManager, BatteryOptimizationDialogFragment.TAG)
                    } else {
                        val locationRequest = LocationRequest.create()
                        val builder = LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                        val settingsClient = LocationServices.getSettingsClient(this)
                        settingsClient.checkLocationSettings(builder.build())
                            .addOnFailureListener {
                                if(it is ResolvableApiException) {
                                    it.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS)
                                }
                            }
                            .addOnSuccessListener { response ->
                                val dd = binder!!.startSwimmingWorkout()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({id ->
                                        val dis = swimmingRepository.getWorkout(id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe {
                                                findViewById<TextView>(R.id.text_view_main_workout).text = it.toString()
                                            }
                                    }, {

                                    })

                            }
                    }
                }
                R.id.button_main_stop_running -> {
                    binder!!.cancelCurrentWorkout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
                }
                R.id.button_main_stop_walking -> {
                    binder!!.cancelCurrentWorkout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
                }
                R.id.button_main_stop_cycling -> {
                    binder!!.cancelCurrentWorkout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
                }
                R.id.button_main_stop_swimming -> {
                    binder!!.cancelCurrentWorkout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
                }
                else -> {

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermissions() {
        val locationPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        for(perm in locationPermissions) {
            if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(locationPermissions, PERMISSION_CODE_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermissions() {
        val locationPermissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        for(perm in locationPermissions) {
            if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(locationPermissions, PERMISSION_CODE_LOCATION)
            }
        }
    }

    @Composable
    fun MainContent() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val (textOffset, setTextOffset) = remember {
                mutableStateOf(0F)
            }
            val (boxTop, setBoxTop) = remember {
                mutableStateOf(0f)
            }
            val (boxBottom, setBoxBottom) = remember {
                mutableStateOf(0f)
            }
            val (textTop, setTextTop) = remember {
                mutableStateOf(0f)
            }
            val (textBottom, setTextBottom) = remember {
                mutableStateOf(0f)
            }
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .size(100.dp, 280.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Drag me",
                    Modifier
                        .absoluteOffset {
                            IntOffset(0, textOffset.roundToInt())
                        }
                        .draggable( //[1]
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState(onDelta = { delta ->
                                if (textTop + delta < 0) { // [2]
//                                        setTextOffset(-textTop)
                                } else if (textBottom + delta > boxBottom) {
//                                        setTextOffset((boxBottom - textBottom))
                                } else {
                                    setTextOffset(textOffset + delta)
                                }
                            })
                        )
                        .onGloballyPositioned { layoutCoordinates ->
//                                setBoxTop(layoutCoordinates.parentCoordinates!!.positionInParent().y) [2]
                            setBoxBottom(
                                layoutCoordinates.parentLayoutCoordinates!!.size.height.toFloat()
                            )
                            setTextTop(layoutCoordinates.boundsInParent().bottom)
                            setTextBottom(layoutCoordinates.boundsInParent().bottom + layoutCoordinates.size.height.toFloat())
                        },
                )
            }
        }
    }
    //Don't mind me, just taking notes
    // [1] So I got the basic dragging, but it's leaving the bounds of it's parent box. I need to prevent that
    // [2] So it sort of works, though it doesn't look pretty, and if I drag it too far out it gets stuck

    companion object {
        const val PERMISSION_CODE_LOCATION = 100
        const val PERMISSION_CODE_NOTIFICATIONS = 101

        const val REQUEST_CODE_ACTIVITY_TRANSITION = 200
        const val REQUEST_CODE_LOCATION_SETTINGS = 200
    }
}