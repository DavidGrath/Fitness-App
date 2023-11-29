package com.davidgrath.fitnessapp.data

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface UserDataRepository {
    fun getOnboardingStages(): List<String>
    fun getFirstName(): Observable<String>
    fun getLastName(): Observable<String>
    fun getEmail(): Observable<String>
    fun getGender(): Observable<String>
    fun getHeight(): Observable<Int>
    fun getHeightUnit(): Observable<String>
    fun getBirthDateDay(): Observable<Int>
    fun getBirthDateMonth(): Observable<Int>
    fun getBirthDateYear(): Observable<Int>
    fun getWeight(): Observable<Float>
    fun getWeightUnit(): Observable<String>
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

