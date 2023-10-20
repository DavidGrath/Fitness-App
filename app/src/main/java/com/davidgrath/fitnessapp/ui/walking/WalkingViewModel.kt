package com.davidgrath.fitnessapp.ui.walking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.WalkingRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.framework.database.entities.WalkingWorkout
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class WalkingViewModel(
    private val walkingRepository: WalkingRepository
) : ViewModel() {

    var currentWorkoutId: Long = -1
        private set

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    private val _pastWeekWorkoutsLiveData = MutableLiveData<SimpleResult<List<WalkingWorkout>>>()
    val pastWeekWorkoutsLiveData: LiveData<SimpleResult<List<WalkingWorkout>>> = _pastWeekWorkoutsLiveData

    private val _pastMonthWorkoutsLiveData = MutableLiveData<SimpleResult<List<WalkingWorkout>>>()
    val pastMonthWorkoutsLiveData: LiveData<SimpleResult<List<WalkingWorkout>>> = _pastMonthWorkoutsLiveData

    private val _pastWorkoutsLiveData = MutableLiveData<SimpleResult<List<WalkingWorkout>>>(
        SimpleResult.Processing())
    val pastWorkoutsLiveData : LiveData<SimpleResult<List<WalkingWorkout>>> = _pastWorkoutsLiveData

    private val _fullWorkoutSummaryLiveData = MutableLiveData<SimpleResult<WorkoutSummary>>(
        SimpleResult.Processing())
    val fullWorkoutSummaryLiveData : LiveData<SimpleResult<WorkoutSummary>> = _fullWorkoutSummaryLiveData

    private val _isWalkingLiveData = MutableLiveData<Boolean>(false)
    val isWalkingLiveData : LiveData<Boolean> = _isWalkingLiveData

    private val _currentWorkoutLiveData = MutableLiveData<WalkingWorkout>()
    private var currentWorkoutDisposable : Disposable? = null
    val currentWorkoutLiveData : LiveData<WalkingWorkout> = _currentWorkoutLiveData

    private val _locationDataLiveData = MutableLiveData<List<LocationDataUI>>()
    val locationDataLiveData : LiveData<List<LocationDataUI>> = _locationDataLiveData

    private val _timeElapsedLiveData = MutableLiveData<Long>()
    val timeElapsedLiveData : LiveData<Long> = _timeElapsedLiveData

    fun getWorkoutsInPastWeek() {
        _pastWeekWorkoutsLiveData.postValue(SimpleResult.Processing())
        val calendar = Calendar.getInstance()
        val lastDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -8)
        // TODO I feel there might be a weird edge case if I use 7 days that I don't know about.
        //  Keep it safe but now test later
        val firstDay = calendar.time
        walkingRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                {
                    _pastWeekWorkoutsLiveData.postValue(SimpleResult.Success(it))
                },
                {
                    _pastWeekWorkoutsLiveData.postValue(SimpleResult.Failure(it))
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
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                {
                    _pastMonthWorkoutsLiveData.postValue(SimpleResult.Success(it))
                },
                {
                    _pastMonthWorkoutsLiveData.postValue(SimpleResult.Failure(it))
                }
            )
    }

    fun getWorkouts() {
        walkingRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                {
                    _pastWorkoutsLiveData.postValue(SimpleResult.Success(it))
                },
                {
                    _pastWorkoutsLiveData.postValue(SimpleResult.Failure(it))
                }
            )
    }

    fun getFullWorkoutsSummary() {
        //TODO I don't know if I should add a currentWorkoutId to the repositories so as to make sure
        // the fetched Observables/LiveDatas aren't prematurely updated before the workout is done
        walkingRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _fullWorkoutSummaryLiveData.postValue(SimpleResult.Success(it))
                },
                {
                    _fullWorkoutSummaryLiveData.postValue(SimpleResult.Failure(it))
                }
            )
    }

    fun getIsWalking() {
        //TODO Add LiveDateReactiveStreams
        fitnessBinder!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "WALKING"
            }
            .subscribe({
                _isWalkingLiveData.postValue(it)
            }, {

            })
    }

    private fun getLocationData() {
        //TODO Add LiveDateReactiveStreams
        walkingRepository.getWorkoutLocationData(currentWorkoutId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val mapped = list.map {
                    LocationDataUI(it.latitude, it.longitude)
                }
                _locationDataLiveData.postValue(mapped)
            }, {

            })
    }

    fun getTimeElapsed() {
        fitnessBinder!!.getCurrentTimeElapsedObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ time ->
                _timeElapsedLiveData.postValue(time)
            }, {

            })
    }

    fun startWalking() {
        currentWorkoutDisposable?.dispose()
        currentWorkoutDisposable = fitnessBinder!!.startWalkingWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapObservable { id ->
                currentWorkoutId = id
                walkingRepository.getWorkout(currentWorkoutId)
            }
            .subscribe( {
                _currentWorkoutLiveData.postValue(it)
                getLocationData() // Called here because it depends on currentWorkoutId
            }, {
            })
    }

    fun stopWalking() {
        fitnessBinder!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }
}

class WalkingViewModelFactory(
    private val walkingRepository: WalkingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalkingViewModel(walkingRepository) as T
    }
}