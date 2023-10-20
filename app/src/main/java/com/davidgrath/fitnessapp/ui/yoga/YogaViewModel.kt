package com.davidgrath.fitnessapp.ui.yoga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidgrath.fitnessapp.data.YogaRepository
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.framework.database.entities.YogaWorkout
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

class YogaViewModel(
    private val yogaRepository: YogaRepository
): ViewModel() {

    var currentWorkoutId: Long = -1
        private set

    private val _addWorkoutLiveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
    val addWorkoutLiveData : LiveData<SimpleResult<Unit>> = _addWorkoutLiveData

    private val _pastWeekWorkoutsLiveData = MutableLiveData<SimpleResult<List<YogaWorkout>>>()
    val pastWeekWorkoutsLiveData: LiveData<SimpleResult<List<YogaWorkout>>> = _pastWeekWorkoutsLiveData

    private val _pastWorkoutsLiveData = MutableLiveData<SimpleResult<List<YogaWorkout>>>(
        SimpleResult.Processing())
    val pastWorkoutsLiveData : LiveData<SimpleResult<List<YogaWorkout>>> = _pastWorkoutsLiveData

    private val _fullWorkoutSummaryLiveData = MutableLiveData<SimpleResult<WorkoutSummary>>(
        SimpleResult.Processing())
    val fullWorkoutSummaryLiveData : LiveData<SimpleResult<WorkoutSummary>> = _fullWorkoutSummaryLiveData

    private val _isDoingYogaLiveData = MutableLiveData<Boolean>(false)
    val isDoingYogaLiveData : LiveData<Boolean> = _isDoingYogaLiveData

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

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    fun addWorkout(name: String? = "") {
        fitnessBinder!!.startYogaWorkout(name?:"")
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
        yogaRepository.getWorkoutsByDateRange(firstDay, lastDay)
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
        yogaRepository.getWorkoutsByDateRange()
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
        yogaRepository.getWorkoutsSummaryByDateRange()
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

    fun getIsDoingYoga() {
        //TODO Add LiveDateReactiveStreams
        fitnessBinder!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it == "YOGA"
            }
            .subscribe({
                _isDoingYogaLiveData.postValue(it)
            }, {

            })
    }

    fun startAsana(asanaIdentifier: String, durationMillis: Int) {
        fitnessBinder!!.startYogaAsana(asanaIdentifier, durationMillis)
    }

    fun skipAsana() {
        _skipLiveData.postValue(SimpleResult.Processing())
        fitnessBinder!!.skipYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { _ ->
                _skipLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _skipLiveData.postValue(SimpleResult.Failure(it))
            })
    }

    fun pauseAsana() {
        fitnessBinder!!.pauseCurrentYogaAsana()
    }

    fun resumeAsana() {
        fitnessBinder!!.resumeCurrentYogaAsana()
    }

    fun getYogaAsanaState() {
        fitnessBinder!!.getYogaAsanaState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _yogaAsanaStateLiveData.postValue(it)
            }
    }

    fun endAsana() {
        fitnessBinder!!.endYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                _endAsanaLiveData.postValue(SimpleResult.Success(Unit))
            }, {
                _endAsanaLiveData.postValue(SimpleResult.Failure(it))
            })
    }


    fun endCurrentWorkout() {
        fitnessBinder!!.endYogaAsana()
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
}


class YogaViewModelFactory(
    private val yogaRepository: YogaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return YogaViewModel(yogaRepository) as T
    }
}