package com.davidgrath.fitnessapp.ui.running

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.RunningRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.ui.entities.RunningWorkoutUI
import com.davidgrath.fitnessapp.util.runningWorkoutToRunningWorkoutUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class RunningViewModel(
    private val runningRepository: RunningRepository
) : ViewModel() {

    private var currentWorkoutDisposable: Disposable? = null

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            value?.let {
                it.getRunningIdObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe({ id ->
                        currentWorkoutDisposable?.dispose()
                        currentWorkoutDisposable = runningRepository.getWorkout(id)
                            .subscribe({
                                val rw = runningWorkoutToRunningWorkoutUI(it)
                                _runningScreensState = _runningScreensState.copy(currentWorkout = rw)
                                _runningScreensStateLiveData.postValue(_runningScreensState)
                                getLocationData(id)
                            }, {})
                    }, {})
                it.getCurrentWorkoutObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        it == "RUNNING"
                    }
                    .subscribe({
                        _runningScreensState = _runningScreensState.copy(isRunning = it)
                        _runningScreensStateLiveData.postValue(_runningScreensState)
                    }, {

                    })
                it.getCurrentTimeElapsedObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ time ->
                        _runningScreensState = _runningScreensState.copy(elapsedTimeMillis = time)
                        _runningScreensStateLiveData.postValue(_runningScreensState)
                    }, {

                    })
            }
        }

    private var _runningScreensState = RunningScreensState()
    private val _runningScreensStateLiveData = MutableLiveData(_runningScreensState)
    val runningScreensStateLiveData: LiveData<RunningScreensState> = _runningScreensStateLiveData

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
        runningRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    val mapped = list.map {
                        runningWorkoutToRunningWorkoutUI(it)
                    }
                    _runningScreensState = _runningScreensState.copy(pastWeekWorkouts = mapped)
                    _runningScreensStateLiveData.postValue(_runningScreensState)
                },
                {
                }
            )
    }

    private fun getWorkoutsInMonth(date: Date) {
        val calendar = Calendar.getInstance().also {
            it.time = date
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.MONTH))
        val lastDay = calendar.time
        runningRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    val mapped = list.map {
                        runningWorkoutToRunningWorkoutUI(it)
                    }
                    _runningScreensState = _runningScreensState.copy(pastMonthWorkouts = mapped)
                    _runningScreensStateLiveData.postValue(_runningScreensState)
                },
                {
                }
            )
    }

    private fun getWorkouts() {
        runningRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    val mapped = list.map {
                        runningWorkoutToRunningWorkoutUI(it)
                    }
                    _runningScreensState = _runningScreensState.copy(workouts = mapped)
                    _runningScreensStateLiveData.postValue(_runningScreensState)
                },
                {
                }
            )
    }

    private fun getFullWorkoutsSummary() {
        //TODO I don't know if I should add a currentWorkoutId to the repositories so as to make sure
        // the fetched Observables/LiveDatas aren't prematurely updated before the workout is done
        runningRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _runningScreensState = _runningScreensState.copy(workoutSummary = it)
                    _runningScreensStateLiveData.postValue(_runningScreensState)
                },
                {
                }
            )
    }

    private fun getLocationData(currentWorkoutId: Long) {
        runningRepository.getWorkoutLocationData(currentWorkoutId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val mapped = list.map {
                    LocationDataUI(it.latitude, it.longitude)
                }
                _runningScreensState = _runningScreensState.copy(locationData = mapped)
                _runningScreensStateLiveData.postValue(_runningScreensState)
            }, {

            })
    }

    fun startRunning() {
        fitnessService!!.startWorkout("RUNNING")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
    }

    fun stopRunning() {
        fitnessService!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }

    data class RunningScreensState(
        val isRunning: Boolean = false,
        val pastWeekWorkouts: List<RunningWorkoutUI> = emptyList(),
        val pastMonthWorkouts: List<RunningWorkoutUI> = emptyList(),
        val workouts: List<RunningWorkoutUI> = emptyList(),
        val currentWorkout: RunningWorkoutUI = RunningWorkoutUI(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val locationData: List<LocationDataUI> = emptyList(),
        val elapsedTimeMillis: Long = 0L
    )

    companion object {
        private const val LOG_TAG = "RunningViewModel"
    }
}

class RunningViewModelFactory(
    private val runningRepository: RunningRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RunningViewModel(runningRepository) as T
    }
}