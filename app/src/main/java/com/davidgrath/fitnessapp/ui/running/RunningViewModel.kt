package com.davidgrath.fitnessapp.ui.running

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.RunningRepository
import com.davidgrath.fitnessapp.data.entities.RunningWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class RunningViewModel(
    private val runningRepository: RunningRepository
) : ViewModel() {

    var currentWorkoutId: Int = -1
        private set

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    private val _pastWeekWorkoutsLiveData = MutableLiveData<SimpleResult<List<RunningWorkout>>>()
    val pastWeekWorkoutsLiveData: LiveData<SimpleResult<List<RunningWorkout>>> = _pastWeekWorkoutsLiveData

    private val _pastMonthWorkoutsLiveData = MutableLiveData<SimpleResult<List<RunningWorkout>>>()
    val pastMonthWorkoutsLiveData: LiveData<SimpleResult<List<RunningWorkout>>> = _pastMonthWorkoutsLiveData

    private val _pastWorkoutsLiveData = MutableLiveData<SimpleResult<List<RunningWorkout>>>(
        SimpleResult.Processing())
    val pastWorkoutsLiveData : LiveData<SimpleResult<List<RunningWorkout>>> = _pastWorkoutsLiveData

    private val _fullWorkoutSummaryLiveData = MutableLiveData<SimpleResult<WorkoutSummary>>(
        SimpleResult.Processing())
    val fullWorkoutSummaryLiveData : LiveData<SimpleResult<WorkoutSummary>> = _fullWorkoutSummaryLiveData

    private val _isRunningLiveData = MutableLiveData<Boolean>(false)
    val isRunningLiveData : LiveData<Boolean> = _isRunningLiveData

    private val _currentWorkoutLiveData = MutableLiveData<RunningWorkout>()
    private var currentWorkoutDisposable : Disposable? = null
    val currentWorkoutLiveData : LiveData<RunningWorkout> = _currentWorkoutLiveData

    private val _locationDataLiveData = MutableLiveData<List<LocationDataUI>>()
    val locationDataLiveData : LiveData<List<LocationDataUI>> = _locationDataLiveData

    fun getWorkoutsInPastWeek() {
        _pastWeekWorkoutsLiveData.postValue(SimpleResult.Processing())
        val calendar = Calendar.getInstance()
        val lastDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -8)
        // TODO I feel there might be a weird edge case if I use 7 days that I don't know about.
        //  Keep it safe but now test later
        val firstDay = calendar.time
        runningRepository.getWorkoutsByDateRange(firstDay, lastDay)
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
        runningRepository.getWorkoutsByDateRange(firstDay, lastDay)
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
        runningRepository.getWorkoutsByDateRange()
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
        runningRepository.getWorkoutsSummaryByDateRange()
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

    fun getIsRunning() {
        //TODO Add LiveDateReactiveStreams
        fitnessBinder!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "RUNNING"
            }
            .subscribe({
                _isRunningLiveData.postValue(it)
            }, {

            })
    }

    fun getLocationData() {
        //TODO Add LiveDateReactiveStreams
        runningRepository.getWorkoutLocationData(currentWorkoutId)
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

    fun startRunning() {
        currentWorkoutDisposable?.dispose()
        currentWorkoutDisposable = fitnessBinder!!.startRunningWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapObservable { id ->
                currentWorkoutId = id
                runningRepository.getWorkout(currentWorkoutId)
            }
            .subscribe( {
                _currentWorkoutLiveData.postValue(it)
                getLocationData()
            }, {
            })
    }

    fun stopRunning() {
        fitnessBinder!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }
}

class RunningViewModelFactory(
    private val runningRepository: RunningRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RunningViewModel(runningRepository) as T
    }
}