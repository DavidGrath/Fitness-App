package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.UserDataRepository
import com.davidgrath.fitnessapp.util.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class UserDataRepositoryTestImpl: UserDataRepository {

    private val preferencesMap = HashMap<String, Any>()
    private val preferencesMapSubject = BehaviorSubject.create<HashMap<String, Any>>()



    override fun getOnboardingStages(): List<String> {
        return STAGES
    }

    override fun getFirstName(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.FIRST_NAME] as String?)?:"" }
    }

    override fun getLastName(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.LAST_NAME] as String?)?:"" }
    }

    override fun getEmail(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.EMAIL] as String?)?:"" }
    }

    override fun getGender(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.GENDER] as String?)?:"" }
    }

    override fun getHeight(): Observable<Int> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.HEIGHT] as Int?)?:0 }
    }

    override fun getHeightUnit(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.HEIGHT_UNIT] as String?)?:"" }
    }

    override fun getBirthDateDay(): Observable<Int> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.BIRTH_DATE_DAY] as Int?)?:0 }
    }

    override fun getBirthDateMonth(): Observable<Int> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.BIRTH_DATE_MONTH] as Int?)?:0 }
    }

    override fun getBirthDateYear(): Observable<Int> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.BIRTH_DATE_YEAR] as Int?)?:0 }
    }

    override fun getWeight(): Observable<Float> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.WEIGHT] as Float?)?:0f }
    }

    override fun getWeightUnit(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.WEIGHT_UNIT] as String?)?:"" }
    }

    override fun getAvatar(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.USER_AVATAR] as String?)?:"" }
    }

    override fun getAvatarType(): Observable<String> {
        return preferencesMapSubject.map { (it[Constants.PreferencesTitles.USER_AVATAR_TYPE] as String?)?:"" }
    }

    override fun setNameAndEmail(firstName: String, lastName: String, email: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap["firstName"] = firstName
            preferencesMap["lastName"] = lastName
            preferencesMap["email"] = email
            emitter.onSuccess(Unit)
        }
    }

    override fun setGender(gender: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap["gender"] = gender
            emitter.onSuccess(Unit)
        }
    }

    override fun setHeight(height: Int, unit: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap["height"] = height
            preferencesMap["heightUnit"] = unit
            emitter.onSuccess(Unit)
        }
    }

    override fun setBirthDate(day: Int, month: Int, year: Int): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap["birthDateDay"] = day
            preferencesMap["birthDateMonth"] = month
            preferencesMap["birthDateYear"] = year
            emitter.onSuccess(Unit)
        }
    }

    override fun setWeight(weight: Float, unit: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap["weight"] = weight
            preferencesMap["weightUnit"] = unit
            emitter.onSuccess(Unit)
        }
    }

    override fun setUserUuid(uuid: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap[Constants.PreferencesTitles.CURRENT_USER_UUID] = uuid
            emitter.onSuccess(Unit)
        }
    }

    override fun setAvatar(userAvatar: String?): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap[Constants.PreferencesTitles.USER_AVATAR] = userAvatar?:""
            emitter.onSuccess(Unit)
        }
    }

    override fun getAvatarFileExists(): Observable<Boolean> {
        return Observable.just(true)
    }

    override fun setAvatarType(userAvatarType: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap[Constants.PreferencesTitles.USER_AVATAR_TYPE] = userAvatarType
            emitter.onSuccess(Unit)
        }
    }

    override fun setAllUserData(firstName: String, lastName: String, email: String,
                                gender: String, height: Int, heightUnit: String, day: Int, month: Int,
                                year: Int, weight: Float, weightUnit: String): Single<Unit> {
        return Single.create { emitter ->
            preferencesMap[Constants.PreferencesTitles.FIRST_NAME] = firstName
            preferencesMap[Constants.PreferencesTitles.LAST_NAME] = lastName
            preferencesMap[Constants.PreferencesTitles.EMAIL] = email
            preferencesMap[Constants.PreferencesTitles.GENDER] = gender
            preferencesMap[Constants.PreferencesTitles.HEIGHT] = height
            preferencesMap[Constants.PreferencesTitles.HEIGHT_UNIT] = heightUnit
            preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_DAY] = day
            preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_MONTH] = month
            preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_YEAR] = year
            preferencesMap[Constants.PreferencesTitles.WEIGHT] = weight
            preferencesMap[Constants.PreferencesTitles.WEIGHT_UNIT] = weightUnit
            emitter.onSuccess(Unit)
        }
    }

    override fun getNextOnboardingPhase(): String {
        if((preferencesMap[Constants.PreferencesTitles.FIRST_NAME] as String?).isNullOrBlank() ||
            (preferencesMap[Constants.PreferencesTitles.LAST_NAME] as String?).isNullOrBlank() ||
            (preferencesMap[Constants.PreferencesTitles.EMAIL] as String?).isNullOrBlank()) {
            return "NAME_AND_EMAIL"
        } else if((preferencesMap[Constants.PreferencesTitles.GENDER] as String?).isNullOrBlank()) {
            return "GENDER"
        } else if(((preferencesMap[Constants.PreferencesTitles.HEIGHT] as Int?) ?: 0) <= 0) {
            return "HEIGHT"
        } else if(((preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_DAY] as Int?) ?: 0) <= 0 ||
            ((preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_MONTH] as Int?) ?: 0) <= 0 ||
            ((preferencesMap[Constants.PreferencesTitles.BIRTH_DATE_YEAR] as Int?) ?: 0) <= 0
        ) {
            return "BIRTH_DATE"
        } else if(((preferencesMap[Constants.PreferencesTitles.WEIGHT] as Float?) ?: 0F) <= 0F) {
            return "WEIGHT"
        } else {
            return ""
        }
    }

    companion object {
        val LOG_TAG = "UserDataRepoTestImpl"
        val STAGES = listOf("NAME_AND_EMAIL","GENDER", "HEIGHT", "BIRTH_DATE", "WEIGHT")
    }
}