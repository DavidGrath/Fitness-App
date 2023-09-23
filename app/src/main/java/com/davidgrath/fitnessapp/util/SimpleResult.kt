package com.davidgrath.fitnessapp.util

sealed class SimpleResult<T> {
    class Success<T>(data: T) : SimpleResult<T>()
    class Processing<T>() : SimpleResult<T>()
    class Failure<T>(throwable: Throwable?) : SimpleResult<T>()
}