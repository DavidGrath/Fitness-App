package com.davidgrath.fitnessapp.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.davidgrath.fitnessapp.data.UserDataRepository
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModel
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.centimetersToInches
import com.davidgrath.fitnessapp.util.inchesToCentimeters
import com.davidgrath.fitnessapp.util.kilogramsToPounds
import com.davidgrath.fitnessapp.util.poundsToKilograms
import com.davidgrath.fitnessapp.util.tempGetUri
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class ProfileViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    private var profileScreenState = ProfileScreenState()
    private val _profileScreenStateLiveData = MutableLiveData<ProfileScreenState>()
    val profileScreenStateLiveData : LiveData<ProfileScreenState> = _profileScreenStateLiveData

    init {
        userDataRepository.getFirstName()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(firstName = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getLastName()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(lastName = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getEmail()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(email = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getGender()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(gender = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getHeight()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(height = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getHeightUnit()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(heightUnit = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getBirthDateDay()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(birthDateDay = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getBirthDateMonth()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(birthDateMonth = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getBirthDateYear()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(birthDateYear = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getWeight()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(weight = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getWeightUnit()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(weightUnit = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getAvatar()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(userAvatar = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getAvatarType()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(userAvatarType = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
        userDataRepository.getAvatarFileExists()
            .subscribeOn(Schedulers.io())
            .subscribe({
                profileScreenState = profileScreenState.copy(userAvatarFileExists = it)
                _profileScreenStateLiveData.postValue(profileScreenState)
            }, {
                Log.e(LOG_TAG, it.message, it)
            })
    }

    fun setFirstName(firstName: String) {
        profileScreenState = profileScreenState.copy(firstName = firstName)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setLastName(lastName: String) {
        profileScreenState = profileScreenState.copy(lastName = lastName)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setEmail(email: String) {
        profileScreenState = profileScreenState.copy(email = email)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setGender(gender: String) {
        profileScreenState = profileScreenState.copy(gender = gender)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setHeightAndUnit(height: Int, unit: String) {
        profileScreenState = profileScreenState.copy(height = height, heightUnit = unit)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setHeightUnit(unit: String) {
        var h = profileScreenState.height
        if(profileScreenState.heightUnit == Constants.UNIT_HEIGHT_CENTIMETERS && unit == Constants.UNIT_HEIGHT_INCHES) {
            h = centimetersToInches(h)
        } else if(profileScreenState.heightUnit == Constants.UNIT_HEIGHT_INCHES && unit == Constants.UNIT_HEIGHT_CENTIMETERS) {
            h = inchesToCentimeters(h)
        }
        profileScreenState = profileScreenState.copy(height = h, heightUnit = unit)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setHeight(height: Int) {
        profileScreenState = profileScreenState.copy(height = height)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setBirthDate(day: Int, month: Int, year: Int) {
        profileScreenState = profileScreenState.copy(birthDateDay = day, birthDateMonth = month, birthDateYear = year)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setWeight(weight: Float) {
        profileScreenState = profileScreenState.copy(weight = weight)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setWeightUnit(unit: String) {
        var w = profileScreenState.weight
        if(profileScreenState.weightUnit == Constants.UNIT_WEIGHT_KG && unit == Constants.UNIT_WEIGHT_POUNDS) {
            w = kilogramsToPounds(w)
        } else if(profileScreenState.weightUnit == Constants.UNIT_WEIGHT_POUNDS && unit == Constants.UNIT_WEIGHT_KG) {
            w = poundsToKilograms(w)
        }
        profileScreenState = profileScreenState.copy(weight = w, weightUnit = unit)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }

    fun setWeightAndUnit(weight: Float, unit: String) {
        profileScreenState = profileScreenState.copy(weight = weight, weightUnit = unit)
        _profileScreenStateLiveData.postValue(profileScreenState)
    }
    fun submitNameAndEmail(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setNameAndEmail(profileScreenState.firstName, profileScreenState.lastName, profileScreenState.email)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitGender(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setGender(profileScreenState.gender)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitHeight(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setHeight(profileScreenState.height, profileScreenState.heightUnit)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitBirthDate(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setBirthDate(profileScreenState.birthDateDay, profileScreenState.birthDateMonth, profileScreenState.birthDateYear)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitWeight(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setWeight(profileScreenState.weight, profileScreenState.weightUnit)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitAvatarType(type: String, defaultAvatarId: String? = null): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setAvatarType(type)
            .flatMap {
                if(type == "default" && defaultAvatarId != null) {
                    userDataRepository.setAvatar(defaultAvatarId)
                } else if(type == "none"){
                    userDataRepository.setAvatar(null)
                } else {
                    Single.just(Unit)
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun setAvatarFile(inputStream: InputStream, avatarFile: File, preferences: SharedPreferences): LiveData<SimpleResult<Unit>> {
        val single = Single.create<Unit> { emitter ->
            if(!avatarFile.exists()) {
                avatarFile.createNewFile()
            }
            val o = avatarFile.outputStream()
            inputStream.copyTo(o)
            inputStream.close()
            o.close()
            emitter.onSuccess(Unit)
        }
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        single
            .subscribeOn(Schedulers.io())
            .subscribe({
                preferences.edit()
                    .putString(PreferencesTitles.MEDIA_STORE_TEMP_IMAGE_URI, null)
                    .commit()
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
                Log.e(LOG_TAG, it.message, it)
            })
        return _liveData
    }

    companion object {
        private const val LOG_TAG = "ProfileViewModel"
    }
}

class ProfileViewModelFactory(
    private val userDataRepository: UserDataRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userDataRepository) as T
    }
}