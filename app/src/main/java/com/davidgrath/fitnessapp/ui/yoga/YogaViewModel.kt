package com.davidgrath.fitnessapp.ui.yoga

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.data.YogaRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.ui.entities.TempVideoDetails
import com.davidgrath.fitnessapp.ui.entities.YogaWorkoutUI
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.yogaWorkoutToYogaWorkoutUI
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

class YogaViewModel(
    private val yogaRepository: YogaRepository
): ViewModel() {

    var currentWorkoutId: Long = -1
        private set

    private val _addWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
    val addWorkoutLiveData : LiveData<SimpleResult<Unit>> = _addWorkoutLiveData

    private var _yogaScreensState = YogaScreensState()
    private val _yogaScreensStateLiveData = MutableLiveData<YogaScreensState>(_yogaScreensState)
    val yogaScreensStateLiveData : LiveData<YogaScreensState> = _yogaScreensStateLiveData

    private val gson = Gson()
    private val _tempVideoDetailsLiveData = MutableLiveData<TempVideoDetails>()
    val tempVideoDetailsLiveData : LiveData<TempVideoDetails> = _tempVideoDetailsLiveData

    private val _yogaAsanaStateLiveData = MutableLiveData<YogaAsanaState>()
    val yogaAsanaStateLiveData : LiveData<YogaAsanaState> = _yogaAsanaStateLiveData

    private val _endAsanaLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endAsanaLiveData : LiveData<SimpleResult<Unit>> = _endAsanaLiveData

    private val _endCurrentWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>()
    val endCurrentWorkoutLiveData : LiveData<SimpleResult<Unit>> = _endCurrentWorkoutLiveData

    private val _skipLiveData = MutableLiveData<SimpleResult<Unit>>()
    val skipLiveData : LiveData<SimpleResult<Unit>> = _skipLiveData

    var asanasProgress: Int = -1
        private set(value) {
            field = value
            Log.d("YogaViewModel", "asanaProgress: $value")
        }

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            //TODO Add LiveDataReactiveStreams
            value!!.getCurrentWorkoutObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it == "YOGA"
                }
                .subscribe({
                    _yogaScreensState = _yogaScreensState.copy(isDoingYoga = it)
                    _yogaScreensStateLiveData.postValue(_yogaScreensState)
                }, {

                })
        }

    fun addWorkout() {
        fitnessService!!.startWorkout("YOGA")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { id ->
                currentWorkoutId = id
                _addWorkoutLiveData.postValue(SimpleResult.Success(Unit))
                asanasProgress = -1
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
        yogaRepository.getWorkoutsByDateRange(firstDay, lastDay)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                { list ->
                    val mapped = list.map {
                        yogaWorkoutToYogaWorkoutUI(it)
                    }
                    _yogaScreensState = _yogaScreensState.copy(pastWeekWorkouts = mapped)
                    _yogaScreensStateLiveData.postValue(_yogaScreensState)
                },
                {
                }
            )
    }

    fun getWorkouts() {
        yogaRepository.getWorkoutsByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //TODO There should be a better way to ensure we don't fetch the current workout
                it.filter { it.id != currentWorkoutId }
            }
            .subscribe(
                { list ->
                    val mapped = list.map {
                        yogaWorkoutToYogaWorkoutUI(it)
                    }
                    _yogaScreensState = _yogaScreensState.copy(pastWorkouts = mapped)
                    _yogaScreensStateLiveData.postValue(_yogaScreensState)
                },
                {
                }
            )
    }

    fun getFullWorkoutsSummary() {
        //TODO I don't know if I should add a currentWorkoutId to the repositories so as to make sure
        // the fetched Observables/LiveDatas aren't prematurely updated before the workout is done
        yogaRepository.getWorkoutsSummaryByDateRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _yogaScreensState = _yogaScreensState.copy(workoutSummary = it)
                    _yogaScreensStateLiveData.postValue(_yogaScreensState)
                },
                {
                }
            )
    }

//    fun getIsDoingYoga() {
//
//    }

    fun getSessionAndAsanaIndex() {
        fitnessService!!.getYogaSessionAndAsanaIndex()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _yogaScreensState = _yogaScreensState.copy(currentSessionIndex = it.first, currentAsanaIndex = it.second)
                    _yogaScreensStateLiveData.postValue(_yogaScreensState)
                },
                {
                }
            )
    }

    fun setSessionAndAsanaIndex(sessionIndex: Int, asanaIndex: Int) {
        Log.d("YogaViewModel", "sessionIndex: $sessionIndex, asanaIndex: $asanaIndex")
        fitnessService!!.setYogaSessionAndAsanaIndex(sessionIndex, asanaIndex)
    }

    fun startAsana(asanaIndex: Int, asanaIdentifier: String, durationMillis: Int) {
        Log.d("YogaViewModel", "startAsana called")
        fitnessService!!.startYogaAsana(asanaIdentifier, durationMillis)
        asanasProgress = asanaIndex
    }

    fun skipAsana() {
        Log.d("YogaViewModel", "skipAsanaCalled")
        _skipLiveData.postValue(SimpleResult.Processing())
        fitnessService!!.skipYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { _ ->
                _skipLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _skipLiveData.postValue(SimpleResult.Failure(it))
            })
    }

    fun pauseAsana() {
        fitnessService!!.pauseCurrentYogaAsana()
    }

    fun resumeAsana() {
        fitnessService!!.resumeCurrentYogaAsana()
    }

    fun getYogaAsanaState() {
        fitnessService!!.getYogaAsanaState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _yogaAsanaStateLiveData.postValue(it)
            }
    }

    fun endAsana() {
        fitnessService!!.endYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endAsanaLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endAsanaLiveData.postValue(SimpleResult.Failure(it))
            })
    }


    fun endCurrentWorkout() {
        fitnessService!!.endYogaAsana()
            .flatMap {
                fitnessService!!.cancelCurrentWorkout()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Success(Unit))
                asanasProgress = -1
            }, {
                _endCurrentWorkoutLiveData.postValue(SimpleResult.Failure(it))
            })
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

    data class YogaScreensState(
        val pastWeekWorkouts: List<YogaWorkoutUI> = emptyList(),
        val pastWorkouts: List<YogaWorkoutUI> = emptyList(),
        val workoutSummary: WorkoutSummary = WorkoutSummary(0, 0, 0),
        val isDoingYoga: Boolean = false,
        val currentSessionIndex: Int = -1,
        val currentAsanaIndex: Int = -1,
    )
}


class YogaViewModelFactory(
    private val yogaRepository: YogaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return YogaViewModel(yogaRepository) as T
    }
}