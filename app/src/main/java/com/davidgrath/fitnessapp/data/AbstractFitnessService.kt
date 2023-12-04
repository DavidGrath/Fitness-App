package com.davidgrath.fitnessapp.data

import com.davidgrath.fitnessapp.data.entities.YogaAsanaState
import com.davidgrath.fitnessapp.framework.database.entities.GymSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Date
import java.util.concurrent.TimeUnit

interface AbstractFitnessService {
    fun getCurrentWorkoutObservable() : Observable<String>

    fun getCurrentTimeElapsedObservable() : Observable<Long>

    fun cancelCurrentWorkout() : Single<Unit>

    fun startWorkout(type: String) : Single<Long>

    fun startYogaAsana(asanaIdentifier: String, durationMillis: Int)

    fun skipYogaAsana() : Single<Long>

    fun pauseCurrentYogaAsana()

    fun resumeCurrentYogaAsana()

    fun getYogaAsanaState() : Observable<YogaAsanaState>

    fun incrementYogaTimeLeft(additionalTimeMillis: Int)

    fun endYogaAsana() : Single<Long>
    fun setYogaSessionAndAsanaIndex(sessionIndex: Int, asanaIndex: Int)
    fun getYogaSessionAndAsanaIndex(): Observable<Pair<Int, Int>>

    fun startGymSet(setIdentifier: String)

    fun skipGymSet()

    fun endGymSet(repCount: Int) : Single<Long>
    fun setGymRoutineAndSetIndex(routineIndex: Int, setIndex: Int)
    fun getGymRoutineAndSetIndex(): Observable<Pair<Int, Int>>
    fun getRunningIdObservable() : Observable<Long>
    fun getWalkingIdObservable() : Observable<Long>
    fun getSwimmingIdObservable() : Observable<Long>
    fun getCyclingIdObservable() : Observable<Long>
    fun getGymIdObservable() : Observable<Long>
    fun getYogaIdObservable() : Observable<Long>
}