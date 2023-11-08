package com.davidgrath.fitnessapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel: ViewModel() {

    var fitnessService: AbstractFitnessService? = null
        set(value) {
            field = value
            value!!.getCurrentWorkoutObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _chooseActivityScreenState = _chooseActivityScreenState.copy(ongoingWorkout = it)
                    _chooseActivityStateLiveData.postValue(_chooseActivityScreenState)
                }, {

                })
        }

    private var _chooseActivityScreenState = ChooseActivityScreenState()
    private val _chooseActivityStateLiveData = MutableLiveData(_chooseActivityScreenState)
    val chooseActivityStateLiveData : LiveData<ChooseActivityScreenState> = _chooseActivityStateLiveData

    /*fun getCurrentWorkout() {
        fitnessService!!.getCurrentWorkoutObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chooseActivityScreenState = chooseActivityScreenState.copy(ongoingWorkout = it)
                _chooseActivityStateLiveData.postValue(chooseActivityScreenState)
            }, {

            })
    }*/

    fun setHasReadOngoingWorkout() {
        _chooseActivityScreenState = _chooseActivityScreenState.copy(hasReadOngoingWorkout = true)
        _chooseActivityStateLiveData.postValue(_chooseActivityScreenState)
    }

    data class ChooseActivityScreenState(
        val hasReadOngoingWorkout: Boolean = false,
        val ongoingWorkout: String = ""
    )
}