package com.davidgrath.fitnessapp.ui.swimming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.SwimmingRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.entities.SwimmingWorkoutUI
import com.davidgrath.fitnessapp.util.swimmingWorkoutToSwimmingWorkoutUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class SwimmingViewModel(
    private val swimmingRepository: SwimmingRepository
) : ViewModel() {

    var currentWorkoutId: Long = -1
    private set

    var fitnessService: AbstractFitnessService? = null

    private var _swimmingScreensState = SwimmingScreensState()
    private val _swimmingScreensStateLiveData = MutableLiveData<SwimmingScreensState>(_swimmingScreensState)
    val swimmingScreensStateLiveData : LiveData<SwimmingScreensState> = _swimmingScreensStateLiveData

    fun getWorkoutsInPastWeek() {
        val calendar = Calendar.getInstance()
        val lastDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -8)
        // TODO I feel there might be a weird edge case if I use 7 days that I don't know about.
        //  Keep it safe but now test later
        val firstDay = calendar.time
        swimmingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                { list ->
                    val mapped = list.map {
                        swimmingWorkoutToSwimmingWorkoutUI(it)
                    }
                    _swimmingScreensState = _swimmingScreensState.copy(pastWeekWorkouts = mapped)
                    _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
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
        swimmingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
            { list ->
                val mapped = list.map {
                    swimmingWorkoutToSwimmingWorkoutUI(it)
                }
                _swimmingScreensState = _swimmingScreensState.copy(pastMonthWorkouts = mapped)
                _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
            },
            {
            }
        )
    }

    fun getWorkouts() {
        swimmingRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
            { list ->
                val mapped = list.map {
                    swimmingWorkoutToSwimmingWorkoutUI(it)
                }
                _swimmingScreensState = _swimmingScreensState.copy(workouts = mapped)
                _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
            },
            {
            }
        )
    }

    fun getFullWorkoutsSummary() {
        //TODO I don't know if I should add a currentWorkoutId to the repositories so as to make sure
        // the fetched Observables/LiveDatas aren't prematurely updated before the workout is done
        swimmingRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _swimmingScreensState = _swimmingScreensState.copy(workoutSummary = it)
                    _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
                },
                {

                }
            )
    }

    fun getIsSwimming() {
        //TODO Add LiveDateReactiveStreams
        fitnessService!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "SWIMMING"
            }
            .subscribe({
                _swimmingScreensState = _swimmingScreensState.copy(isSwimming = it)
                _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
            }, {

            })
    }

    fun getTimeElapsed() {
        fitnessService!!.getCurrentTimeElapsedObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ time ->
                _swimmingScreensState = _swimmingScreensState.copy(elapsedTimeMillis = time)
                _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
            }, {

            })
    }

    fun startSwimming() {
        fitnessService!!.startWorkout("SWIMMING")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapObservable { id ->
                currentWorkoutId = id
                swimmingRepository.getWorkout(currentWorkoutId)
            }
            .subscribe( {
                _swimmingScreensState = _swimmingScreensState.copy(currentWorkout = swimmingWorkoutToSwimmingWorkoutUI(it))
                _swimmingScreensStateLiveData.postValue(_swimmingScreensState)
            }, {
            })
    }

    fun stopSwimming() {
        fitnessService!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }

    data class SwimmingScreensState(
        val isSwimming: Boolean = false,
        val pastWeekWorkouts: List<SwimmingWorkoutUI> = emptyList(),
        val pastMonthWorkouts: List<SwimmingWorkoutUI> = emptyList(),
        val workouts: List<SwimmingWorkoutUI> = emptyList(),
        val currentWorkout: SwimmingWorkoutUI = SwimmingWorkoutUI(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val elapsedTimeMillis: Long = 0L
    )
}

class SwimmingViewModelFactory(
    private val swimmingRepository: SwimmingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SwimmingViewModel(swimmingRepository) as T
    }
}