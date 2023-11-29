package com.davidgrath.fitnessapp.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.davidgrath.fitnessapp.ui.SplashScreenActivity
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class UserDataRepositoryImpl(private val context: Context): UserDataRepository {

    private val preferences: SharedPreferences
    private val preferencesMap = HashMap<String, Any>()
    private val preferencesMapSubject = BehaviorSubject.create<HashMap<String, Any>>()

    private val sharedPreferencesChangeListener = object: SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String?) {
            when(key) {
                PreferencesTitles.FIRST_NAME, PreferencesTitles.LAST_NAME, PreferencesTitles.EMAIL,
                PreferencesTitles.WEIGHT_UNIT, PreferencesTitles.HEIGHT_UNIT, PreferencesTitles.GENDER -> {
                    val v = sp.getString(key, null)?:""
                    preferencesMap[key] = v
                    preferencesMapSubject.onNext(preferencesMap)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.BIRTH_DATE_DAY, PreferencesTitles.BIRTH_DATE_MONTH, PreferencesTitles.BIRTH_DATE_YEAR,
                PreferencesTitles.HEIGHT -> {
                    val v = sp.getInt(key, 0)
                    preferencesMap[key] = v
                    preferencesMapSubject.onNext(preferencesMap)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.WEIGHT -> {
                    val v = sp.getFloat(key, 0f)
                    preferencesMap[key] = v
                    preferencesMapSubject.onNext(preferencesMap)
                    Log.d(LOG_TAG, "$key: $v")
                }
            }
        }
    }
    init {
        preferences = context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)

        val firstName = preferences.getString(PreferencesTitles.FIRST_NAME, null)?:""
        preferencesMap[PreferencesTitles.FIRST_NAME] = firstName
        val lastName = preferences.getString(PreferencesTitles.LAST_NAME, null)?:""
        preferencesMap[PreferencesTitles.LAST_NAME] = lastName
        val email = preferences.getString(PreferencesTitles.EMAIL, null)?:""
        preferencesMap[PreferencesTitles.EMAIL] = email
        val gender = preferences.getString(PreferencesTitles.GENDER, null)?:""
        preferencesMap[PreferencesTitles.GENDER] = gender
        val weight = preferences.getFloat(PreferencesTitles.WEIGHT, 0f)
        preferencesMap[PreferencesTitles.WEIGHT] = weight
        val weightUnit = preferences.getString(PreferencesTitles.WEIGHT_UNIT, null)?:""
        preferencesMap[PreferencesTitles.WEIGHT_UNIT] = weightUnit
        val height = preferences.getInt(PreferencesTitles.HEIGHT, 0)
        preferencesMap[PreferencesTitles.HEIGHT] = height
        val heightUnit = preferences.getString(PreferencesTitles.HEIGHT_UNIT, null)?:""
        preferencesMap[PreferencesTitles.HEIGHT_UNIT] = heightUnit
        val birthDateDay = preferences.getInt(PreferencesTitles.BIRTH_DATE_DAY, 0)
        preferencesMap[PreferencesTitles.BIRTH_DATE_DAY] = birthDateDay
        val birthDateMonth = preferences.getInt(PreferencesTitles.BIRTH_DATE_MONTH, 0)
        preferencesMap[PreferencesTitles.BIRTH_DATE_MONTH] = birthDateMonth
        val birthDateYear = preferences.getInt(PreferencesTitles.BIRTH_DATE_YEAR, 0)
        preferencesMap[PreferencesTitles.BIRTH_DATE_YEAR] = birthDateYear
        preferencesMapSubject.onNext(preferencesMap)
    }

    override fun getOnboardingStages(): List<String> {
        return STAGES
    }

    override fun getFirstName(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.FIRST_NAME] as String?)?:"" }
    }

    override fun getLastName(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.LAST_NAME] as String?)?:"" }
    }

    override fun getEmail(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.EMAIL] as String?)?:"" }
    }

    override fun getGender(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.GENDER] as String?)?:"" }
    }

    override fun getHeight(): Observable<Int> {
        return preferencesMapSubject.map { (it[PreferencesTitles.HEIGHT] as Int?)?:0 }
    }

    override fun getHeightUnit(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.HEIGHT_UNIT] as String?)?:"" }
    }

    override fun getBirthDateDay(): Observable<Int> {
        return preferencesMapSubject.map { (it[PreferencesTitles.BIRTH_DATE_DAY] as Int?)?:0 }
    }

    override fun getBirthDateMonth(): Observable<Int> {
        return preferencesMapSubject.map { (it[PreferencesTitles.BIRTH_DATE_MONTH] as Int?)?:0 }
    }

    override fun getBirthDateYear(): Observable<Int> {
        return preferencesMapSubject.map { (it[PreferencesTitles.BIRTH_DATE_YEAR] as Int?)?:0 }
    }

    override fun getWeight(): Observable<Float> {
        return preferencesMapSubject.map { (it[PreferencesTitles.WEIGHT] as Float?)?:0f }
    }

    override fun getWeightUnit(): Observable<String> {
        return preferencesMapSubject.map { (it[PreferencesTitles.WEIGHT_UNIT] as String?)?:"" }
    }

    override fun setNameAndEmail(firstName: String, lastName: String, email: String): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putString("firstName", firstName)
                .putString("lastName", lastName)
                .putString("email", email)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun setGender(gender: String): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putString("gender", gender)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun setHeight(height: Int, unit: String): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putInt("height", height)
                .putString("heightUnit", unit)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun setBirthDate(day: Int, month: Int, year: Int): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putInt("birthDateDay", day)
                .putInt("birthDateMonth", month)
                .putInt("birthDateYear", year)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun setWeight(weight: Float, unit: String): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putFloat("weight", weight)
                .putString("weightUnit", unit)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun setAllUserData(firstName: String, lastName: String, email: String,
        gender: String, height: Int, heightUnit: String, day: Int, month: Int,
        year: Int, weight: Float, weightUnit: String): Single<Unit> {
        return Single.create { emitter ->
            preferences.edit()
                .putString(PreferencesTitles.FIRST_NAME, firstName)
                .putString(PreferencesTitles.LAST_NAME, lastName)
                .putString(PreferencesTitles.EMAIL, email)
                .putString(PreferencesTitles.GENDER, gender)
                .putInt(PreferencesTitles.HEIGHT, height)
                .putString(PreferencesTitles.HEIGHT_UNIT, heightUnit)
                .putInt(PreferencesTitles.BIRTH_DATE_DAY, day)
                .putInt(PreferencesTitles.BIRTH_DATE_MONTH, month)
                .putInt(PreferencesTitles.BIRTH_DATE_YEAR, year)
                .putFloat(PreferencesTitles.WEIGHT, weight)
                .putString(PreferencesTitles.WEIGHT_UNIT, weightUnit)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun getNextOnboardingPhase(): String {
        if(preferences.getString(PreferencesTitles.FIRST_NAME, null).isNullOrBlank() ||
                preferences.getString(PreferencesTitles.LAST_NAME, null).isNullOrBlank() ||
                preferences.getString(PreferencesTitles.EMAIL, null).isNullOrBlank()) {
            return "NAME_AND_EMAIL"
        } else if(preferences.getString(PreferencesTitles.GENDER, null).isNullOrBlank()) {
            return "GENDER"
        } else if(preferences.getInt(PreferencesTitles.HEIGHT, 0) <= 0) {
            return "HEIGHT"
        } else if(preferences.getInt(PreferencesTitles.BIRTH_DATE_DAY, 0) <= 0 ||
            preferences.getInt(PreferencesTitles.BIRTH_DATE_MONTH, 0) <= 0 ||
            preferences.getInt(PreferencesTitles.BIRTH_DATE_YEAR, 0) <= 0) {
            return "BIRTH_DATE"
        } else if(preferences.getFloat(PreferencesTitles.WEIGHT, 0F) <= 0F) {
            return "WEIGHT"
        } else {
            return ""
        }
    }

    companion object {
        val LOG_TAG = "UserDataRepositoryImpl"
        val STAGES = listOf("NAME_AND_EMAIL","GENDER", "HEIGHT", "BIRTH_DATE", "WEIGHT")
    }
}