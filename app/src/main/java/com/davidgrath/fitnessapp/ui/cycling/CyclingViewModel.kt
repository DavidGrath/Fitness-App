package com.davidgrath.fitnessapp.ui.cycling

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.CyclingRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.ui.entities.CyclingWorkoutUI
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.cyclingWorkoutToCyclingWorkoutUI
import com.davidgrath.fitnessapp.util.runningWorkoutToRunningWorkoutUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class CyclingViewModel(
    private val cyclingRepository: CyclingRepository
) : ViewModel() {

    private var currentWorkoutDisposable : Disposable? = null

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            value?.let {
                it.getCyclingIdObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe({ id ->
                        currentWorkoutDisposable?.dispose()
                        currentWorkoutDisposable = cyclingRepository.getWorkout(id)
                            .subscribe({
                                val cw = cyclingWorkoutToCyclingWorkoutUI(it)
                                _cyclingScreenState = _cyclingScreenState.copy(currentWorkout = cw)
                                _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
                                getLocationData(id)
                            }, {})
                    }, {})
                it.getCurrentWorkoutObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        it == "CYCLING"
                    }
                    .subscribe({
                        _cyclingScreenState = _cyclingScreenState.copy(isCycling = it)
                        _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
                    }, {

                    })
            }
        }

    private var _cyclingScreenState = CyclingScreenState()
    private val _cyclingScreenStateLiveData = MutableLiveData(_cyclingScreenState)
    val cyclingScreenStateLiveData: LiveData<CyclingScreenState> = _cyclingScreenStateLiveData

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
        cyclingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _cyclingScreenState = _cyclingScreenState.copy(pastWeekWorkouts = it)
                    _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
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
        cyclingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    _cyclingScreenState = _cyclingScreenState.copy(pastMonthWorkouts = list)
                    _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
                },
                {

                }
            )
    }

    private fun getWorkouts() {
        cyclingRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    _cyclingScreenState = _cyclingScreenState.copy(workouts = list)
                    _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
                },
                {
                }
            )
    }

    private fun getFullWorkoutsSummary() {
        cyclingRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _cyclingScreenState = _cyclingScreenState.copy(workoutSummary = it)
                    _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
                },
                {
                }
            )
    }

    /*fun getIsCycling() {
        //TODO Add LiveDateReactiveStreams
        fitnessService!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "CYCLING"
            }
            .subscribe({
                _cyclingScreenState = _cyclingScreenState.copy(isCycling = it)
                _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
            }, {

            })
    }*/

    private fun getLocationData(currentWorkoutId: Long) {
        //TODO Add LiveDateReactiveStreams
        cyclingRepository.getWorkoutLocationData(currentWorkoutId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val mapped = list.map {
                    LocationDataUI(it.latitude, it.longitude)
                }
                _cyclingScreenState = _cyclingScreenState.copy(locationData = mapped)
                _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
            }, {

            })
    }

    fun getTimeElapsed() {
        fitnessService!!.getCurrentTimeElapsedObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ time ->
                _cyclingScreenState = _cyclingScreenState.copy(elapsedTimeMillis = time)
                _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
            }, {

            })
    }

    fun startCycling() {
        currentWorkoutDisposable?.dispose()
        currentWorkoutDisposable = fitnessService!!.startWorkout("CYCLING")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
    }

    fun stopCycling() {
        fitnessService!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }

    fun getIsCycling() {
        fitnessService!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "CYCLING"
            }
            .subscribe({
                _cyclingScreenState = _cyclingScreenState.copy(isCycling = it)
                _cyclingScreenStateLiveData.postValue(_cyclingScreenState)
            }, {

            })
    }

    fun getRunningWorkout() {
        currentWorkoutDisposable?.dispose()
        fitnessService!!.getRunningIdObservable()
    }

    data class CyclingScreenState(
        val isCycling: Boolean = false,
        val pastWeekWorkouts: List<CyclingWorkout> = emptyList(),
        val pastMonthWorkouts: List<CyclingWorkout> = emptyList(),
        val workouts: List<CyclingWorkout> = emptyList(),
        val currentWorkout: CyclingWorkoutUI = CyclingWorkoutUI(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val locationData: List<LocationDataUI> = emptyList(),
        val elapsedTimeMillis: Long = 0L
    )
}


class CyclingViewModelFactory(
    private val cyclingRepository: CyclingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CyclingViewModel(cyclingRepository) as T
    }
}