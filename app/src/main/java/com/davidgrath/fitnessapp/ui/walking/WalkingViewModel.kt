package com.davidgrath.fitnessapp.ui.walking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.WalkingRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.ui.entities.WalkingWorkoutUI
import com.davidgrath.fitnessapp.util.walkingWorkoutToWalkingWorkoutUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class WalkingViewModel(
    private val walkingRepository: WalkingRepository
) : ViewModel() {

    private var currentWorkoutDisposable : Disposable? = null

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            value?.let {
                it.getWalkingIdObservable()
                    .subscribe({ id ->
                        currentWorkoutDisposable?.dispose()
                        currentWorkoutDisposable = walkingRepository.getWorkout(id)
                            .subscribe({
                                _walkingScreensState = _walkingScreensState.copy(
                                    currentWorkout = walkingWorkoutToWalkingWorkoutUI(it)
                                )
                                _walkingScreensStateLiveData.postValue(_walkingScreensState)
                                getLocationData(id)
                            }, {
                            })

                    }, {})
                it.getCurrentWorkoutObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        it == "WALKING"
                    }
                    .subscribe({
                        _walkingScreensState = _walkingScreensState.copy(isWalking = it)
                        _walkingScreensStateLiveData.postValue(_walkingScreensState)
                    }, {

                    })
                it.getCurrentTimeElapsedObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ time ->
                        _walkingScreensState = _walkingScreensState.copy(elapsedTimeMillis = time)
                        _walkingScreensStateLiveData.postValue(_walkingScreensState)
                    }, {

                    })
            }
        }


    private var _walkingScreensState = WalkingScreensState()
    private val _walkingScreensStateLiveData = MutableLiveData<WalkingScreensState>(_walkingScreensState)
    val walkingScreensStateLiveData : LiveData<WalkingScreensState> = _walkingScreensStateLiveData

    init {
        getWorkouts()
        getWorkoutsInPastWeek()
        getFullWorkoutsSummary()
    }
    private fun getWorkoutsInPastWeek() {
        val calendar = Calendar.getInstance()
        val lastDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -8)
        // TODO I feel there might be a weird edge case if I use 7 days that I don't know about.
        //  Keep it safe but now test later
        val firstDay = calendar.time
        walkingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
//            .map {
//                //TODO There should be a better way to ensure we don't fetch the current workout
//                it.filter {  }
//            }
            .subscribe(
                { list ->
                    val mapped = list.map {
                        walkingWorkoutToWalkingWorkoutUI(it)
                    }
                    _walkingScreensState = _walkingScreensState.copy(pastWeekWorkouts = mapped)
                    _walkingScreensStateLiveData.postValue(_walkingScreensState)
                },
                {
                }
            )
    }

    fun getWorkoutsInMonth(date: Date) {
        val calendar = Calendar.getInstance().also {
            it.time = date
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.MONTH))
        val lastDay = calendar.time
        walkingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    val mapped = list.map {
                        walkingWorkoutToWalkingWorkoutUI(it)
                    }
                    _walkingScreensState = _walkingScreensState.copy(pastMonthWorkouts = mapped)
                    _walkingScreensStateLiveData.postValue(_walkingScreensState)

                },
                {
                }
            )
    }

    private fun getWorkouts() {
        walkingRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    val mapped = list.map {
                        walkingWorkoutToWalkingWorkoutUI(it)
                    }
                    _walkingScreensState = _walkingScreensState.copy(workouts = mapped)
                    _walkingScreensStateLiveData.postValue(_walkingScreensState)
                },
                {
                }
            )
    }

    private fun getFullWorkoutsSummary() {
        //TODO I don't know if I should add a currentWorkoutId to the repositories so as to make sure
        // the fetched Observables/LiveDatas aren't prematurely updated before the workout is done
        walkingRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    _walkingScreensState = _walkingScreensState.copy(workoutSummary = it)
                    _walkingScreensStateLiveData.postValue(_walkingScreensState)
                },
                {
                }
            )
    }

    private fun getLocationData(currentWorkoutId: Long) {
        //TODO Add LiveDateReactiveStreams
        walkingRepository.getWorkoutLocationData(currentWorkoutId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val mapped = list.map {
                    LocationDataUI(it.latitude, it.longitude)
                }
                _walkingScreensState = _walkingScreensState.copy(locationData = mapped)
                _walkingScreensStateLiveData.postValue(_walkingScreensState)
            }, {

            })
    }

    fun startWalking() {
        fitnessService!!.startWorkout("WALKING")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
    }

    fun stopWalking() {
        fitnessService!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }

    data class WalkingScreensState(
        val isWalking: Boolean = false,
        val pastWeekWorkouts: List<WalkingWorkoutUI> = emptyList(),
        val pastMonthWorkouts: List<WalkingWorkoutUI> = emptyList(),
        val workouts: List<WalkingWorkoutUI> = emptyList(),
        val currentWorkout: WalkingWorkoutUI = WalkingWorkoutUI(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val locationData: List<LocationDataUI> = emptyList(),
        val elapsedTimeMillis: Long = 0L
    )
}

class WalkingViewModelFactory(
    private val walkingRepository: WalkingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalkingViewModel(walkingRepository) as T
    }
}