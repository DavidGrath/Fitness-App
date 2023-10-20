package com.davidgrath.fitnessapp.ui.cycling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.CyclingRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.framework.database.entities.CyclingWorkout
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class CyclingViewModel(
    private val cyclingRepository: CyclingRepository
) : ViewModel() {

    var currentWorkoutId: Long = -1
        private set

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    private val _pastWeekWorkoutsLiveData = MutableLiveData<SimpleResult<List<CyclingWorkout>>>()
    val pastWeekWorkoutsLiveData: LiveData<SimpleResult<List<CyclingWorkout>>> = _pastWeekWorkoutsLiveData

    private val _pastMonthWorkoutsLiveData = MutableLiveData<SimpleResult<List<CyclingWorkout>>>()
    val pastMonthWorkoutsLiveData: LiveData<SimpleResult<List<CyclingWorkout>>> = _pastMonthWorkoutsLiveData

    private val _pastWorkoutsLiveData = MutableLiveData<SimpleResult<List<CyclingWorkout>>>(
        SimpleResult.Processing())
    val pastWorkoutsLiveData : LiveData<SimpleResult<List<CyclingWorkout>>> = _pastWorkoutsLiveData

    private val _fullWorkoutSummaryLiveData = MutableLiveData<SimpleResult<WorkoutSummary>>(
        SimpleResult.Processing())
    val fullWorkoutSummaryLiveData : LiveData<SimpleResult<WorkoutSummary>> = _fullWorkoutSummaryLiveData

    private val _isCyclingLiveData = MutableLiveData<Boolean>(false)
    val isCyclingLiveData : LiveData<Boolean> = _isCyclingLiveData

    private val _currentWorkoutLiveData = MutableLiveData<CyclingWorkout>()
    private var currentWorkoutDisposable : Disposable? = null
    val currentWorkoutLiveData : LiveData<CyclingWorkout> = _currentWorkoutLiveData

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
        cyclingRepository.getWorkoutsByDateRange(firstDay, lastDay)
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
        cyclingRepository.getWorkoutsByDateRange(firstDay, lastDay)
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
        cyclingRepository.getWorkoutsByDateRange()
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
        cyclingRepository.getWorkoutsSummaryByDateRange()
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

    fun getIsCycling() {
        //TODO Add LiveDateReactiveStreams
        fitnessBinder!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "CYCLING"
            }
            .subscribe({
                _isCyclingLiveData.postValue(it)
            }, {

            })
    }

    private fun getLocationData() {
        //TODO Add LiveDateReactiveStreams
        cyclingRepository.getWorkoutLocationData(currentWorkoutId)
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

    fun startCycling() {
        currentWorkoutDisposable?.dispose()
        currentWorkoutDisposable = fitnessBinder!!.startCyclingWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapObservable { id ->
                currentWorkoutId = id
                cyclingRepository.getWorkout(currentWorkoutId)
            }
            .subscribe( {
                _currentWorkoutLiveData.postValue(it)
                getLocationData() // Called here because it depends on currentWorkoutId
            }, {
            })
    }

    fun stopCycling() {
        fitnessBinder!!.cancelCurrentWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }
}


class CyclingViewModelFactory(
    private val cyclingRepository: CyclingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CyclingViewModel(cyclingRepository) as T
    }
}