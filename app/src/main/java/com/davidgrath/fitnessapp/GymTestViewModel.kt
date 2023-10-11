package com.davidgrath.fitnessapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidgrath.fitnessapp.framework.FitnessService
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class GymTestViewModel(
//    private val gymRepository: GymRepository
): ViewModel() {

    var currentWorkoutId: Int = -1
    private set

    //TODO this is basically illegal by architecture standards but I'm not abstracting just yet
    var fitnessBinder: FitnessService.FitnessBinder? = null

    fun addWorkout(name: String? = ""): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.startGymWorkout()
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
        fitnessBinder!!.startGymSet(setIdentifier)
    }

    fun skipSet() {
        fitnessBinder!!.skipGymSet()
    }

    fun endSet(repCount: Int): LiveData<SimpleResult<Unit>> {
        val liveData = MutableLiveData<SimpleResult<Unit>>(SimpleResult.Processing())
        fitnessBinder!!.endGymSet(repCount)
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
        fitnessBinder!!.endGymSet(lastSetRepCount)
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

//class GymTestViewModelFactory(
//    private val gymRepository: GymRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return GymTestViewModel(gymRepository) as T
//    }
//}
