package com.davidgrath.fitnessapp.framework

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.framework.database.AppDatabase
import com.davidgrath.fitnessapp.framework.database.entities.CyclingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.GymSet
import com.davidgrath.fitnessapp.framework.database.entities.GymWorkout
import com.davidgrath.fitnessapp.framework.database.entities.RunningLocationData
import com.davidgrath.fitnessapp.framework.database.entities.RunningWorkout
import com.davidgrath.fitnessapp.framework.database.entities.SwimmingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.WalkingLocationData
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import com.davidgrath.fitnessapp.framework.database.entities.YogaAsana
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
import com.davidgrath.fitnessapp.ui.home.HomeActivity
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.distanceKm
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.LocationSettingsRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class FitnessService: Service() {

    lateinit var binder: FitnessBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        binder = FitnessBinder(this)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    class FitnessBinder(private val service: FitnessService): Binder(), AbstractFitnessService {

        private val UPDATE_DURATION = 67L  //Some prime less than 100
        private val onNotificationClickedPendingIntent : PendingIntent
        private val locationManager = service.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(service)
        private var currentWorkout = "NONE"
        private val currentWorkoutSubject = BehaviorSubject.create<String>()
        private var currentWorkoutId = -1L
//        private val currentWorkoutIdSubject = PublishSubject.create<Int>()
        private val fitnessApp = service.application as FitnessApp
        private val appDatabase = AppDatabase.getDatabase(service)

        private var timerDuration = 0L
        private var timerDurationSubject = PublishSubject.create<Long>()
        private val executorService: ScheduledThreadPoolExecutor
        private var timerUpdateFuture: ScheduledFuture<*>? = null
        private val timerUpdateRunnable: Runnable

        private val runningWorkoutDao = appDatabase.runningWorkoutDao()
        private val runningLocationDataDao = appDatabase.runningLocationDataDao()
        private val runningNotificationUpdateRunnable: Runnable
        private var totalRunningDistance = 0.0
        private var runningNotificationUpdateFuture: ScheduledFuture<*>? = null

        private val walkingWorkoutDao = appDatabase.walkingWorkoutDao()
        private val walkingLocationDataDao = appDatabase.walkingLocationDataDao()
        private val walkingNotificationUpdateRunnable: Runnable
        private var totalWalkingDistance = 0.0
        private var walkingNotificationUpdateFuture: ScheduledFuture<*>? = null

        private val cyclingWorkoutDao = appDatabase.cyclingWorkoutDao()
        private val cyclingLocationDataDao = appDatabase.cyclingLocationDataDao()
        private val cyclingNotificationUpdateRunnable: Runnable
        private var totalCyclingDistance = 0.0
        private var cyclingNotificationUpdateFuture: ScheduledFuture<*>? = null


        private val swimmingWorkoutDao = appDatabase.swimmingWorkoutDao()
        private val swimmingUpdateRunnable: Runnable
        private var swimmingUpdateFuture: ScheduledFuture<*>? = null
        private val swimmingNotificationUpdateRunnable: Runnable
        private var swimmingNotificationUpdateFuture: ScheduledFuture<*>? = null

        private val gymWorkoutDao = appDatabase.gymWorkoutDao()
        private val gymSetDao = appDatabase.gymSetDao()
        private var gymSetTimerDuration = 0L
        private var gymSetTimerUpdateFuture: ScheduledFuture<*>? = null
        private val gymSetTimerUpdateRunnable: Runnable
        private var currentGymSetIdentifier: String = ""
        private var currentGymRoutineIndex: Int = 0
        private var currentGymSetIndex: Int = 0

        private val yogaWorkoutDao = appDatabase.yogaWorkoutDao()
        private val yogaAsanaDao = appDatabase.yogaAsanaDao()
        private var yogaAsanaTimerDuration = 0L
        private var yogaAsanaTimeLeft = 0L
        private var yogaAsanaStateSubject = PublishSubject.create<YogaAsanaState>()
        private var yogaAsanaState = YogaAsanaState(0, false)
        private var yogaAsanaTimerDurationEnd = 0
        private var yogaAsanaTimerUpdateFuture: ScheduledFuture<*>? = null
        private val yogaAsanaTimerUpdateRunnable: Runnable
        private var yogaAsanaTimeLeftUpdateFuture: ScheduledFuture<*>? = null
        private val yogaAsanaTimeLeftUpdateRunnable: Runnable
        private var currentYogaAsanaIdentifier: String = ""

        private val preferences: SharedPreferences


        /*val locationCallback = object : LocationCallback() {
            private var previousLatitude: Double? = null
            private var previousLongitude: Double? = null
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val latestLocation = locationResult.locations.last()
                val altitude : Double? = if (latestLocation.hasAltitude()) latestLocation.altitude else null
                val bearing : Float? = if (latestLocation.hasBearing()) latestLocation.bearing else null

                val speed : Float? = if (latestLocation.hasSpeed()) latestLocation.speed else null
                when(currentWorkout) {
                    "RUNNING" -> {
                        incrementRunningStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                            .flatMap {
                                runningLocationDataDao.insertWorkoutLocationData(currentWorkoutId, latestLocation.longitude,
                                latestLocation.latitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            }
                            .subscribeOn(Schedulers.io())
                            .subscribe({}, {})
                    }
                    "WALKING" -> {
                        incrementWalkingStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                            .flatMap {
                                walkingLocationDataDao.insertWorkoutLocationData(currentWorkoutId, latestLocation.longitude,
                                    latestLocation.latitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            }
                            .subscribeOn(Schedulers.io())
                            .subscribe({}, {})
                    }
                    "CYCLING" -> {
                        incrementCyclingStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                            .flatMap {
                                cyclingLocationDataDao.insertWorkoutLocationData(currentWorkoutId, latestLocation.longitude,
                                    latestLocation.latitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            }
                            .subscribeOn(Schedulers.io())
                            .subscribe({}, {})
                    }
                }
                previousLongitude = latestLocation.longitude
                previousLatitude = latestLocation.latitude
            }
        }*/

        private var previousLatitude: Double? = null
        private var previousLongitude: Double? = null
        val locationListener = LocationListener { latestLocation ->
            val altitude : Double? = if (latestLocation.hasAltitude()) latestLocation.altitude else null
            val bearing : Float? = if (latestLocation.hasBearing()) latestLocation.bearing else null

            val speed : Float? = if (latestLocation.hasSpeed()) latestLocation.speed else null
            when(currentWorkout) {
                "RUNNING" -> {
                    incrementRunningStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                        .flatMap {
                            val runningLocationData = RunningLocationData(null, currentWorkoutId, latestLocation.latitude,
                                latestLocation.longitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            runningLocationDataDao.insertWorkoutLocationData(runningLocationData)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({}, {})
                }
                "WALKING" -> {
                    incrementWalkingStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                        .flatMap {
                            val walkingLocationData = WalkingLocationData(null, currentWorkoutId, latestLocation.latitude,
                                latestLocation.longitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            walkingLocationDataDao.insertWorkoutLocationData(walkingLocationData)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({}, {})
                }
                "CYCLING" -> {
                    incrementCyclingStatistics(currentWorkoutId, previousLatitude, previousLongitude, latestLocation.latitude, latestLocation.longitude)
                        .flatMap {
                            val cyclingLocationData = CyclingLocationData(null, currentWorkoutId, latestLocation.latitude,
                                latestLocation.longitude, latestLocation.time, latestLocation.accuracy, altitude, bearing, speed)
                            cyclingLocationDataDao.insertWorkoutLocationData(cyclingLocationData)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({}, {})
                }
            }
            previousLongitude = latestLocation.longitude
            previousLatitude = latestLocation.latitude
        }

        init {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
            preferences = service.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, MODE_PRIVATE)
            val onNotificationClickedIntent = Intent(service, HomeActivity::class.java)
            onNotificationClickedPendingIntent =
                PendingIntent.getActivity(service, NOTIFICATION_CLICK_REQUEST_CODE, onNotificationClickedIntent, getIntentFlags())
            executorService = ScheduledThreadPoolExecutor(5)
            timerDuration = 0
            timerDurationSubject.onNext(timerDuration)
            currentWorkoutSubject.onNext("NONE")
            timerUpdateRunnable = Runnable {
                timerDuration += UPDATE_DURATION
                timerDurationSubject.onNext(timerDuration)
            }
            gymSetTimerUpdateRunnable = Runnable {
                gymSetTimerDuration += UPDATE_DURATION
            }
            yogaAsanaTimerUpdateRunnable = Runnable {
                yogaAsanaTimerDuration += UPDATE_DURATION
            }
            yogaAsanaTimeLeftUpdateRunnable = Runnable {
                if(yogaAsanaTimeLeft - UPDATE_DURATION <= 0) {
                    yogaAsanaTimeLeft = 0
                    yogaAsanaState = yogaAsanaState.copy(timeLeft = yogaAsanaTimeLeft)
                    yogaAsanaStateSubject.onNext(yogaAsanaState)
                    yogaAsanaTimerUpdateFuture?.cancel(true)
                } else {
                    yogaAsanaTimeLeft -= UPDATE_DURATION
                    yogaAsanaState = yogaAsanaState.copy(timeLeft = yogaAsanaTimeLeft)
                    yogaAsanaStateSubject.onNext(yogaAsanaState)
                }
            }
            runningNotificationUpdateRunnable = Runnable {
                val notificationManager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notif = buildNotification("Running","0s, 0km", R.drawable.run_fast)
                //TODO Add timer, distance, kcal text
                notificationManager.notify(NOTIFICATION_ID, notif)
            }

            walkingNotificationUpdateRunnable = Runnable {
                val notificationManager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notif = buildNotification("Walking","0s, 0km", R.drawable.shoe_sneaker)
                //TODO Add timer, distance, kcal text
                notificationManager.notify(NOTIFICATION_ID, notif)
            }

            cyclingNotificationUpdateRunnable = Runnable {
                val notificationManager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notif = buildNotification("Cycling","0s, 0km", R.drawable.bike)
                //TODO Add timer, distance, kcal text
                notificationManager.notify(NOTIFICATION_ID, notif)
            }

            swimmingUpdateRunnable = Runnable {
                calculateSwimmingKCalBurned(currentWorkoutId, timerDuration).subscribe({}, {})
            }
            swimmingNotificationUpdateRunnable = Runnable {
                val notificationManager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notif = buildNotification("Swimming","0s, 0km", R.drawable.swim)
                notificationManager.notify(NOTIFICATION_ID, notif)
            }
        }

        override fun getCurrentWorkoutObservable() : Observable<String> {
            return currentWorkoutSubject
        }

        override fun getCurrentTimeElapsedObservable() : Observable<Long> {
            return timerDurationSubject
        }

        override fun cancelCurrentWorkout() : Single<Unit> {
            return if(currentWorkout != "NONE") {
                when(currentWorkout) {
                    "RUNNING" -> {
//                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        locationManager.removeUpdates(locationListener)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            runningNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(runningNotificationUpdateRunnable)
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                            Log.d("REMOVED", removed.toString())
                        } else {
                            runningNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(runningNotificationUpdateRunnable)
                            service.stopForeground(true)
                            Log.d("REMOVED", removed.toString())
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        timerUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        calculateRunningKCalBurned(currentWorkoutId, duration)
                    }
                    "WALKING" -> {
//                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        locationManager.removeUpdates(locationListener)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            walkingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(walkingNotificationUpdateRunnable)
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                            Log.d("REMOVED", removed.toString())
                        } else {
                            walkingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(walkingNotificationUpdateRunnable)
                            service.stopForeground(true)
                            Log.d("REMOVED", removed.toString())
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        timerUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        calculateWalkingKCalBurned(currentWorkoutId, timerDuration)
                    }
                    "CYCLING" -> {
//                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        locationManager.removeUpdates(locationListener)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            cyclingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(cyclingNotificationUpdateRunnable)
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                            Log.d("REMOVED", removed.toString())
                        } else {
                            cyclingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(cyclingNotificationUpdateRunnable)
                            service.stopForeground(true)
                            Log.d("REMOVED", removed.toString())
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        timerUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        calculateCyclingKCalBurned(currentWorkoutId, duration)
                    }
                    "SWIMMING" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            swimmingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(swimmingNotificationUpdateRunnable)
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                            Log.d("REMOVED", removed.toString())
                        } else {
                            swimmingNotificationUpdateFuture?.cancel(true)
                            val removed = executorService.remove(swimmingNotificationUpdateRunnable)
                            service.stopForeground(true)
                            Log.d("REMOVED", removed.toString())
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        swimmingUpdateFuture?.cancel(true)
                        executorService.remove(swimmingUpdateRunnable)
                        timerUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        calculateSwimmingKCalBurned(currentWorkoutId, duration)
                    }
                    "GYM" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                        } else {
                            service.stopForeground(true)
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        timerUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        calculateGymKCalBurned(currentWorkoutId, duration)
                    }
                    "YOGA" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            service.stopForeground(STOP_FOREGROUND_REMOVE)
                        } else {
                            service.stopForeground(true)
                        }
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        val duration = timerDuration
                        timerDuration = 0
                        timerDurationSubject.onNext(timerDuration)
                        timerUpdateFuture?.cancel(true)
                        yogaAsanaTimerUpdateFuture?.cancel(true)
                        yogaAsanaTimeLeftUpdateFuture?.cancel(true)
                        executorService.remove(timerUpdateRunnable)
                        executorService.remove(yogaAsanaTimerUpdateRunnable)
                        executorService.remove(yogaAsanaTimeLeftUpdateRunnable)
                        calculateYogaKCalBurned(currentWorkoutId, duration)
                    }
                    else -> {
                        currentWorkout = "NONE"
                        currentWorkoutSubject.onNext(currentWorkout)
                        Single.just(Unit)
                    }
                }
            } else {
                Single.just(Unit)
            }
        }

        /*private fun checkLocationSettings() : Single<LocationRequest> {
            val locationRequest = LocationRequest.create().apply {
                interval = INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val settingsClient = LocationServices.getSettingsClient(service)
            val task  = settingsClient.checkLocationSettings(builder.build())
            return Single.create<LocationRequest> { emitter ->
                if(currentWorkout != "NONE") {
                    emitter.onError(Exception())
                }

                task.addOnSuccessListener { response ->
                    if (
                        ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        emitter.onError(Exception())
                    } else {
                        emitter.onSuccess(locationRequest)
                    }
                }
                task.addOnFailureListener {
                    Log.e("FitnessService", it.message, it)
                    emitter.onError(it)
                }
            }
        }*/

        val TEMP_CODE_OK = 0
        val TEMP_CODE_OTHER = 1
        val TEMP_CODE_LOCATION_PERMISSION_NOT_GRANTED = 2
        val TEMP_CODE_PROVIDER_NOT_ENABLED = 3
        private fun verifyLocationSettings(): Int {
            if(currentWorkout != "NONE") {
                return TEMP_CODE_OTHER
            }
            if (
                ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return TEMP_CODE_LOCATION_PERMISSION_NOT_GRANTED
            } else if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                return TEMP_CODE_PROVIDER_NOT_ENABLED
            } else {
                return TEMP_CODE_OK
            }
        }

        override fun startWorkout(type: String): Single<Long> {
            if(type.equals(currentWorkout, true)) { //No point restarting if it's the same
                // type of workout
                return Single.just(-1L)
            }
            val workoutSingle = when(type) {
                "WALKING" -> startWalkingWorkout()
                "RUNNING" -> startRunningWorkout()
                "CYCLING" -> startCyclingWorkout()
                "SWIMMING" -> startSwimmingWorkout()
                "GYM" -> startGymWorkout()
                "YOGA" -> startYogaWorkout()
                else -> Single.just(-1L)
            }
            if(currentWorkout == "NONE") {
                return workoutSingle
            } else {
                return cancelCurrentWorkout().flatMap { workoutSingle }
            }
        }

        private fun startRunningWorkout() : Single<Long> {
//            return checkLocationSettings().flatMap { locationRequest ->
            return when(verifyLocationSettings()) {
                TEMP_CODE_OK -> {
                    Single.just(Unit)
                        .flatMap {
                            val timeZone = TimeZone.getDefault()
                            currentWorkout = "RUNNING"
                            currentWorkoutSubject.onNext(currentWorkout)
//                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                            Handler(Looper.getMainLooper()).post {
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    FASTEST_INTERVAL,
                                    1F,
                                    locationListener
                                )
                            }
                            val notification =
                                buildNotification("Running", "0s, 0km", R.drawable.run_fast)

                            timerUpdateFuture = executorService.scheduleAtFixedRate(
                                timerUpdateRunnable,
                                0L,
                                UPDATE_DURATION,
                                TimeUnit.MILLISECONDS
                            )
                            runningNotificationUpdateFuture = executorService.scheduleAtFixedRate(
                                runningNotificationUpdateRunnable,
                                0L,
                                5L,
                                TimeUnit.SECONDS
                            )
                            service.startForeground(NOTIFICATION_ID, notification)
                            runningWorkoutDao.insertWorkout(
                                RunningWorkout(
                                    null,
                                    Date().time,
                                    timeZone.id
                                )
                            ).map {
                                currentWorkoutId = it
                                it
                            }
                        }
                }
                TEMP_CODE_OTHER -> {
                    Single.error(Exception())
                }
                TEMP_CODE_PROVIDER_NOT_ENABLED -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if(intent.resolveActivity(service.packageManager) != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        service.startActivity(intent)
                    }
                    Single.just(-1)
                }
                TEMP_CODE_LOCATION_PERMISSION_NOT_GRANTED -> {
                    Single.error(SecurityException())
                }
                else -> {
                    Single.error(Exception())
                }
            }

        }

        //TODO This function gets affected by "stationary" behavior. Improve by detecting and adjusting
        // the calculations for average speed -> MET -> calories appropriately
        private fun incrementRunningStatistics(workoutId: Long, lat1: Double?, lon1: Double?, lat2: Double, lon2: Double) : Single<Unit> {
            if(lat1 == null || lon1 == null) {
                return Single.just(Unit)
            }
            val tempSpeedValues = arrayOf(1.0)
            val tempMetValues = arrayOf(2.0)
            //2011 Compendium of Physical Activities
            val speedValues = tempSpeedValues + arrayOf(4.0, 5.0, 5.2, 6.0, 6.7, 7.0, 7.5, 8.0, 8.6, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0)
            val metValues = tempMetValues + arrayOf(6.0, 8.3, 9.0, 9.8, 10.5, 11.0, 11.5, 11.8, 12.3, 12.8, 14.5, 16.0, 19.0, 19.8, 23.0)
            return runningLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {
                    val d = distanceKm(lat1, lon1, lat2, lon2)
                    totalRunningDistance += d

                    val durationMinutes = timerDuration / 60_000.0
                    val durationHours = timerDuration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalRunningDistance * 0.621371F
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, speedValues)
                    if(metValueIndex == -1) {
                        return@flatMap runningWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, timerDuration, 0).map {  }
                    }
                    val metValue = metValues[metValueIndex]
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    runningWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, timerDuration, kCalBurned.toInt()).map {  }
                }
        }

        private fun calculateRunningKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            //2011 Compendium of Physical Activities
            val speedValues = arrayOf(4.0, 5.0, 5.2, 6.0, 6.7, 7.0, 7.5, 8.0, 8.6, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0)
            val metValues = arrayOf(6.0, 8.3, 9.0, 9.8, 10.5, 11.0, 11.5, 11.8, 12.3, 12.8, 14.5, 16.0, 19.0, 19.8, 23.0)
            return runningLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {
                    var totalDistance = 0.0
                    it.windowed(2, 1, false) { coordPair ->
                        val d = distanceKm(coordPair[0].latitude, coordPair[0].longitude, coordPair[1].latitude, coordPair[1].longitude)
                        totalDistance += d
                    }
                    if(totalDistance == 0.0) {
                        return@flatMap runningWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map {  }
                    }
                    val durationMinutes = duration / 60_000.0
                    val durationHours = duration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalDistance * 0.621371
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, speedValues)
                    if(metValueIndex == -1) {
                        return@flatMap runningWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map {  }
                    }
                    val metValue = metValues[metValueIndex]
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    runningWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, kCalBurned.toInt()).map { }
                }
        }

        private fun startWalkingWorkout() : Single<Long> {
//            return checkLocationSettings().flatMap { locationRequest ->
            return when(verifyLocationSettings()) {
                TEMP_CODE_OK -> {
                    Single.just(Unit)
                        .flatMap {
                            val timeZone = TimeZone.getDefault()
                            currentWorkout = "WALKING"
                            currentWorkoutSubject.onNext(currentWorkout)
//                    fusedLocationClient.requestLocationUpdates(
//                        locationRequest,
//                        locationCallback,
//                        Looper.getMainLooper()
//                    )
                            Handler(Looper.getMainLooper()).post {
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    FASTEST_INTERVAL,
                                    1F,
                                    locationListener
                                )
                            }
                            val notification =
                                buildNotification("Walking", "0s, 0km", R.drawable.shoe_sneaker)

                            timerUpdateFuture = executorService.scheduleAtFixedRate(
                                timerUpdateRunnable,
                                0L,
                                UPDATE_DURATION,
                                TimeUnit.MILLISECONDS
                            )
                            walkingNotificationUpdateFuture = executorService.scheduleAtFixedRate(
                                walkingNotificationUpdateRunnable,
                                0L,
                                5L,
                                TimeUnit.SECONDS
                            )
                            service.startForeground(NOTIFICATION_ID, notification)
                            walkingWorkoutDao.insertWorkout(
                                WalkingWorkout(
                                    null,
                                    Date().time,
                                    timeZone.id
                                )
                            ).map {
                                currentWorkoutId = it
                                it
                            }
                        }
                }
                TEMP_CODE_OTHER -> {
                    Single.error(Exception())
                }
                TEMP_CODE_PROVIDER_NOT_ENABLED -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if(intent.resolveActivity(service.packageManager) != null) {
                        service.startActivity(intent)
                    }
                    Single.just(-1)
                }
                TEMP_CODE_LOCATION_PERMISSION_NOT_GRANTED -> {
                    Single.error(SecurityException())
                }
                else -> {
                    Single.error(Exception())
                }
            }
        }

        private fun incrementWalkingStatistics(workoutId: Long, lat1: Double?, lon1: Double?, lat2: Double, lon2: Double) : Single<Unit> {
            if(lat1 == null || lon1 == null) {
                return Single.just(Unit)
            }
            val tempSpeedValues = arrayOf(1.0)
            val tempMetValues = arrayOf(1.2)
            //2011 Compendium of Physical Activities
            val speedValues = tempSpeedValues + arrayOf(2.5, 2.8, 3.2, 3.5, 4.0, 4.5, 5.0) //All "level surface"
            val metValues = tempMetValues + arrayOf(3.0, 3.5, 3.5, 4.3, 5.0, 7.0, 8.3) // All "level surface"
            return walkingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {

                    val d = distanceKm(lat1, lon1, lat2, lon2)
                    totalWalkingDistance += d

                    val durationMinutes = timerDuration / 60_000.0
                    val durationHours = timerDuration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalWalkingDistance * 0.621371F
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, speedValues)
                    if(metValueIndex == -1) {
                        return@flatMap walkingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, timerDuration, 0).map {  }
                    }
                    val metValue = metValues[metValueIndex]
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    walkingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, timerDuration, kCalBurned.toInt()).map {  }
                }
        }

        private fun calculateWalkingKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            //2011 Compendium of Physical Activities
            val walkingSpeedValues = arrayOf(2.5, 2.8, 3.2, 3.5, 4.0, 4.5, 5.0) //All "level surface"
            val walkingMetValues = arrayOf(3.0, 3.5, 3.5, 4.3, 5.0, 7.0, 8.3) // All "level surface"
            return walkingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {
                    var totalDistance = 0.0
                    it.windowed(2, 1, false) { coordPair ->
                        val d = distanceKm(coordPair[0].latitude, coordPair[0].longitude, coordPair[1].latitude, coordPair[1].longitude)
                        totalDistance += d
                    }
                    if(totalDistance == 0.0) {
                        return@flatMap walkingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map {  }
                    }
                    val durationMinutes = duration / 60_000.0
                    val durationHours = duration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalDistance * 0.621371
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, walkingSpeedValues)
                    if(metValueIndex == -1) {
                        return@flatMap walkingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map { }
                    }
                    val metValue = walkingMetValues[metValueIndex]
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    walkingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, kCalBurned.toInt()).map { }
                }
        }

        private fun startCyclingWorkout() : Single<Long> {
//            return checkLocationSettings().flatMap { locationRequest ->
            return when(verifyLocationSettings()) {
                TEMP_CODE_OK -> {
                    Single.just(Unit) // Added this because of new startWorkout() method.
                        // "Side effects" need to be delayed
                        .flatMap {
                        val timeZone = TimeZone.getDefault()
                        currentWorkout = "CYCLING"
                        currentWorkoutSubject.onNext(currentWorkout)
//                    fusedLocationClient.requestLocationUpdates(
//                        locationRequest,
//                        locationCallback,
//                        Looper.getMainLooper()
//                    )
                            Handler(Looper.getMainLooper()).post {
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    FASTEST_INTERVAL,
                                    1F,
                                    locationListener
                                )
                            }
                        val notification = buildNotification("Cycling", "0s, 0km", R.drawable.bike)

                        timerUpdateFuture = executorService.scheduleAtFixedRate(
                            timerUpdateRunnable,
                            0L,
                            UPDATE_DURATION,
                            TimeUnit.MILLISECONDS
                        )
                        cyclingNotificationUpdateFuture = executorService.scheduleAtFixedRate(
                            cyclingNotificationUpdateRunnable,
                            0L,
                            5L,
                            TimeUnit.SECONDS
                        )
                        service.startForeground(NOTIFICATION_ID, notification)
                        cyclingWorkoutDao.insertWorkout(CyclingWorkout(null, Date().time, timeZone.id)).map {
                            currentWorkoutId = it
                            it
                        }
                    }
                }
                TEMP_CODE_OTHER -> {
                    Single.error(Exception())
                }
                TEMP_CODE_PROVIDER_NOT_ENABLED -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if(intent.resolveActivity(service.packageManager) != null) {
                        service.startActivity(intent)
                    }
                    Single.just(-1)
                }
                TEMP_CODE_LOCATION_PERMISSION_NOT_GRANTED -> {
                    Single.error(SecurityException())
                }
                else -> {
                    Single.error(Exception())
                }
            }
        }

        private fun incrementCyclingStatistics(workoutId: Long, lat1: Double?, lon1: Double?, lat2: Double, lon2: Double) : Single<Unit> {
            if(lat1 == null || lon1 == null) {
                return Single.just(Unit)
            }
            val tempSpeedValues = arrayOf(1.0)
            val tempMetValues = arrayOf(1.2)
            //2011 Compendium of Physical Activities
            val speedValues = tempSpeedValues + arrayOf(10.0, 11.9, 12.0, 13.9, 14.0, 15.9, 16.0, 19.0, 20.0)
            val metValues = tempMetValues + arrayOf(6.8, 6.8, 8.0, 8.0, 10.0, 10.0, 12.0, 12.0, 15.8)
            return cyclingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {
                    val d = distanceKm(lat1, lon1, lat2, lon2)
                    totalCyclingDistance += d

                    val durationMinutes = timerDuration / 60_000.0
                    val durationHours = timerDuration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalCyclingDistance * 0.621371F
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, speedValues)
                    var metValue : Double
                    if(metValueIndex == -1) {
                        metValue = 7.5 // Bicycling, general
                    } else {
                        metValue = metValues[metValueIndex]
                    }
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    cyclingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, timerDuration, kCalBurned.toInt()).map {  }
                }
        }

        private fun calculateCyclingKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            //2011 Compendium of Physical Activities
            val cyclingSpeedValues = arrayOf(10.0, 11.9, 12.0, 13.9, 14.0, 15.9, 16.0, 19.0, 20.0)
            val cyclingMetValues = arrayOf(6.8, 6.8, 8.0, 8.0, 10.0, 10.0, 12.0, 12.0, 15.8)
            return cyclingLocationDataDao.getWorkoutLocationDataSingle(workoutId)
                .flatMap {
                    var totalDistance = 0.0
                    it.windowed(2, 1, false) { coordPair ->
                        val d = distanceKm(coordPair[0].latitude, coordPair[0].longitude, coordPair[1].latitude, coordPair[1].longitude)
                        totalDistance += d
                    }
                    if(totalDistance == 0.0) {
                        return@flatMap cyclingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map {  }
                    }
                    val durationMinutes = duration / 60_000.0
                    val durationHours = duration.toFloat() / 3_600_000
                    val totalDistanceMiles = totalDistance * 0.621371
                    val averageSpeedMph = totalDistanceMiles/durationHours
                    val metValueIndex = linearSearchApproximate(averageSpeedMph, cyclingSpeedValues)
                    if(metValueIndex == -1) {
                        return@flatMap cyclingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, 0).map { }
                    }
                    val metValue = cyclingMetValues[metValueIndex]
                    var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                    if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                        bodyWeight *= 0.453592F;
                    }
                    val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                    cyclingWorkoutDao.setWorkoutDurationAndKCalBurned(workoutId, duration, kCalBurned.toInt()).map { }
                }
        }

        private fun startSwimmingWorkout() : Single<Long> {
            return Single.just(Unit)
                .flatMap {
                    val timeZone = TimeZone.getDefault()
                    currentWorkout = "SWIMMING"
                    currentWorkoutSubject.onNext(currentWorkout)
                    val notification = buildNotification("Swimming", "0s, 0km", R.drawable.swim)

                    timerUpdateFuture = executorService.scheduleAtFixedRate(
                        timerUpdateRunnable,
                        0L,
                        UPDATE_DURATION,
                        TimeUnit.MILLISECONDS
                    )
                    swimmingUpdateFuture = executorService.scheduleAtFixedRate(
                        swimmingUpdateRunnable,
                        0L,
                        1_000L,
                        TimeUnit.MILLISECONDS
                    )
                    swimmingNotificationUpdateFuture = executorService.scheduleAtFixedRate(
                        swimmingNotificationUpdateRunnable,
                        0L,
                        5L,
                        TimeUnit.SECONDS
                    )
                    service.startForeground(NOTIFICATION_ID, notification)
                    swimmingWorkoutDao.insertWorkout(SwimmingWorkout(null, Date().time, timeZone.id)).map {
                        currentWorkoutId = it
                        it
                    }
                }
        }

        private fun calculateSwimmingKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            val durationMinutes = duration / 60_000.0
            val metValue = 6.0 // Leisurely, not lap swimming, general
            var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
            if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                bodyWeight *= 0.453592F;
            }
            val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
            return swimmingWorkoutDao
                .setWorkoutDurationAndKCalBurned(workoutId, duration, kCalBurned.toInt()).map {  }
        }

        private fun startGymWorkout(name: String = ""): Single<Long> {
            return Single.just(Unit)
                .flatMap {
                    val timestamp = Date().time
                    val timeZone = TimeZone.getDefault()
                    currentWorkout = "GYM"
                    currentWorkoutSubject.onNext(currentWorkout)
                    val notification = buildNotification("Gym", name, R.drawable.weight_lifter)
                    timerUpdateFuture = executorService.scheduleAtFixedRate(
                        timerUpdateRunnable,
                        0L,
                        UPDATE_DURATION,
                        TimeUnit.MILLISECONDS
                    )
                    service.startForeground(NOTIFICATION_ID, notification)
                    gymWorkoutDao.insertWorkout(GymWorkout(null, timestamp, timeZone.id, name))
                        .map {
                            currentWorkoutId = it
                            it
                        }
                }
        }

        override fun startGymSet(setIdentifier: String) {
            gymSetTimerDuration = 0L
            gymSetTimerUpdateFuture = executorService.scheduleAtFixedRate(gymSetTimerUpdateRunnable, 0L, UPDATE_DURATION, TimeUnit.MILLISECONDS)
            currentGymSetIdentifier = setIdentifier
        }

        override fun skipGymSet() {
            gymSetTimerUpdateFuture?.cancel(true)
            gymSetTimerDuration = 0L
        }

        override fun endGymSet(repCount: Int) : Single<Long> {
            gymSetTimerUpdateFuture?.cancel(true)
            val timestamp = Date().time
            return gymSetDao.insertSet(GymSet(null, currentWorkoutId, currentGymSetIdentifier, timestamp, repCount, gymSetTimerDuration))
                .doOnSuccess {
                    gymSetTimerDuration = 0L
                    currentGymSetIdentifier = ""
                }
        }

        private fun calculateGymKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            return gymSetDao.getAllSetsByWorkoutIdSingle(workoutId)
                .flatMap { gymSets ->
                    var totalKCalBurned = 0.0
                    for(gymSet in gymSets) {
                        val durationMinutes = gymSet.timeTaken / 60_000.0
                        val metValue = 3.5 // resistance (weight) training, multiple exercises, 8-15 repetitions at varied resistance
                        var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                        if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                            bodyWeight *= 0.453592F;
                        }
                        val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                        totalKCalBurned += kCalBurned
                    }
                    gymWorkoutDao
                        .setWorkoutDurationAndKCalBurned(workoutId, duration, totalKCalBurned.toInt()).map {  }
                }
        }

//        fun addGymSet(workoutId: Long, setIdentifier: String, repCount: Int, timeTaken: Long): Single<Long> {
//            val timestamp = Date().time
//            return gymSetDao.insertSet(GymSet(null, workoutId, setIdentifier, timestamp, repCount, timeTaken))
//        }

        private fun startYogaWorkout(name: String = ""): Single<Long> {
            return Single.just(Unit)
                .flatMap {
                    val timestamp = Date().time
                    val timeZone = TimeZone.getDefault()
                    currentWorkout = "YOGA"
                    currentWorkoutSubject.onNext(currentWorkout)
                    val notification = buildNotification("Yoga", name, R.drawable.yoga)
                    timerUpdateFuture = executorService.scheduleAtFixedRate(
                        timerUpdateRunnable,
                        0L,
                        UPDATE_DURATION,
                        TimeUnit.MILLISECONDS
                    )
                    service.startForeground(NOTIFICATION_ID, notification)
                    yogaWorkoutDao.insertWorkout(YogaWorkout(null, timestamp, timeZone.id, name))
                        .map {
                            currentWorkoutId = it
                            it
                        }
                }
        }

        override fun startYogaAsana(asanaIdentifier: String, durationMillis: Int) {
            yogaAsanaTimerDuration = 0L
            yogaAsanaTimerUpdateFuture = executorService.scheduleAtFixedRate(yogaAsanaTimerUpdateRunnable, 0L, UPDATE_DURATION, TimeUnit.MILLISECONDS)
            yogaAsanaTimeLeft = durationMillis.toLong()
            yogaAsanaState = YogaAsanaState(yogaAsanaTimeLeft, false)
            yogaAsanaStateSubject.onNext(yogaAsanaState)
            yogaAsanaTimeLeftUpdateFuture = executorService.scheduleAtFixedRate(
                yogaAsanaTimeLeftUpdateRunnable, 0L, UPDATE_DURATION, TimeUnit.MILLISECONDS)
            currentYogaAsanaIdentifier = asanaIdentifier
        }

        override fun skipYogaAsana() : Single<Long> {
            if(currentYogaAsanaIdentifier.isBlank()) {
                return Single.just(0)
            }
            yogaAsanaTimerUpdateFuture?.cancel(true)
            yogaAsanaTimeLeftUpdateFuture?.cancel(true)
            val timestamp = Date().time
            return yogaAsanaDao.insertAsana(YogaAsana(null, currentWorkoutId, currentYogaAsanaIdentifier, timestamp, yogaAsanaTimerDuration))
                .doOnSuccess {
                    yogaAsanaTimerDuration = 0L
                    yogaAsanaTimeLeft = 0
                    yogaAsanaState = YogaAsanaState(yogaAsanaTimeLeft, false)
                    yogaAsanaStateSubject.onNext(yogaAsanaState)
                    currentYogaAsanaIdentifier = ""
                }
        }


        override fun pauseCurrentYogaAsana() {
            if(currentYogaAsanaIdentifier.isNotBlank()) {
                yogaAsanaTimerUpdateFuture?.cancel(true)
                yogaAsanaTimeLeftUpdateFuture?.cancel(true)
                yogaAsanaState = yogaAsanaState.copy(isPaused = true)
                yogaAsanaStateSubject.onNext(yogaAsanaState)
            }
        }

        override fun resumeCurrentYogaAsana() {
            if(currentYogaAsanaIdentifier.isNotBlank() && yogaAsanaState.isPaused) {
                yogaAsanaTimerUpdateFuture =
                    executorService.scheduleAtFixedRate(yogaAsanaTimerUpdateRunnable, 0L, UPDATE_DURATION, TimeUnit.MILLISECONDS)
                yogaAsanaTimeLeftUpdateFuture =
                    executorService.scheduleAtFixedRate(yogaAsanaTimeLeftUpdateRunnable, 0L, UPDATE_DURATION, TimeUnit.MILLISECONDS)
                yogaAsanaState = yogaAsanaState.copy(isPaused = false)
                yogaAsanaStateSubject.onNext(yogaAsanaState)
            }
        }

        override fun getYogaAsanaState() : Observable<YogaAsanaState> {
            return yogaAsanaStateSubject
        }

        override fun incrementYogaTimeLeft(additionalTimeMillis: Int) {
            yogaAsanaTimeLeft += additionalTimeMillis
            yogaAsanaState = yogaAsanaState.copy(timeLeft = yogaAsanaTimeLeft)
            yogaAsanaStateSubject.onNext(yogaAsanaState)
        }

        override fun endYogaAsana() : Single<Long> {
            //TODO this if statement is basically a workaround to the fact that at the last asana
            // of a workout, one can "skip" and then "end" the asana, which would be an insert of a
            // blank asana without this statement. Fix?
            // Thu 22 Sep 2023 15:41
            if(currentYogaAsanaIdentifier.isNotBlank()) {
                yogaAsanaTimerUpdateFuture?.cancel(true)
                yogaAsanaTimeLeftUpdateFuture?.cancel(true)
                val timestamp = Date().time
                return yogaAsanaDao.insertAsana(YogaAsana(null, currentWorkoutId, currentYogaAsanaIdentifier, timestamp, yogaAsanaTimerDuration))
                    .doOnSuccess {
                        yogaAsanaTimerDuration = 0L
                        currentYogaAsanaIdentifier = ""
                    }
            } else {
                return Single.just(0)
            }
        }

        private fun calculateYogaKCalBurned(workoutId: Long, duration: Long) : Single<Unit> {
            return yogaAsanaDao.getAllAsanasByWorkoutIdSingle(workoutId)
                .flatMap { yogaAsanas ->
                    var totalKCalBurned = 0.0
                    for(yogaAsana in yogaAsanas) {
                        val durationMinutes = yogaAsana.timeTaken / 60_000.0
                        val metValue = 2.3 // 02140 - video exercise workouts, TV conditioning programs (e.g., yoga, stretching), light effort
                        var bodyWeight = preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0.0F)
                        if(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, null) == Constants.UNIT_WEIGHT_POUNDS) {
                            bodyWeight *= 0.453592F;
                        }
                        val kCalBurned = (durationMinutes * bodyWeight * metValue) /200
                        totalKCalBurned += kCalBurned
                    }
                    yogaWorkoutDao
                        .setWorkoutDurationAndKCalBurned(workoutId, duration, totalKCalBurned.toInt()).map {  }
                }
        }

        // https://stackoverflow.com/a/64737829/7876958
        private fun linearSearchApproximate(value: Double, array: Array<Double>) : Int {
            val end = array.size - 1
            for(i in 0..end-1) {
                if(array[i] <= value && value <= array[i+1]) {
                    return if(abs(value - array[i]) < abs(value - array[i+1])) {
                        i
                    } else {
                        i+1
                    }
                }
            }
            return -1
        }

        private fun buildNotification(workoutTitle: String, workoutDetails: String, workoutIcon: Int) : Notification {
            val builder = NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(workoutTitle)
                .setContentText(workoutDetails)
                .setSmallIcon(workoutIcon)
                .setOnlyAlertOnce(true)
                .setContentIntent(onNotificationClickedPendingIntent)

            return builder.build()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel() {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        private fun getIntentFlags() : Int {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                return PendingIntent.FLAG_UPDATE_CURRENT
            }
        }

        companion object {
            private const val NOTIFICATION_CHANNEL_ID = "workout_tracker"
            private const val NOTIFICATION_CHANNEL_NAME = "Workout tracker"
            private const val NOTIFICATION_CLICK_REQUEST_CODE = 100
            private const val INTERVAL = 3_000L
            private const val FASTEST_INTERVAL = 1_000L;
            private const val NOTIFICATION_ID = 200
        }
    }
}