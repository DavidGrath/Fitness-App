package com.davidgrath.fitnessapp.ui.onboarding

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
import android.widget.NumberPicker
import android.widget.NumberPicker.Formatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.feetAndInchesToInches
import com.davidgrath.fitnessapp.util.getTensPart
import com.davidgrath.fitnessapp.util.inchesToFeetAndInches
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.floor


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    screenState: OnboardingScreenState,
    stages: List<String>,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    setGender: (String) -> Unit,
    setHeight: (Int) -> Unit,
    setHeightUnit: (String) -> Unit,
    setDay: (Int) -> Unit,
    setMonth: (Int) -> Unit,
    setYear: (Int) -> Unit,
    setWeight: (Float) -> Unit,
    setWeightUnit: (String) -> Unit,
    validateNameAndEmail: () -> Boolean,
    validateGender: () -> Boolean,
    goNext: () -> Unit,
    goPrevious: () -> Unit
) {
    val pagerState = rememberPagerState {
        screenState.pageCount
    }
    LaunchedEffect(screenState.currentPageIndex) {
        pagerState.animateScrollToPage(screenState.currentPageIndex)
    }

    val canGoPrevious = screenState.currentPageIndex > 0
//    val canGoNext = screenState.currentPageIndex < stages.size - 1
//    val canGoNext = screenState.currentPageIndex <= stages.size - 1
    val canGoNext = true
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.next_previous),
                contentDescription = "previous",
                Modifier
                    .padding(start = 16.dp)
                    .graphicsLayer {
                        rotationZ = 180F
                    }
                    .then(if (canGoPrevious) {
                        Modifier.clickable {
                            goPrevious()
                        }
                    } else {
                        Modifier
                    }),
                tint = if (canGoPrevious) {
                    Color.Black
                } else {
                    Color.Gray
                }
            )
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = R.drawable.next_previous),
                contentDescription = "previous",
                Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        goNext()
                    },
//                    .then(if (canGoNext) {
//                        Modifier.clickable {
//                            when (stages[screenState.currentPageIndex]) {
//                                "NAME_AND_EMAIL" -> {
//                                    if (validateNameAndEmail()) {
//                                        coroutineScope.launch {
//                                            pagerState.scrollToPage(screenState.currentPageIndex+1)
//                                        }
//                                        goNext()
//                                    }
//                                }
//
//                                "GENDER" -> {
//                                    if (validateGender()) {
//                                        coroutineScope.launch {
//                                            pagerState.scrollToPage(screenState.currentPageIndex+1)
//                                        }
//                                        goNext()
//                                    }
//                                }
//
//                                else -> {
//                                    coroutineScope.launch {
//                                        pagerState.scrollToPage(screenState.currentPageIndex+1)
//                                    }
//                                    goNext()
//                                }
//                            }
//                        }
//                    } else {
//                        Modifier
//                    }),
                tint = if (canGoNext) {
                    Color.Black
                } else {
                    Color.Gray
                }
            )
        }
        Box(Modifier.weight(1F)) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false, contentPadding = PaddingValues(16.dp)) { page ->
                when(page) {
                    0 -> {
                        NameAndEmailScreen(
                            screenState.firstName, onFirstNameChange, screenState.lastName,
                            onLastNameChange, screenState.email, onEmailChange
                        )
                    }
                    1 -> {
                        GenderScreen(screenState.gender, setGender)
                    }
                    2 -> {
                        HeightScreen(screenState.height, setHeight, screenState.heightUnit, setHeightUnit)
                    }
                    3 -> {
                        BirthDateScreen(
                            screenState.birthDateDay, setDay,
                            screenState.birthDateMonth, setMonth,
                            screenState.birthDateYear, setYear)
                    }
                    4 -> {
                        WeightScreen(
                            screenState.weight, setWeight, unit = screenState.weightUnit, setWeightUnit
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 56.dp)) {
            stages.forEachIndexed { i, stage ->
//                val color = arrayOf(Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Red).random()
                val color = if(screenState.currentPageIndex == i) {
                    MaterialTheme.colors.primary
                } else {
                    Color(0x111111FF)
                }
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .border(
                            BorderStroke(16.dp, color = color),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .size(32.dp, 12.dp)


                ) {

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NameAndEmailScreen(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()) {
        Text(
            text = "Enter your details",
            Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "First Name", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        val firstNameInteractionSource = remember {
            MutableInteractionSource()
        }
        BasicTextField(
            value = firstName, onValueChange = onFirstNameChange,
            interactionSource = firstNameInteractionSource,
            textStyle = LocalTextStyle.current
                .copy(MaterialTheme.colors.primary, fontSize = MaterialTheme.typography.h5.fontSize),
            modifier = Modifier
                .fillMaxWidth()
                .indicatorLine(
                    true,
                    false,
                    firstNameInteractionSource,
                    TextFieldDefaults.textFieldColors(),
                    2.dp,
                    2.dp
                )) { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = firstName,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = firstNameInteractionSource,
                contentPadding = PaddingValues(bottom = 4.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Last Name", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        val lastNameInteractionSource = remember {
            MutableInteractionSource()
        }
        BasicTextField(
            value = lastName, onValueChange = onLastNameChange,
            Modifier
                .fillMaxWidth()
                .indicatorLine(
                    true,
                    false,
                    lastNameInteractionSource,
                    TextFieldDefaults.textFieldColors(), 2.dp, 2.dp
                ),
            textStyle = LocalTextStyle.current
                .copy(MaterialTheme.colors.primary, fontSize = MaterialTheme.typography.h5.fontSize),
            interactionSource = lastNameInteractionSource) { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = lastName,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = lastNameInteractionSource,
                contentPadding = PaddingValues(bottom = 4.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        val emailInteractionSource = remember {
            MutableInteractionSource()
        }
        BasicTextField(
            value = email, onValueChange = onEmailChange,
            Modifier
                .fillMaxWidth()
                .indicatorLine(
                    true, false, emailInteractionSource, TextFieldDefaults.textFieldColors(),
                    2.dp, 2.dp
                ),
            textStyle = LocalTextStyle.current
                .copy(MaterialTheme.colors.primary, fontSize = MaterialTheme.typography.h5.fontSize),
            interactionSource = emailInteractionSource) { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = email,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = emailInteractionSource,
                contentPadding = PaddingValues(bottom = 4.dp),
            )
        }
    }
}

@Composable
fun GenderScreen(
    gender: String,
    setGender: (String) -> Unit
) {
    val primaryColor = MaterialTheme.colors.primary
//    val primaryColor = Color.Red
    val otherColor = Color.Black
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Choose Gender", style = MaterialTheme.typography.h4)
        Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            val maleIconColor = if (gender.equals("male", true)) {
                primaryColor
            } else {
                otherColor
            }
            val femaleIconColor = if (gender.equals("female", true)) {
                primaryColor
            } else {
                otherColor
            }
            Icon(
                painter = painterResource(id = R.drawable.face),
                contentDescription = "male",
                Modifier
                    .clickable {
                        setGender("male")
                    }
                    .padding(16.dp)
                    .size(56.dp),
                tint = maleIconColor
            )
            Icon(
                painter = painterResource(id = R.drawable.face_woman),
                contentDescription = "female",
                Modifier
                    .clickable {
                        setGender("female")
                    }
                    .padding(16.dp)
                    .size(56.dp),
                tint = femaleIconColor
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeightScreen(
    height: Int, setHeight: (Int) -> Unit,
    unit: String, setUnit: (String) -> Unit
) {

    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }
    val units = arrayOf(
        Constants.UNIT_HEIGHT_INCHES to "Imperial (ft)",
        Constants.UNIT_HEIGHT_CENTIMETERS to "Metric (cm)"
    )

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,) {
        Text(
            text = "Choose Height",
            style = MaterialTheme.typography.h4
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "Height", style = MaterialTheme.typography.h5, modifier = Modifier.align(Alignment.Start))
            val currentUnitText = if (unit == Constants.UNIT_HEIGHT_INCHES) {
                "Imperial (ft)"
            } else if (unit == Constants.UNIT_HEIGHT_CENTIMETERS) {
                "Metric (cm)"
            } else {
                "Metric (cm)"
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { it -> setExpanded(!expanded) },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 8.dp)) {
                Row(Modifier.clickable { }) {
                    Text(text = currentUnitText)
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "drop down"
                    )
                }
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { }) {
                    units.forEach { (u, text) ->
                        DropdownMenuItem(onClick = {
                            setUnit(u)
                            setExpanded(false)
                        }) {
                            Text(text = text)
                        }
                    }

                }
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                if (unit == Constants.UNIT_HEIGHT_INCHES) {
                    //TODO Maybe add the min and max as util constants
                    var initialFeetAndInches = inchesToFeetAndInches(height)
                    if(initialFeetAndInches.first !in (1..6)) {
                        initialFeetAndInches = 1 to initialFeetAndInches.second
                    }
                    if(initialFeetAndInches.second !in (0..11)) {
                        initialFeetAndInches = initialFeetAndInches.first to 0
                    }
                    val adjustedHeight = feetAndInchesToInches(initialFeetAndInches.first, initialFeetAndInches.second)
                    setHeight(adjustedHeight)
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = false
                            view.minValue = 1
                            view.maxValue = 6
                            view.setFormatter { num ->
                                "$num '"
                            }
                            view
                        },
                        update = {
                            //Reads height, has to be put in 'update'
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentInches = inchesToFeetAndInches(height).second
                                setHeight(feetAndInchesToInches(new, currentInches))
                            }
                            it.value = inchesToFeetAndInches(height).first
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = true
                            view.minValue = 0
                            view.maxValue = 11
                            view.setFormatter { num ->
                                "$num  \""
                            }
                            view
                        },
                        update = {
                            //Reads height, has to be put in 'update'
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentFeet = inchesToFeetAndInches(height).first
                                setHeight(feetAndInchesToInches(currentFeet, new))
                            }
                            it.value = inchesToFeetAndInches(height).second
                        }
                    )
                } else {
                    if(height !in 100..200) {
                        setHeight(100)
                    }
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = false
                            view.minValue = 100
                            view.maxValue = 200
                            view.setFormatter { num ->
                                "$num cm"
                            }
                            //Doesn't read height, can be left in 'factory'
                            view.setOnValueChangedListener { picker, old, new ->
                                setHeight(new)
                            }
                            view
                        },
                        update = {
                            it.value = height
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BirthDateScreen(
    day: Int, setDay: (Int) -> Unit,
    month: Int, setMonth: (Int) -> Unit,
    year: Int, setYear: (Int) -> Unit
) {

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Choose Age",
            style = MaterialTheme.typography.h4
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "Birthdate", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))
            val displayMonths = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )

            Row(Modifier.align(Alignment.CenterHorizontally)) {
                AndroidView(
                    factory = { context ->
                        val view = NumberPicker(context)
                        view.wrapSelectorWheel = true
                        view.minValue = 1
                        view.value = 1
                        view.setOnValueChangedListener { picker, old, new ->
                            setDay(new)
                        }
                        view
                    },
                    update = {
                        val calendar = GregorianCalendar()
                        calendar.clear()
                        calendar.set(year, month, 1)
                        val maxDaysForMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        it.maxValue = maxDaysForMonth
                        if(day !in 1..maxDaysForMonth) {
                            setDay(1)
                        } else {
                            it.value = day
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AndroidView(
                    factory = { context ->
                        val view = NumberPicker(context)
                        view.wrapSelectorWheel = true
                        view.minValue = 0
                        view.maxValue = 11
                        view.displayedValues = displayMonths
                        view.setOnValueChangedListener { picker, old, new ->
                            setMonth(new)
                        }
                        view
                    },
                    update = {
                        if(month !in 0..11) {
                            setMonth(0)
                        } else {
                            it.value = month
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AndroidView(
                    factory = { context ->
                        val view = NumberPicker(context)
                        view.wrapSelectorWheel = false
                        view.minValue = 1984
                        view.maxValue = GregorianCalendar().get(Calendar.YEAR)
                        view.setOnValueChangedListener { picker, old, new ->
                            setYear(new)
                        }
                        view
                    },
                    update = {
                        val endYear = GregorianCalendar().get(Calendar.YEAR)
                        if(year !in 1984..endYear) {
                            setYear(1984)
                        } else {
                            it.value = year
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeightScreen(
    weight: Float, setWeight: (Float) -> Unit,
    unit: String, setUnit: (String) -> Unit
) {

    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }
    val units = arrayOf(
        Constants.UNIT_WEIGHT_POUNDS to "Imperial (lb)",
        Constants.UNIT_WEIGHT_KG to "Metric (kg)"
    )

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Choose Weight",
            style = MaterialTheme.typography.h4
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "Weight", style = MaterialTheme.typography.h5)
            val currentUnitText = if (unit == Constants.UNIT_WEIGHT_POUNDS) {
                "Imperial (lb)"
            } else if (unit == Constants.UNIT_WEIGHT_KG) {
                "Metric (kg)"
            } else {
                "Metric (kg)"
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { it -> setExpanded(!expanded) }) {
                Row(Modifier.clickable { }) {
                    Text(text = currentUnitText)
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "drop down"
                    )
                }
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { }) {
                    units.forEach { (u, text) ->
                        DropdownMenuItem(onClick = {
                            setUnit(u)
                            setExpanded(false)
                        }) {
                            Text(text = text)
                        }
                    }

                }
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                if (unit == Constants.UNIT_WEIGHT_POUNDS) {
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = false
                            view.minValue = 110
                            view.maxValue = 330
                            view
                        },
                        update = {
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentFractionalPart = (weight) - floor(weight)
                                setWeight(new + currentFractionalPart)
                            }
                            if(weight !in 110F..330F) {
                                setWeight(110F)
                            } else {
                                it.value = weight.toInt()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = true
                            view.minValue = 0
                            view.maxValue = 9
                            view
                        },
                        update = {
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentWholePart = weight.toInt()
                                setWeight(currentWholePart + (new * 0.1F))
                            }
                            it.value = getTensPart(weight)
                        }
                    )
                } else {
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = false
                            view.minValue = 50
                            view.maxValue = 150
                            view
                        },
                        update = {
                            //Once again forgot about the 'factory' vs 'update' issue
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentFractionalPart = (weight) - floor(weight)
                                setWeight(new + currentFractionalPart)
                            }
                            if(weight !in 50F..150F) {
                                setWeight(50F)
                            } else {
                                it.value = weight.toInt()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AndroidView(
                        factory = { context ->
                            val view = NumberPicker(context)
                            view.wrapSelectorWheel = true
                            view.minValue = 0
                            view.maxValue = 9
                            view
                        },
                        update = {
                            it.setOnValueChangedListener { picker, old, new ->
                                val currentWholePart = weight.toInt()
                                setWeight(currentWholePart + (new * 0.1F))
                            }
                            it.value = getTensPart(weight)
                        }
                    )
                }
            }
        }
    }
}