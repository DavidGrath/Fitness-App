package com.davidgrath.fitnessapp.util

import com.davidgrath.fitnessapp.R
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.floor
import kotlin.math.roundToInt

fun inchesToFeetAndInches(inches: Int) : Pair<Int, Int> {
    return (inches / 12) to (inches % 12)
}

fun feetAndInchesToInches(feet: Int, inches: Int) : Int {
    return (feet * 12) + inches
}


fun poundsToKilograms(quantity: Float) : Float {
    return quantity / 2.20462f
}

fun kilogramsToPounds(quantity: Float) : Float {
    return quantity * 2.20462f
}

fun inchesToCentimeters(quantity: Int) : Int {
    return (quantity * 2.54f).roundToInt()
}

fun centimetersToInches(quantity: Int) : Int {
    return (quantity / 2.54f).roundToInt()
}

//Today I learned why BigDecimal exists - floats are unreliable
fun getTensPart(number: Float): Int {
    val bigDecimal = BigDecimal(number.toString())
    val bigDecimalFloored = BigDecimal(floor(number).toString())
    return bigDecimal.subtract(bigDecimalFloored).multiply(BigDecimal.TEN).toInt()
}

//https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double {
    val R = 6371
    val deltaLat = Math.toRadians(Math.abs(lat2-lat1))
    val deltaLon = Math.toRadians(Math.abs(lon2-lon1))
    val a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(deltaLon/2) * Math.sin(deltaLon/2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    val d = R * c
    return d
}

val setIdentifierToIconMap = mapOf(
    "DUMBBELL_CURL" to R.drawable.arms1,
    "HAMMER_CURL" to R.drawable.arms2,
    "BARBELL_CURL" to R.drawable.arms3,
//    "BARBELL_CURL" to R.drawable.arms4,
    "CABLE_ROPE_TRICEPS_PUSHDOWN" to R.drawable.arms5,
    "TRICEPS_BENCH_DIPS" to R.drawable.arms6,
    "DEADLIFT" to R.drawable.back1,
    "LAT_PULLDOWN" to R.drawable.back2,
//    "LAT_PULLDOWN" to R.drawable.back3,
    "SINGLE_ARM_ROW" to R.drawable.back4,
    "BENT_OVER_DUMBBELL_ROW" to R.drawable.back5,
    "CHEST_DIPS" to R.drawable.chest1,
    "BARBELL_BENCH_PRESS" to R.drawable.chest2,
    "DUMBBELL_FLY" to R.drawable.chest3,
    "SEATED_PEC_DECK" to R.drawable.chest4,
//    "SEATED_PEC_DECK" to R.drawable.chest5,
    "BARBELL_SQUAT" to R.drawable.leg1,
    "DUMBELL_LUNGE" to R.drawable.leg2,
    "LEG_PRESS" to R.drawable.leg3,
    "LEG_EXTENSIONS" to R.drawable.leg4,
    "MILITARY_PRESS" to R.drawable.shoulder1,
    "LATERAL_RAISES" to R.drawable.shoulder3,
    "SHOULDER_PRESS" to R.drawable.shoulder2,
    "FRONT_RAISES" to R.drawable.shoulder4,
)

fun dateAsStart(date: Date) : Date {
    val calendar = Calendar.getInstance()
    calendar.timeZone = TimeZone.getTimeZone("UTC")
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun dateAsEnd(date: Date) : Date {
    val calendar = Calendar.getInstance()
    calendar.timeZone = TimeZone.getTimeZone("UTC")
    calendar.time = date
    calendar.add(Calendar.DATE, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.MILLISECOND, -1)
    return calendar.time
}