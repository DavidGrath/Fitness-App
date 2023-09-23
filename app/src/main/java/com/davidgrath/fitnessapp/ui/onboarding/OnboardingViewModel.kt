package com.davidgrath.fitnessapp.ui.onboarding

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.UserDataRepository
import com.davidgrath.fitnessapp.util.SimpleResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class OnboardingViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private var screenState : OnboardingScreenState
    private val _screenStateLiveData : MutableLiveData<OnboardingScreenState>
    val screenStateLiveData: LiveData<OnboardingScreenState>
    private val _submitLiveData = MutableLiveData<SimpleResult<Unit>>()
    val submitLiveData : LiveData<SimpleResult<Unit>> = _submitLiveData

    init {
        var initialIndex = getOnboardingStages().indexOf(userDataRepository.getNextOnboardingPhase())
        if(initialIndex == -1) {
            initialIndex = 0
        }
        screenState = OnboardingScreenState(
            getOnboardingStages().size,
            initialIndex,
            userDataRepository.getFirstName(),
            userDataRepository.getLastName(),
            userDataRepository.getEmail(),
            userDataRepository.getGender(),
            userDataRepository.getHeight(),
            userDataRepository.getHeightUnit(),
            userDataRepository.getBirthDateDay(),
            userDataRepository.getBirthDateMonth(),
            userDataRepository.getBirthDateYear(),
            userDataRepository.getWeight(),
            userDataRepository.getWeightUnit()
        )
        _screenStateLiveData = MutableLiveData<OnboardingScreenState>(screenState)
        screenStateLiveData = _screenStateLiveData
    }

    fun getOnboardingStages() : List<String> {
        return userDataRepository.getOnboardingStages()
    }

    fun validateGender() : Boolean {
        return screenState.gender.isNotBlank()
    }

    fun validateNameAndEmail(): Boolean {
        return (screenState.firstName.isNotBlank()
                && screenState.lastName.isNotBlank()
                && screenState.email.isNotBlank()
                && Patterns.EMAIL_ADDRESS.matcher(screenState.email.trim()).matches())
    }

    fun goToNextPage() {
        val size = getOnboardingStages().size

        if(screenState.currentPageIndex + 1 in 0 until size) {
            screenState = screenState.copy(currentPageIndex = screenState.currentPageIndex + 1)
            _screenStateLiveData.postValue(screenState)
        } else if(screenState.currentPageIndex + 1 == size) {
            //TODO Move to separate submit method or learn how to consume state in a "one-off" manner
            _submitLiveData.postValue(SimpleResult.Processing())
            userDataRepository.setAllUserData(
                screenState.firstName.trim(), screenState.lastName.trim(), screenState.email.trim(),
                screenState.gender, screenState.height, screenState.heightUnit,
                screenState.birthDateDay, screenState.birthDateMonth, screenState.birthDateYear,
                screenState.weight, screenState.weightUnit
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _submitLiveData.postValue(SimpleResult.Success(Unit))
                }, {
                    _submitLiveData.postValue(SimpleResult.Failure(it))
                })

        }
    }

    fun goToPreviousPage() {
        val size = getOnboardingStages().size

        if(screenState.currentPageIndex - 1 in 0 until size) {
            screenState = screenState.copy(currentPageIndex = screenState.currentPageIndex - 1)
            _screenStateLiveData.postValue(screenState)
        }
    }



    fun setFirstName(firstName: String) {
        screenState = screenState.copy(firstName = firstName)
        _screenStateLiveData.postValue(screenState)
    }

    fun setLastName(lastName: String) {
        screenState = screenState.copy(lastName = lastName)
        _screenStateLiveData.postValue(screenState)
    }

    fun setEmail(email: String) {
        screenState = screenState.copy(email = email)
        _screenStateLiveData.postValue(screenState)
    }

    fun setGender(gender: String) {
        screenState = screenState.copy(gender = gender)
        _screenStateLiveData.postValue(screenState)
    }

    fun setHeight(height: Int, unit: String) {
        screenState = screenState.copy(height = height, heightUnit = unit)
        _screenStateLiveData.postValue(screenState)
    }

    fun setBirthDate(day: Int, month: Int, year: Int) {
        screenState = screenState.copy(birthDateDay = day, birthDateMonth = month, birthDateYear = year)
        _screenStateLiveData.postValue(screenState)
    }

    fun setWeight(weight: Float, unit: String) {
        screenState = screenState.copy(weight = weight, weightUnit = unit)
        _screenStateLiveData.postValue(screenState)
    }


    fun submitNameAndEmail(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setNameAndEmail(screenState.firstName, screenState.lastName, screenState.email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        userDataRepository.setGender(screenState.gender)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        userDataRepository.setHeight(screenState.height, screenState.heightUnit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        userDataRepository.setBirthDate(screenState.birthDateDay, screenState.birthDateMonth, screenState.birthDateYear)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        userDataRepository.setWeight(screenState.weight, screenState.weightUnit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }

    fun submitAllUserData(): LiveData<SimpleResult<Unit>> {
        val _liveData = MutableLiveData<SimpleResult<Unit>>()
        _liveData.postValue(SimpleResult.Processing())
        userDataRepository.setAllUserData(
            screenState.firstName, screenState.lastName, screenState.email,
            screenState.gender, screenState.height, screenState.heightUnit,
            screenState.birthDateDay, screenState.birthDateMonth, screenState.birthDateYear,
            screenState.weight, screenState.weightUnit
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _liveData.postValue(SimpleResult.Success(Unit))
            }, {
                _liveData.postValue(SimpleResult.Failure(it))
            })
        return _liveData
    }


}

class OnboardingViewModelFactory(
    private val userDataRepository: UserDataRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(userDataRepository) as T
    }
}