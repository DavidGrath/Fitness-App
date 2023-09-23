package com.davidgrath.fitnessapp.data

import android.content.Context
import android.content.SharedPreferences
import com.davidgrath.fitnessapp.util.Constants
import io.reactivex.rxjava3.core.Single

class UserDataRepositoryImpl(private val context: Context): UserDataRepository {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun getOnboardingStages(): List<String> {
        return STAGES
    }

    override fun getFirstName(): String {
        return preferences.getString("firstName", null)?:""
    }

    override fun getLastName(): String {
        return preferences.getString("lastName", null)?:""
    }

    override fun getEmail(): String {
        return preferences.getString("email", null)?:""
    }

    override fun getGender(): String {
        return preferences.getString("gender", null)?:""
    }

    override fun getHeight(): Int {
        return preferences.getInt("height", 0)
    }

    override fun getHeightUnit(): String {
        return preferences.getString("heightUnit", null)?:Constants.UNIT_HEIGHT_CENTIMETERS
    }

    override fun getBirthDateDay(): Int {
        return preferences.getInt("birthDateDay", 0)
    }

    override fun getBirthDateMonth(): Int {
        return preferences.getInt("birthDateMonth", 0)
    }

    override fun getBirthDateYear(): Int {
        return preferences.getInt("birthDateYear", 0)
    }

    override fun getWeight(): Float {
        return preferences.getFloat("weight", 0.0F)
    }

    override fun getWeightUnit(): String {
        return preferences.getString("weightUnit", null)?:Constants.UNIT_WEIGHT_KG
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
                .putString(Constants.PreferencesTitles.FIRST_NAME, firstName)
                .putString(Constants.PreferencesTitles.LAST_NAME, lastName)
                .putString(Constants.PreferencesTitles.EMAIL, email)
                .putString(Constants.PreferencesTitles.GENDER, gender)
                .putInt(Constants.PreferencesTitles.HEIGHT, height)
                .putString(Constants.PreferencesTitles.HEIGHT_UNIT, heightUnit)
                .putInt(Constants.PreferencesTitles.BIRTH_DATE_DAY, day)
                .putInt(Constants.PreferencesTitles.BIRTH_DATE_MONTH, month)
                .putInt(Constants.PreferencesTitles.BIRTH_DATE_YEAR, year)
                .putFloat(Constants.PreferencesTitles.WEIGHT, weight)
                .putString(Constants.PreferencesTitles.WEIGHT_UNIT, weightUnit)
                .commit()
            emitter.onSuccess(Unit)
        }
    }

    override fun getNextOnboardingPhase(): String {
        if(preferences.getString(Constants.PreferencesTitles.FIRST_NAME, null).isNullOrBlank() ||
                preferences.getString(Constants.PreferencesTitles.LAST_NAME, null).isNullOrBlank() ||
                preferences.getString(Constants.PreferencesTitles.EMAIL, null).isNullOrBlank()) {
            return "NAME_AND_EMAIL"
        } else if(preferences.getString(Constants.PreferencesTitles.GENDER, null).isNullOrBlank()) {
            return "GENDER"
        } else if(preferences.getInt(Constants.PreferencesTitles.HEIGHT, 0) <= 0) {
            return "HEIGHT"
        } else if(preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_DAY, 0) <= 0 ||
            preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_MONTH, 0) <= 0 ||
            preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_YEAR, 0) <= 0) {
            return "BIRTH_DATE"
        } else if(preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0F) <= 0F) {
            return "WEIGHT"
        } else {
            return ""
        }
    }

    companion object {
        val STAGES = listOf("NAME_AND_EMAIL","GENDER", "HEIGHT", "BIRTH_DATE", "WEIGHT")
    }
}