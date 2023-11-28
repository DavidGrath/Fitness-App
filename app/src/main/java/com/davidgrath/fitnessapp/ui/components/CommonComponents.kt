package com.davidgrath.fitnessapp.ui.components

import android.graphics.drawable.ColorDrawable
import android.view.animation.LinearInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.toCameraOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import android.graphics.Color as AndroidColor

@Composable
fun WorkoutSummaryComponent(
    summary: WorkoutSummary
) {
    Column(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.workout_summary_total), style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(summary.totalCaloriesBurned.toString(), style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.workout_summary_label_calories_burned), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(summary.workoutCount.toString(), style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.workout_summary_label_workout_count), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(summary.timeSpentMinutes.toString(),
                        Modifier.alignByBaseline(),
                        style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                    Text("min",
                        Modifier.alignByBaseline(),
                        style = MaterialTheme.typography.h5, color = MaterialTheme.colors.primary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.workout_summary_label_total_time), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun WeekHistoryComponent(
    startingDate: Calendar,
    workoutDates: List<Calendar>
) {
    Column(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.workout_history_header), style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        val calendars = (0..6).map {  offset ->
            (startingDate.clone() as Calendar).also {
                it.add(Calendar.DAY_OF_YEAR, offset)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            calendars.map { cal ->
                Column(Modifier.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(SimpleDateFormat("EE").format(cal.time).substring(0, 2),
                        color = Color.Gray,
                        style = MaterialTheme.typography.body1.copy(fontSize = 11.sp))
                    Spacer(modifier = Modifier.height(4.dp))
                    val primaryColor = MaterialTheme.colors.primary
                    val workoutHappenedOnDate = workoutDates.any {
                        it.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
                        it.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                    }
                    Box(
                        Modifier
                            .size(24.dp)
                            .drawBehind {
                                drawCircle(Color.Gray, alpha = .5f)
                                if (workoutHappenedOnDate) {
                                    drawCircle(primaryColor, size.width / 3)
                                }
                            }) {

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(cal.get(Calendar.DAY_OF_MONTH).toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WelcomeBanner(
    photoResource: Any,
    bannerButtonText: String,
    onBannerButtonClicked: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        GlideImage(
            model = photoResource,
            contentDescription = "",
            loading = placeholder(ColorDrawable(AndroidColor.YELLOW)),
            failure = placeholder(ColorDrawable(AndroidColor.RED)),
            colorFilter = ColorFilter.tint(Color.Gray, BlendMode.Multiply),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .zIndex(-1f),
        ) {
            it.centerCrop()
        }
        Column(Modifier.align(Alignment.CenterEnd)
            .padding(16.dp)) {
            Text(stringResource(R.string.banner_welcome_back), style = MaterialTheme.typography.h5, color = Color.White)
            Text(stringResource(R.string.banner_last_activity), style = MaterialTheme.typography.caption, color = Color.White)
            Row(horizontalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = R.drawable.baseline_timer_24), contentDescription = "time estimate", colorFilter = ColorFilter.tint(Color.White))
                Spacer(Modifier.width(4.dp))
                Text("120 min", style = MaterialTheme.typography.caption, color = Color.White)
            }
            Row(horizontalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = R.drawable.baseline_bar_chart_24), contentDescription = "difficulty", colorFilter = ColorFilter.tint(Color.White))
                Spacer(Modifier.width(4.dp))
                Text("Beginner", style = MaterialTheme.typography.caption, color = Color.White)
            }
            SimpleGradientButton(onBannerButtonClicked, Modifier) {
                Text(bannerButtonText,
                    style = MaterialTheme.typography.button,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CalendarComponent(
    currentMonth: Calendar,
    onSetCurrentMonth: (Calendar) -> Unit,
    onDateClicked: (Calendar) -> Unit,
    highlightedDates: Array<Calendar>,
    firstDayOfWeek: Int,
    timeZoneId: String,
    minDate: Calendar? = null,
    maxDate: Calendar? = null
) {
    val sdfDays = SimpleDateFormat("dd")
    val sdfMonth = SimpleDateFormat("yyyy/MM")
    val defaultWeek = arrayOf(Calendar.SUNDAY,Calendar.MONDAY,Calendar.TUESDAY,
        Calendar.WEDNESDAY,Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY)
    val defaultWeekNames = mapOf(
        Calendar.SUNDAY to "Su",
        Calendar.MONDAY to "Mo",
        Calendar.TUESDAY to "Tu",
        Calendar.WEDNESDAY to "We",
        Calendar.THURSDAY to "Th",
        Calendar.FRIDAY to "Fr",
        Calendar.SATURDAY to "Sa"
    )
    val index = defaultWeek.indexOf(firstDayOfWeek)
    val weekArr = Array(7) { -1 }
    for(i in 0..6) {
        val pos = (index+i) % 7
        weekArr[i] = defaultWeek[pos]
    }
    val monthMatrix =
        fillMonth(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH), firstDayOfWeek, timeZoneId)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        ) {
            val prevMonth : Calendar? = if(minDate != null) {
                if (currentMonth.get(Calendar.MONTH) > minDate.get(Calendar.MONTH) &&
                    currentMonth.get(Calendar.YEAR) >= minDate.get(Calendar.YEAR)) {
                    (currentMonth.clone() as Calendar).also { it.add(Calendar.MONTH, -1) }
                } else {
                    null
                }
            } else {
                (currentMonth.clone() as Calendar).also { it.add(Calendar.MONTH, -1) }
            }
            val prevMonthModifier = if(prevMonth != null) {
                Modifier.clickable {
                    onSetCurrentMonth(prevMonth)
                }
            } else {
                Modifier
            }
            val prevMonthAlpha = if(prevMonth != null) {
                1f
            } else {
                .5f
            }
            Image(
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                contentDescription = "",
                Modifier
                    .rotate(180F)
                    .then(prevMonthModifier),
                alpha = prevMonthAlpha
            )
            Text(sdfMonth.format(currentMonth.time), style = MaterialTheme.typography.body1)
            val nextMonth : Calendar? = if(maxDate != null) {
                if (currentMonth.get(Calendar.MONTH) < maxDate.get(Calendar.MONTH) &&
                    currentMonth.get(Calendar.YEAR) <= maxDate.get(Calendar.YEAR)) {
                    (currentMonth.clone() as Calendar).also { it.add(Calendar.MONTH, 1) }
                } else {
                    null
                }
            } else {
                (currentMonth.clone() as Calendar).also { it.add(Calendar.MONTH, 1) }
            }
            val nextMonthModifier = if(nextMonth != null) {
                Modifier.clickable {
                    onSetCurrentMonth(nextMonth)
                }
            } else {
                Modifier
            }
            val nextMonthAlpha = if(nextMonth != null) {
                1f
            } else {
                .5f
            }
            Image(
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                contentDescription = "",
                Modifier.then(nextMonthModifier),
                alpha = nextMonthAlpha
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            weekArr.forEach {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    Alignment.Center
                ) {
                    Text(
                        defaultWeekNames[it] ?: "",
                        Modifier
                            .padding(8.dp),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        AnimatedContent(targetState = currentMonth,
            transitionSpec = {
                val compare = if(targetState.get(Calendar.YEAR) == initialState.get(Calendar.YEAR)) {
                    targetState.get(Calendar.MONTH).compareTo(initialState.get(Calendar.MONTH))
                } else if(targetState.get(Calendar.YEAR) < initialState.get(Calendar.YEAR)) {
                    -1
                } else {
                    1
                }
                if(compare > 0) { // e.g November 2023 -> October 2023
                    slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                } else { // e.g October 2023 -> November 2023
                    slideInHorizontally { width -> -width } togetherWith  slideOutHorizontally { width -> width }
                }
            }
        ) { currentMonth ->
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                monthMatrix.forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        week.forEach { date ->
                            val highlighted = highlightedDates.any {
                                it.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) &&
                                        it.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                            }
                            val modifier = if (highlighted) {
                                val color = MaterialTheme.colors.primary
                                Modifier.drawBehind {
                                    drawCircle(color)
                                }
                            } else {
                                Modifier
                            }
                            val color =
                                if (currentMonth.get(Calendar.MONTH) == date.get(Calendar.MONTH)) {
                                    Color.Black
                                } else {
                                    Color.Gray
                                }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                Alignment.Center
                            ) {
                                Text(
                                    sdfDays.format(date.time),
                                    Modifier
                                        .padding(8.dp)
                                        .then(modifier)
                                        .padding(8.dp)
                                        .clickable {
                                            onDateClicked(date)
                                        },
                                    style = MaterialTheme.typography.body1.copy(fontSize = 11.sp),
                                    textAlign = TextAlign.Center,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fillMonth(year: Int, month: Int, firstDayOfWeek: Int, timeZoneId: String) : List<List<Calendar>> {
    val monthMatrix = mutableListOf(
        mutableListOf<Calendar>()
    )
    var monthMatrixCounter = 0
    val defaultWeek = arrayOf(Calendar.SUNDAY,Calendar.MONDAY,Calendar.TUESDAY,
        Calendar.WEDNESDAY,Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY)
    val index = defaultWeek.indexOf(firstDayOfWeek)
    val weekArr = Array(7) { -1 }
    for(i in 0..6) {
        val pos = (index+i) % 7
        weekArr[i] = defaultWeek[pos]
    }


    val currMonthCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).also {
        it.set(year, month, 1)
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
    val lastDayCalendar = (currMonthCalendar.clone() as Calendar).also {
        it.set(Calendar.DAY_OF_MONTH, it.getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    val prevMonthCalendar = (currMonthCalendar.clone() as Calendar).also {
        it.add(Calendar.DAY_OF_MONTH, -1)
    }
    val nextMonthCalendar = (lastDayCalendar.clone() as Calendar).also {
        it.add(Calendar.DAY_OF_MONTH, 1)
    }
    val weekdayOfFirstDay = currMonthCalendar.get(Calendar.DAY_OF_WEEK)
    val weekdayOfLastDay = lastDayCalendar.get(Calendar.DAY_OF_WEEK)
    var pos = weekArr.indexOf(weekdayOfFirstDay)
    if(pos > 0) {
        val leftoverDaysCount = pos
        val startingMonthDay = prevMonthCalendar.get(Calendar.DAY_OF_MONTH)-(leftoverDaysCount-1)
        for(i in startingMonthDay..prevMonthCalendar.get(Calendar.DAY_OF_MONTH)) {
            val row = monthMatrixCounter / 7
            val col = monthMatrixCounter % 7
            if(col == 0) {
                monthMatrix.add(mutableListOf())
            }
            monthMatrix[row].add(
                (prevMonthCalendar.clone() as Calendar).also {
                    it.set(Calendar.DAY_OF_MONTH, i)
                }
            )
            monthMatrixCounter++
        }
    }
    for(i in 1..lastDayCalendar.get(Calendar.DAY_OF_MONTH)) {
        val row = monthMatrixCounter / 7
        val col = monthMatrixCounter % 7
        if(col == 0) {
            monthMatrix.add(mutableListOf())
        }
        monthMatrix[row].add(
        (currMonthCalendar.clone() as Calendar).also {
            it.set(Calendar.DAY_OF_MONTH, i)
        }
        )
        monthMatrixCounter++
    }
    pos = weekArr.indexOf(weekdayOfLastDay)
    println(pos)
    if(pos < 6) {
        val additiveDaysCount = 6-pos
        for(i in 1..additiveDaysCount) {
            val row = monthMatrixCounter / 7
            val col = monthMatrixCounter % 7
            if(col == 0) {
                monthMatrix.add(mutableListOf())
            }
            monthMatrix[row].add(
                (nextMonthCalendar.clone() as Calendar).also {
                it.set(Calendar.DAY_OF_MONTH, i)
                }
            )
            monthMatrixCounter++
        }
    }
    return monthMatrix
}


@Composable
fun SimpleAppBar(
    title: String,
    expanded: Boolean,
    centeredWhileCollapsed: Boolean = false,
    textColor: Color = MaterialTheme.colors.primary,
    onBackClicked: () -> Unit
) {
    Surface(elevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        if(expanded) {

            Column {
                Image(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "back",
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.Start)
                        .rotate(180F)
                        .clickable {
                            onBackClicked()
                        }

                )
                val verticalPadding = if (expanded) {
                    24.dp
                } else {
                    0.dp
                }
                Text(
                    title,
                    Modifier
                        .padding(vertical = verticalPadding)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.h4,
                    color = textColor
                )
            }
        } else {
            Row(Modifier.padding(vertical = 16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "back",
                    Modifier
                        .padding(16.dp)
                        .rotate(180F)
                        .clickable {
                            onBackClicked()
                        }

                )
                val textAlign = if(centeredWhileCollapsed) {
                    TextAlign.Start
                } else {
                    TextAlign.Center
                }
                Text(
                    title,
                    style = MaterialTheme.typography.h4.copy(textAlign = textAlign),
                    color = textColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SimpleGradientButton(
    onClicked: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    SimpleGradientButton(onClicked, modifier, 8.dp, true, content)
}

@Composable
fun SimpleGradientButton(
    onClicked: () -> Unit,
    modifier: Modifier,
    cornerRadius: Dp,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    val colors = listOf(
        Color(0xFF49EEAF),
        Color(0xFF24D18F)
    )
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Box(
        modifier
            .background(brush = Brush.linearGradient(colors), RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                onClick = onClicked,
                interactionSource = interactionSource,
                indication = rememberRipple(),
                enabled = enabled
            )
            .padding(horizontal = 32.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


const val ZOOM_ANIMATION_DURATION = 400L
const val MIN_ZOOM_LEVEL = 0.0
const val MAX_ZOOM_LEVEL = 22.0
const val DEFAULT_ZOOM = 15.0

val CameraStateSaver = Saver<CameraState?, Array<Float>> ( //Float[] because there's no Double[] in Bundle
    save = {
        if(it == null) {
            emptyArray<Float>()
        } else {
            arrayOf(
                it.bearing.toFloat(), it.zoom.toFloat(),
                it.center.latitude().toFloat(), it.center.longitude().toFloat(),
                it.pitch.toFloat(), it.padding.top.toFloat(), it.padding.left.toFloat(),
                it.padding.bottom.toFloat(), it.padding.right.toFloat()
            )
        }
    },
    restore = {
        if(it.isEmpty()) {
            null
        } else {
            val bearing = it[0].toDouble()
            val zoom = it[1].toDouble()
            val point = Point.fromLngLat(it[3].toDouble(), it[2].toDouble())
            val pitch = it[4].toDouble()
            val padding =
                EdgeInsets(it[5].toDouble(), it[6].toDouble(), it[7].toDouble(), it[8].toDouble())
            CameraState(point, padding, zoom, bearing, pitch)
        }
    }
)

@Composable
fun MapViewComponent(
    isTrackingLocation: Boolean,
    locationData: List<LocationDataUI>,
    modifier: Modifier
) {

    val (zoomInEnabled, setZoomInEnabled) = remember {
        mutableStateOf(true)
    }
    val (zoomOutEnabled, setZoomOutEnabled) = remember {
        mutableStateOf(true)
    }

    val (polylineAnnotationManager, setPolylineAnnotationManager) = remember {
        mutableStateOf<PolylineAnnotationManager?>(null)
    }
    val (cameraPlugin, setCameraPlugin) = remember {
        mutableStateOf<CameraAnimationsPlugin?>(null)
    }
    val (mapboxMap, setMapboxMap) = remember {
        mutableStateOf<MapboxMap?>(null)
    }
    val (cameraState, setCameraState) = rememberSaveable(stateSaver = CameraStateSaver) {
        mutableStateOf<CameraState?>(null)
    }

    Box(modifier) {
        AndroidView(factory = {
            val mapView = MapView(it, null)
            val locationComponentPlugin = mapView.location
            locationComponentPlugin.updateSettings {
                this.enabled = true
                this.locationPuck = LocationPuck2D(
                    bearingImage = AppCompatResources.getDrawable(
                        it,
                        R.drawable.baseline_navigation_24
                    )
                )
            }
            mapView.camera.addCameraZoomChangeListener {
                if (it < MAX_ZOOM_LEVEL) {
                    setZoomInEnabled(true)
                } else {
                    setZoomInEnabled(false)
                }
                if (it > MIN_ZOOM_LEVEL) {
                    setZoomOutEnabled(true)
                } else {
                    setZoomOutEnabled(false)
                }
            }
            mapView.getMapboxMap().addOnCameraChangeListener {
                setCameraState(mapView.getMapboxMap().cameraState)
            }
            setPolylineAnnotationManager(mapView.annotations.createPolylineAnnotationManager())
            setCameraPlugin(mapView.camera)
            setMapboxMap(mapView.getMapboxMap())
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
            if(cameraState != null) {
                mapView.getMapboxMap().setCamera(cameraState.toCameraOptions())
            }
            mapView
        },
            update = { mapView ->
//                val polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
//                val cameraPlugin = mapView.camera
//                val mapboxMap = mapView.getMapboxMap()

                if (isTrackingLocation) {
                    if (locationData.isEmpty()) {

                    } else if (locationData.size == 1) {
                        val points = locationData.map {
                            Point.fromLngLat(it.longitude, it.latitude)
                        }
                        cameraPlugin?.let {
                            val centerAnimatorOptions =
                                CameraAnimatorOptions.cameraAnimatorOptions(points.last())
                            val centerAnimator =
                                cameraPlugin.createCenterAnimator(centerAnimatorOptions) {
                                    duration = ZOOM_ANIMATION_DURATION
                                    interpolator = LinearInterpolator()
                                }
                            cameraPlugin.registerAnimators(centerAnimator)
                            centerAnimator.start()
                        }
                    } else {
                        val points = locationData.map {
                            Point.fromLngLat(it.longitude, it.latitude)
                        }
                        cameraPlugin?.let {
                            val centerAnimatorOptions =
                                CameraAnimatorOptions.cameraAnimatorOptions(points.last())
                            val centerAnimator =
                                cameraPlugin.createCenterAnimator(centerAnimatorOptions) {
                                    duration = ZOOM_ANIMATION_DURATION
                                    interpolator = LinearInterpolator()
                                }
                            cameraPlugin.registerAnimators(centerAnimator)
                            centerAnimator.start()
                        }
                        polylineAnnotationManager?.let {
                            // val simplified = PolylineUtils.simplify(points)
                            val polylineAnnotationOptions =
                                PolylineAnnotationOptions().withPoints(
                                    points
                                )
                                    .withLineWidth(5.0)
                                    .withLineColor(android.graphics.Color.BLACK)
                            polylineAnnotationManager.create(
                                polylineAnnotationOptions
                            )
                        }
                    }
                } else {
                    polylineAnnotationManager?.deleteAll()
                }
            })

        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .border(2.dp, Color.Black)
        ) {
            val zoomInAlpha = if (zoomInEnabled) {
                1f
            } else {
                .6f
            }
            val zoomInClickable = if(zoomInEnabled) {
                Modifier.clickable {
                    val currentZoom = mapboxMap?.cameraState?.zoom?: DEFAULT_ZOOM
                    if (currentZoom < MAX_ZOOM_LEVEL) {
                        val newZoom = if (currentZoom + 1 >= MAX_ZOOM_LEVEL) {
                            MAX_ZOOM_LEVEL
                        } else {
                            currentZoom + 1
                        }
                        cameraPlugin?.let {
                            val zoomAnimatorOptions =
                                CameraAnimatorOptions.cameraAnimatorOptions(newZoom)
                            val zoomAnimator = cameraPlugin.createZoomAnimator(zoomAnimatorOptions) {
                                duration = ZOOM_ANIMATION_DURATION
                                interpolator = LinearInterpolator()
                            }
                            cameraPlugin.registerAnimators(zoomAnimator)
                            zoomAnimator.start()
                        }
                    }
                }
            } else {
                Modifier
            }
            Box(
                Modifier
                    .background(Color.White)
                    .padding(4.dp)
                    .alpha(zoomInAlpha)
                    .then(zoomInClickable)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_zoom_in_24),
                    contentDescription = "zoom in"
                )
            }
            val zoomOutAlpha = if (zoomOutEnabled) {
                1f
            } else {
                .6f
            }
            val zoomOutClickable = if(zoomOutEnabled) {
                Modifier.clickable {
                    val currentZoom = mapboxMap?.cameraState?.zoom?: DEFAULT_ZOOM
                    if(currentZoom > MIN_ZOOM_LEVEL) {
                        val newZoom = if(currentZoom - 1 <= MIN_ZOOM_LEVEL) {
                            MIN_ZOOM_LEVEL
                        } else {
                            currentZoom - 1
                        }
                        cameraPlugin?.let {
                            val zoomAnimatorOptions =
                                CameraAnimatorOptions.cameraAnimatorOptions(newZoom)
                            val zoomAnimator =
                                cameraPlugin.createZoomAnimator(zoomAnimatorOptions) {
                                    duration = ZOOM_ANIMATION_DURATION
                                    interpolator = LinearInterpolator()
                                }
                            cameraPlugin.registerAnimators(zoomAnimator)
                            zoomAnimator.start()
                        }
                    }
                }
            } else {
                Modifier
            }
            Box(
                Modifier
                    .background(Color.White)
                    .padding(4.dp)
                    .alpha(zoomOutAlpha)
                    .then(zoomOutClickable)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_zoom_out_24),
                    contentDescription = "zoom out"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    isEmail: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.h5.copy(MaterialTheme.colors.primary),
    onDone: (() -> Unit) = { }
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val focusManager = LocalFocusManager.current
    val keyboardOptions = if(isEmail) {
        KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
    } else {
        KeyboardOptions(imeAction = ImeAction.Next)
    }
    BasicTextField(
        value = value, onValueChange = onValueChange,
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onAny = {
            if(!focusManager.moveFocus(FocusDirection.Down)) {
                focusManager.clearFocus()
            }
            onDone()
        }),
        modifier = modifier
            .indicatorLine(
                true,
                false,
                interactionSource,
                TextFieldDefaults.textFieldColors(),
                2.dp,
                2.dp
            )) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(bottom = 4.dp),
        )
    }
}


//https://stackoverflow.com/a/70031663/7876958
//https://stackoverflow.com/a/73230181/7876958
@Composable
fun animateAlignmentAsState(targetAlignment: Alignment): State<BiasAlignment> {
    targetAlignment as BiasAlignment
    val horizontal = animateFloatAsState(targetAlignment.horizontalBias)
    val vertical = animateFloatAsState(targetAlignment.verticalBias)
    return derivedStateOf { BiasAlignment(horizontal.value, vertical.value) }
}

/*@Preview(widthDp = 600)
@Composable
fun WeekHistoryPreview() {
    val start = Calendar.getInstance()
    val dates = (0..2).map { offset ->
        (start.clone() as Calendar).also {
            it.add(Calendar.DAY_OF_YEAR, offset)
        }
    }
    Box(Modifier.fillMaxSize()) {
        FitnessAppTheme {
            WeekHistoryComponent(startingDate = start, workoutDates = dates)
        }
    }
}*/

/*
@Preview()
@Composable
fun WorkoutSummaryPreview() {
    val summary = WorkoutSummary(200, 5, 25)
    Box(Modifier.fillMaxSize()) {
        FitnessAppTheme {
            WorkoutSummaryComponent(summary = summary)
        }
    }
}*/

/*@Preview()
@Composable
fun WelcomeBannerPreview() {
    Box(Modifier.fillMaxSize()) {
        FitnessAppTheme {
            WelcomeBanner("Start Swimming", {})
        }
    }
}*/

@Preview()
@Composable
fun CalendarComponentPreview() {
    val dates = mutableListOf<Calendar>()
    val calendar = Calendar.getInstance()
    dates.add(calendar)
    dates.add((calendar.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, 1)})
    dates.add((calendar.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, 2)})
    dates.add((calendar.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, 20)})
    dates.add((calendar.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, 13)})
//    Box(Modifier.fillMaxSize()) {
//        FitnessAppTheme {
//            CalendarComponent(calendar, {}, dates.toTypedArray(), Calendar.SUNDAY, TimeZone.getDefault().id)
//        }
//    }
}

fun NavHostController.navigateSingleTopTo(route: String) {
    return navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}