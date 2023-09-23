package com.davidgrath.fitnessapp.data

import io.reactivex.rxjava3.core.Single

interface UserDataRepository {
    fun getOnboardingStages(): List<String>
    fun getFirstName(): String
    fun getLastName(): String
    fun getEmail(): String
    fun getGender(): String
    fun getHeight(): Int
    fun getHeightUnit(): String
    fun getBirthDateDay(): Int
    fun getBirthDateMonth(): Int
    fun getBirthDateYear(): Int
    fun getWeight(): Float
    fun getWeightUnit(): String
    fun setNameAndEmail(firstName: String, lastName: String, email: String): Single<Unit>
    fun setGender(gender: String): Single<Unit>
    fun setHeight(height: Int, unit: String): Single<Unit>
    fun setBirthDate(day: Int, month: Int, year: Int): Single<Unit>
    fun setWeight(weight: Float, unit: String): Single<Unit>
    fun setAllUserData(firstName: String, lastName: String, email: String, gender: String,
                       height: Int, heightUnit: String, day: Int, month: Int, year: Int,
                       weight: Float, weightUnit: String): Single<Unit>

    fun getNextOnboardingPhase() : String
}

