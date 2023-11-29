package com.davidgrath.fitnessapp.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles

class SettingsViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private var _settingsScreensState = SettingsScreensState()
    private val _settingsScreensStateLiveData = MutableLiveData<SettingsScreensState>()
    val settingsScreensStateLiveData : LiveData<SettingsScreensState> = _settingsScreensStateLiveData
    private val preferences: SharedPreferences
    private val preferencesChangeListener = object: SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String?) {
            when(key) {
                PreferencesTitles.WEIGHT_UNIT -> {
                    val v = sp.getString(key, null)?:Constants.UNIT_WEIGHT_KG
                    _settingsScreensState = _settingsScreensState.copy(weightUnit = v)
                    _settingsScreensStateLiveData.postValue(_settingsScreensState)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.TEMPERATURE_UNIT -> {
                    val v = sp.getString(key, null)?:Constants.UNIT_TEMPERATURE_CELSIUS
                    _settingsScreensState = _settingsScreensState.copy(temperatureUnit = v)
                    _settingsScreensStateLiveData.postValue(_settingsScreensState)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.DISTANCE_UNIT -> {
                    val v = sp.getString(key, null)?:Constants.UNIT_DISTANCE_KILOMETERS
                    _settingsScreensState = _settingsScreensState.copy(distanceUnit = v)
                    _settingsScreensStateLiveData.postValue(_settingsScreensState)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.SHOULD_SYNC_TO_GOOGLE_FIT -> {
                    val v = sp.getBoolean(key, false)
                    _settingsScreensState = _settingsScreensState.copy(shouldSyncToGoogleFit = v)
                    _settingsScreensStateLiveData.postValue(_settingsScreensState)
                    Log.d(LOG_TAG, "$key: $v")
                }
                PreferencesTitles.SHOULD_REMIND_TO_WORKOUT -> {
                    val v = sp.getBoolean(key, false)
                    _settingsScreensState = _settingsScreensState.copy(shouldRemindForWorkouts = v)
                    _settingsScreensStateLiveData.postValue(_settingsScreensState)
                    Log.d(LOG_TAG, "$key: $v")
                }
            }
        }
    }

    init {
        preferences = application.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
        _settingsScreensState = SettingsScreensState(
            preferences.getBoolean(PreferencesTitles.SHOULD_SYNC_TO_GOOGLE_FIT, false),
            preferences.getBoolean(PreferencesTitles.SHOULD_REMIND_TO_WORKOUT, false),
            preferences.getString(PreferencesTitles.DISTANCE_UNIT, null)?:Constants.UNIT_DISTANCE_KILOMETERS,
            preferences.getString(PreferencesTitles.TEMPERATURE_UNIT, null)?:Constants.UNIT_TEMPERATURE_CELSIUS,
            preferences.getString(PreferencesTitles.WEIGHT_UNIT, null)?:Constants.UNIT_WEIGHT_KG,
        )
        _settingsScreensStateLiveData.postValue(_settingsScreensState)
        preferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    fun setIsSyncedToGoogleFit(isSyncedToGoogleFit: Boolean) {
        preferences.edit()
            .putBoolean(PreferencesTitles.SHOULD_SYNC_TO_GOOGLE_FIT, isSyncedToGoogleFit)
            .apply()
    }

    fun setShouldRemindForWorkouts(shouldRemindForWorkouts: Boolean) {
        preferences.edit()
            .putBoolean(PreferencesTitles.SHOULD_REMIND_TO_WORKOUT, shouldRemindForWorkouts)
            .apply()
    }

    fun setWeightUnit(weightUnit: String) {
        preferences.edit()
            .putString(PreferencesTitles.WEIGHT_UNIT, weightUnit)
            .apply()
    }

    fun setTemperatureUnit(temperatureUnit: String) {
        preferences.edit()
            .putString(PreferencesTitles.TEMPERATURE_UNIT, temperatureUnit)
            .apply()
    }

    fun setDistanceUnit(distanceUnit: String) {
        preferences.edit()
            .putString(PreferencesTitles.DISTANCE_UNIT, distanceUnit)
            .apply()
    }

    data class SettingsScreensState(
        val shouldSyncToGoogleFit: Boolean = false,
        val shouldRemindForWorkouts: Boolean = false,
        val distanceUnit: String = Constants.UNIT_DISTANCE_KILOMETERS,
        val temperatureUnit: String = Constants.UNIT_TEMPERATURE_CELSIUS,
        val weightUnit: String = Constants.UNIT_WEIGHT_KG
    )

    companion object {
        private const val LOG_TAG = "SettingsViewModel"
    }
}