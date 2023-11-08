package com.davidgrath.fitnessapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidgrath.fitnessapp.data.AbstractFitnessService
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class GymTestViewModel(
//    private val gymRepository: GymRepository
): ViewModel() {

    var currentWorkoutId: Long = -1
    private set

    var fitnessService: AbstractFitnessService? = null

    fun addWorkout(name: String? = ""): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.startWorkout("GYM")
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

    fun startSet(setIdentifier: String) {
        fitnessService!!.startGymSet(setIdentifier)
    }

    fun skipSet() {
        fitnessService!!.skipGymSet()
    }

    fun endSet(repCount: Int): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.endGymSet(repCount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ id ->
                liveData.postValue(SimpleResult.Success(Unit))
            }, {
                liveData.postValue(SimpleResult.Failure(it))
            })
        return liveData
    }


    fun endCurrentWorkout(lastSetRepCount: Int): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessService!!.endGymSet(lastSetRepCount)
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

//class GymTestViewModelFactory(
//    private val gymRepository: GymRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return GymTestViewModel(gymRepository) as T
//    }
//}
