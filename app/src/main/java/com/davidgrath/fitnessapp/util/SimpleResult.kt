package com.davidgrath.fitnessapp.util

sealed class SimpleResult<T> {
    class Success<T>(val data: T) : SimpleResult<T>()
    class Processing<T>() : SimpleResult<T>()
    class Failure<T>(val throwable: Throwable?) : SimpleResult<T>()
}