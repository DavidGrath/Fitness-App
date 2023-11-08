package com.davidgrath.fitnessapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidgrath.fitnessapp.data.AbstractFitnessService
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

    var fitnessService: AbstractFitnessService? = null

    fun addWorkout(name: String? = ""): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.startWorkout("YOGA")
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
        fitnessService!!.startYogaAsana(asanaIdentifier, durationMillis)
    }

    fun skipAsana(): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.skipYogaAsana()
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
        fitnessService!!.pauseCurrentYogaAsana()
    }

    fun resumeAsana() {
        fitnessService!!.resumeCurrentYogaAsana()
    }

    fun getYogaAsanaState() : LiveData<YogaAsanaState> {
        val liveData = MutableLiveData<YogaAsanaState>()
        fitnessService!!.getYogaAsanaState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                liveData.postValue(it)
            }
        return liveData
    }

    fun endAsana(): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.endYogaAsana()
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
        fitnessService!!.endYogaAsana()
            .flatMap {
                fitnessService!!.cancelCurrentWorkout()
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