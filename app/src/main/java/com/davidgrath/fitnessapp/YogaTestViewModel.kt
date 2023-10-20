package com.davidgrath.fitnessapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidgrath.fitnessapp.data.YogaRepository
import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class YogaTestViewModel(
//    private val yogaRepository: YogaRepository
): ViewModel() {

    var currentWorkoutId: Long = -1
        private set

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    fun addWorkout(name: String? = ""): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.startYogaWorkout(name?:"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { id ->
                currentWorkoutId = id
                liveData.postValue(SimpleResult.Success(Unit))
            }, {
                liveData.postValue(SimpleResult.Failure(it))
            })

        return liveData
    }

    fun startAsana(asanaIdentifier: String, durationMillis: Int) {
        fitnessBinder!!.startYogaAsana(asanaIdentifier, durationMillis)
    }

    fun skipAsana(): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.skipYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { _ ->
                liveData.postValue(SimpleResult.Success(Unit))
            }, {
                liveData.postValue(SimpleResult.Failure(it))
            })

        return liveData
    }

    fun pauseAsana() {
        fitnessBinder!!.pauseCurrentYogaAsana()
    }

    fun resumeAsana() {
        fitnessBinder!!.resumeCurrentYogaAsana()
    }

    fun getYogaAsanaState() : LiveData<YogaAsanaState> {
        val liveData = MutableLiveData<YogaAsanaState>()
        fitnessBinder!!.getYogaAsanaState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                liveData.postValue(it)
            }
        return liveData
    }

    fun endAsana(): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.endYogaAsana()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                liveData.postValue(SimpleResult.Success(Unit))
            }, {
                liveData.postValue(SimpleResult.Failure(it))
            })
        return liveData
    }


    fun endCurrentWorkout(): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.endYogaAsana()
            .flatMap {
                fitnessBinder!!.cancelCurrentWorkout()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                liveData.postValue(SimpleResult.Success(Unit))
            }, {
                liveData.postValue(SimpleResult.Failure(it))
            })
        return liveData

    }
}