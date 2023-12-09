package com.davidgrath.fitnessapp.util

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.davidgrath.fitnessapp.R
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
    "STANDING_OVERHEAD_CABLE_TRICEPS_EXTENSION" to R.drawable.arms4,
    "CABLE_ROPE_TRICEPS_PUSHDOWN" to R.drawable.arms5,
    "TRICEPS_BENCH_DIPS" to R.drawable.arms6,
    "DEADLIFT" to R.drawable.back1,
    "LAT_PULLDOWN" to R.drawable.back2,
    "SEATED_CABLE_ROW" to R.drawable.back3,
    "SINGLE_ARM_ROW" to R.drawable.back4,
    "BENT_OVER_DUMBBELL_ROW" to R.drawable.back5,
    "CHEST_DIPS" to R.drawable.chest1,
    "BARBELL_BENCH_PRESS" to R.drawable.chest2,
    "DUMBBELL_FLY" to R.drawable.chest3,
    "SEATED_PEC_DECK" to R.drawable.chest4,
    "CABLE_CROSS_OVER" to R.drawable.chest5,
    "BARBELL_SQUAT" to R.drawable.leg1,
    "DUMBELL_LUNGE" to R.drawable.leg2,
    "LEG_PRESS" to R.drawable.leg3,
    "LEG_EXTENSIONS" to R.drawable.leg4,
    "MILITARY_PRESS" to R.drawable.shoulder1,
    "LATERAL_RAISES" to R.drawable.shoulder3,
    "SHOULDER_PRESS" to R.drawable.shoulder2,
    "FRONT_RAISES" to R.drawable.shoulder4,
)

val routineNameToAssetMap = mapOf(
    "arms" to "gym_arms_dollar_gill_unsplash.jpg",
    "legs" to "gym_legs_alora_griffiths_unsplash.jpg",
    "chest" to "gym_chest_alora_griffiths_unsplash.jpg",
    "back" to "gym_back_john_arano_unsplash.jpg",
    "shoulders" to "gym_shoulders_arthur_edelmans_unsplash.jpg"
)

val routineNameToUrlMap = mapOf(
    "arms" to "https://source.unsplash.com/QoW2Sdlh9Nk",
    "legs" to "https://source.unsplash.com/TuzrzArccvc",
    "chest" to "https://source.unsplash.com/V3GnMeRhnjk",
    "back" to "https://source.unsplash.com/h4i9G-de7Po",
    "shoulders" to "https://source.unsplash.com/qfjuh4OLdxw"
)

val workoutNameToAssetMap = mapOf(
    "cycling" to "banner_cycling_viktor_bystrov_unsplash.jpg",
    "gym" to "banner_gym_danielle_cerullo_unsplash.jpg",
    "running" to "banner_running_jenny_hill_unsplash.jpg",
    "swimming" to "banner_swimming_b_mat_an_gelo_unsplash.jpg",
    "walking" to "banner_walking_tyler_nix_unsplash.jpg",
    "yoga" to "banner_yoga_jared_rice_unsplash.jpg"
)

val workoutNameToUrlMap = mapOf(
    "cycling" to "https://source.unsplash.com/Gi0OMNguFaw",
    "gym" to "https://source.unsplash.com/CQfNt66ttZM",
    "running" to "https://source.unsplash.com/mQVWb7kUoOE",
    "swimming" to "https://source.unsplash.com/-BUPaAMSOdE",
    "walking" to "https://source.unsplash.com/VZEj0iepzKA",
    "yoga" to "https://source.unsplash.com/NTyBbu66_SI"
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

fun millisToTimeString(milliseconds: Long): String {
    val duration = if(milliseconds >= 0) {
        milliseconds.toDuration(DurationUnit.MILLISECONDS)
    } else {
        0L.toDuration(DurationUnit.MILLISECONDS)
    }
    var timeString: String = "00:00"
    // I had this assumption that lambdas like below have no guarantee of setting a variable's value
    // I wonder, then, how timeString's initializer above is redundant
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        timeString = if(hours > 0) {
            String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
        } else {
            String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
        }
    }
    return timeString
}

//TODO This function doesn't belong here, but I don't know where else to keep it,
// so it stays here for now
fun tempGetUri(context: Context, preferences: SharedPreferences) : Uri {
    val uriString = preferences.getString(Constants.PreferencesTitles.MEDIA_STORE_TEMP_IMAGE_URI, null)
    val uri: Uri
    if(uriString == null) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            put(MediaStore.Images.Media.DISPLAY_NAME, "temp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1_000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    } else {
        uri = Uri.parse(uriString)
    }
    return uri
}