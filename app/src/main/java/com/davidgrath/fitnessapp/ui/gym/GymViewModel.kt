package com.davidgrath.fitnessapp.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidgrath.fitnessapp.data.GymRepository
import com.davidgrath.fitnessapp.data.entities.GymWorkout
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.util.SimpleResult
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class GymViewModel(
    private val gymRepository: GymRepository
): ViewModel() {

    var currentWorkoutId: Int = -1
        private set

    private val _pastWeekWorkoutsLiveData = MutableLiveData<SimpleResult<List<GymWorkout>>>()
    val pastWeekWorkoutsLiveData: LiveData<SimpleResult<List<GymWorkout>>> = _pastWeekWorkoutsLiveData

    private val _pastMonthWorkoutsLiveData = MutableLiveData<SimpleResult<List<GymWorkout>>>()
    val pastMonthWorkoutsLiveData: LiveData<SimpleResult<List<GymWorkout>>> = _pastMonthWorkoutsLiveData

    private val _pastWorkoutsLiveData = MutableLiveData<SimpleResult<List<GymWorkout>>>(
        SimpleResult.Processing())
    val pastWorkoutsLiveData : LiveData<SimpleResult<List<GymWorkout>>> = _pastWorkoutsLiveData

    private val _fullWorkoutSummaryLiveData = MutableLiveData<SimpleResult<WorkoutSummary>>(
        SimpleResult.Processing())
    val fullWorkoutSummaryLiveData : LiveData<SimpleResult<WorkoutSummary>> = _fullWorkoutSummaryLiveData

    private val _isDoingGymLiveData = MutableLiveData<Boolean>(false)
    val isDoingGymLiveData : LiveData<Boolean> = _isDoingGymLiveData

    private val gson = Gson()
    private val _tempVideoDetailsLiveData = MutableLiveData<TempVideoDetails>()
    val tempVideoDetailsLiveData : LiveData<TempVideoDetails> = _tempVideoDetailsLiveData

    private val _endCurrentWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endCurrentWorkoutLiveData : LiveData<SimpleResult<Unit>> = _endCurrentWorkoutLiveData

    private val _endSetLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endSetLiveData : LiveData<SimpleResult<Unit>> = _endSetLiveData

    private val _addWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
    val addWorkoutLiveData : LiveData<SimpleResult<Unit>> = _addWorkoutLiveData

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    fun addWorkout(name: String? = "") {
        fitnessBinder!!.startGymWorkout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { id ->
                currentWorkoutId = id
                _addWorkoutLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _addWorkoutLiveData.postValue(SimpleResult.Failure(it))
            })
    }

    fun getWorkoutsInPastWeek() {
        _pastWeekWorkoutsLiveData.postValue(SimpleResult.Processing())
        val calendar = Calendar.getInstance()
        val lastDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -8)
        // TODO I feel there might be a weird edge case if I use 7 days that I don't know about.
        //  Keep it safe but now test later
        val firstDay = calendar.time
        gymRepository.getWorkoutsByDateRange(firstDay, lastDay)
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

    fun getWorkouts() {
        gymRepository.getWorkoutsByDateRange()
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
        gymRepository.getWorkoutsSummaryByDateRange()
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

    fun getIsDoingGym() {
        //TODO Add LiveDateReactiveStreams
        fitnessBinder!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "GYM"
            }
            .subscribe({
                _isDoingGymLiveData.postValue(it)
            }, {

            })
    }

    fun startSet(setIdentifier: String) {
        fitnessBinder!!.startGymSet(setIdentifier)
    }

    fun skipSet() {
        fitnessBinder!!.skipGymSet()
    }

    fun endSet(repCount: Int): LiveData<SimpleResult<Unit>> {
        fitnessBinder!!.endGymSet(repCount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endSetLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endSetLiveData.postValue(SimpleResult.Failure(it))
            })
        return _endSetLiveData
    }


    fun endCurrentWorkout(lastSetRepCount: Int)  {
        fitnessBinder!!.endGymSet(lastSetRepCount)
            .flatMap {
                fitnessBinder!!.cancelCurrentWorkout()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Failure(it))
            })
    }

    fun resetNextSetResult() {
        _endSetLiveData.postValue(null)
    }

    fun tempFetchVideoTitle(videoId: String) {
//        _tempVideoDetailsLiveData.postValue()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val urlString = "https://www.youtube.com/oembed?format=json&url=https://www.youtube.com/watch?v=$videoId"
                val url =  URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                val bufferedReader = BufferedReader(BufferedInputStream(connection.inputStream).reader())
                val response = StringBuffer("")
                var input: String? = ""
                while(input != null) {
                    response.append(input)
                    input = bufferedReader.readLine()
                }
                bufferedReader.close()
                val json = response.toString()
                val tempVideoDetails = gson.fromJson(json, TempVideoDetails::class.java)
                _tempVideoDetailsLiveData.postValue(tempVideoDetails)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    class GymSetState(

    )
}

class GymViewModelFactory(
    private val gymRepository: GymRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GymViewModel(gymRepository) as T
    }
}