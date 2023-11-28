package com.davidgrath.fitnessapp.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.GymRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.framework.database.entities.GymWorkout
import com.davidgrath.fitnessapp.ui.entities.TempVideoDetails
import com.davidgrath.fitnessapp.util.SimpleResult
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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

    var currentWorkoutId: Long = -1
        private set

    private val gson = Gson()
    private val _tempVideoDetailsLiveData = MutableLiveData<TempVideoDetails>()
    val tempVideoDetailsLiveData : LiveData<TempVideoDetails> = _tempVideoDetailsLiveData

    private val _endCurrentWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endCurrentWorkoutLiveData : LiveData<SimpleResult<Unit>> = _endCurrentWorkoutLiveData

    private val _endSetLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endSetLiveData : LiveData<SimpleResult<Unit>> = _endSetLiveData

    private val _addWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
    val addWorkoutLiveData : LiveData<SimpleResult<Unit>> = _addWorkoutLiveData

    private var _gymScreenState = GymScreensState()
    private val _gymScreenStateLiveData = MutableLiveData(_gymScreenState)
    val gymScreenStateLiveData: LiveData<GymScreensState> = _gymScreenStateLiveData

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            value!!.getCurrentWorkoutObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it == "GYM"
                }
                .subscribe({
                    _gymScreenState = _gymScreenState.copy(isDoingGym = it)
                    _gymScreenStateLiveData.postValue(_gymScreenState)
                }, {

                })
        }

    fun addWorkout(name: String? = "") {
        fitnessService!!.startWorkout("GYM")
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
                    _gymScreenState = _gymScreenState.copy(pastWeekWorkouts = it)
                    _gymScreenStateLiveData.postValue(_gymScreenState)
                },
                {
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
                    _gymScreenState = _gymScreenState.copy(pastWorkouts = it)
                    _gymScreenStateLiveData.postValue(_gymScreenState)
                },
                {
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
                    _gymScreenState = _gymScreenState.copy(workoutSummary = it)
                    _gymScreenStateLiveData.postValue(_gymScreenState)
                },
                {
                }
            )
    }

    fun getRoutineAndSetIndex() {
        fitnessService!!.getGymRoutineAndSetIndex()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _gymScreenState = _gymScreenState.copy(currentRoutineIndex = it.first, currentSetIndex = it.second)
                    _gymScreenStateLiveData.postValue(_gymScreenState)
                },
                {
                }
            )
    }

    fun startSet(setIdentifier: String) {
        fitnessService!!.startGymSet(setIdentifier)
    }

    //TODO For now use endSet() with zero as argument
    /*fun skipSet() {
        fitnessService!!.skipGymSet()
    }*/

    fun endSet(repCount: Int): LiveData<SimpleResult<Unit>> {
        fitnessService!!.endGymSet(repCount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endSetLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endSetLiveData.postValue(SimpleResult.Failure(it))
            })
        return _endSetLiveData
    }


    fun endCurrentWorkout(lastSetRepCount: Int) {
        fitnessService!!.endGymSet(lastSetRepCount)
            .flatMap {
                fitnessService!!.cancelCurrentWorkout()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Failure(it))
            })
    }

    fun setRoutineAndSetIndex(routineIndex: Int, setIndex: Int) {
        fitnessService!!.setGymRoutineAndSetIndex(routineIndex, setIndex)
    }

    fun tempFetchVideoDetails(videoId: String) {
        _tempVideoDetailsLiveData.postValue(null)
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

    //TODO Port routineId and setId to service/binder
    data class GymScreensState(
        val pastWeekWorkouts: List<GymWorkout> = emptyList(),
        val pastMonthWorkouts: List<GymWorkout> = emptyList(),
        val pastWorkouts: List<GymWorkout> = emptyList(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val isDoingGym: Boolean = false,
        val currentRoutineIndex: Int = 0,
        val currentSetIndex: Int = 0,
    )
}

class GymViewModelFactory(
    private val gymRepository: GymRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GymViewModel(gymRepository) as T
    }
}