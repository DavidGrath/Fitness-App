package com.davidgrath.fitnessapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.data.entities.WorkoutSummary
import com.davidgrath.fitnessapp.ui.entities.LocationDataUI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@Composable
fun WorkoutSummaryComponent(
    summary: WorkoutSummary
) {
    Column(Modifier.fillMaxWidth()) {
        Text("Total", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(summary.totalCaloriesBurned.toString(), style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Kcal", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(summary.workoutCount.toString(), style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Workouts", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
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
                Text("Time", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
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
        Text("History", style = MaterialTheme.typography.body1)
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

@Composable
fun WelcomeBanner(
    bannerButtonText: String,
    onBannerButtonClicked: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(16.dp)
    ) {
        Column(Modifier.align(Alignment.CenterEnd)) {
            Text("Welcome back", style = MaterialTheme.typography.h5, color = Color.White)
            Text("last activity", style = MaterialTheme.typography.caption, color = Color.White)
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
                    color = MaterialTheme.colors.primary
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
                Text(
                    title,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary
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
    val colors = listOf(
        Color(0xFF49EEAF),
        Color(0xFF24D18F)
    )
    Button(onClicked,
        modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            Modifier
                .background(brush = Brush.linearGradient(colors), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun TempLocationListItem(
    locationData: LocationDataUI
) {
    Row(Modifier.fillMaxWidth()) {
        Text(locationData.latitude.toString(),
            Modifier.weight(1f),
            style = MaterialTheme.typography.body1
        )
        Text(locationData.longitude.toString(),
            Modifier.weight(1f),
            style = MaterialTheme.typography.body1
        )
    }
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