package com.davidgrath.fitnessapp.ui.profile

import android.content.Context
import android.widget.NumberPicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.UnderlineTextField
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.feetAndInchesToInches
import com.davidgrath.fitnessapp.util.getTensPart
import com.davidgrath.fitnessapp.util.inchesToFeetAndInches
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.floor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()) {

        val scrollState = rememberScrollState()
        val context = LocalContext.current
        val preferences = context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val (firstName, setFirstName) = remember {
            mutableStateOf(preferences.getString(Constants.PreferencesTitles.FIRST_NAME, "")!!)
        }
        val (lastName, setLastName) = remember {
            mutableStateOf(preferences.getString(Constants.PreferencesTitles.LAST_NAME, "")!!)
        }
        val (email, setEmail) = remember {
            mutableStateOf(preferences.getString(Constants.PreferencesTitles.EMAIL, "")!!)
        }
        val (gender, setGender) = remember {
            mutableStateOf(preferences.getString(Constants.PreferencesTitles.GENDER, "male")!!)
        }

        SimpleAppBar("User Profile", false, onNavigateBack)
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Text("General Settings", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))

            Image(painter = painterResource(R.drawable.account_circle), contentDescription = "avatar",
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(96.dp)
            )

            Spacer(Modifier.height(16.dp))
            Text("First Name", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = firstName, onValueChange = setFirstName, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Text("Last Name", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = lastName, onValueChange = setLastName, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Text("Last Name", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = email, onValueChange = setEmail, modifier = Modifier.fillMaxWidth())

            val defaultInteractionSource = remember {
                MutableInteractionSource()
            }
            val indicatorColor = TextFieldDefaults.textFieldColors().indicatorColor(
                enabled = true,
                isError = false,
                interactionSource = defaultInteractionSource
            ).value

            val sdf = SimpleDateFormat.getDateInstance()
            val (isBirthdateDialogShowing, setIsBirthdateDialogShowing) = remember {
                mutableStateOf(false)
            }

            val (calendar, setCalendar) = remember {
                mutableStateOf(Calendar.getInstance().also {
                    it.set(
                        preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_YEAR, 1984),
                        preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_MONTH, 0),
                        preferences.getInt(Constants.PreferencesTitles.BIRTH_DATE_DAY, 1)
                    )
                })
            }
            Spacer(Modifier.height(16.dp))
            Text("Birthdate", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            Text(sdf.format(calendar.time), style = MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setIsBirthdateDialogShowing(true)
                    }
            )
            Box(
                Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(indicatorColor))
            if(isBirthdateDialogShowing) {
                BirthdateDialog(initialBirthdate = calendar,
                    onSaveBirthdate = { c ->
                        setCalendar(c)
                    },
                    onDismiss = {
                        setIsBirthdateDialogShowing(false)
                    })
            }

            val (height, setHeight) = remember {
                mutableStateOf(preferences.getInt(Constants.PreferencesTitles.HEIGHT, 0))
            }
            val (heightUnit, setHeightUnit) = remember {
                mutableStateOf(preferences.getString(Constants.PreferencesTitles.HEIGHT_UNIT, Constants.UNIT_HEIGHT_CENTIMETERS)!!)
            }
            val (isHeightDialogShowing, setIsHeightDialogShowing) = remember {
                mutableStateOf(false)
            }
            val heightString = if(heightUnit == Constants.UNIT_HEIGHT_CENTIMETERS) {
                "$height cm"
            } else {
                val feetAndInches = inchesToFeetAndInches(height)
                "${feetAndInches.first}'${feetAndInches.second}\""
            }
            Spacer(Modifier.height(16.dp))
            Text("Height", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            Text(heightString, style = MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setIsHeightDialogShowing(true)
                    }
            )
            Box(
                Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(indicatorColor))
            if(isHeightDialogShowing) {
                HeightDialog(
                    initialHeight = height,
                    initialHeightUnit = heightUnit,
                    onSaveHeight = { h, u ->
                        setHeight(h)
                        setHeightUnit(u)
                    },
                    onDismiss = {
                        setIsHeightDialogShowing(false)
                    })
            }

            val (weight, setWeight) = remember {
                mutableStateOf(preferences.getFloat(Constants.PreferencesTitles.WEIGHT, 0f))
            }
            val (weightUnit, setWeightUnit) = remember {
                mutableStateOf(preferences.getString(Constants.PreferencesTitles.WEIGHT_UNIT, Constants.UNIT_WEIGHT_KG)!!)
            }
            val (isWeightDialogShowing, setIsWeightDialogShowing) = remember {
                mutableStateOf(false)
            }
            val weightString = if(weightUnit == Constants.UNIT_WEIGHT_KG) {
                "$weight kg"
            } else {
                "$weight pounds"
            }
            Spacer(Modifier.height(16.dp))
            Text("Weight", style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            Text(weightString, style = MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setIsWeightDialogShowing(true)
                    }
            )
            Box(
                Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(indicatorColor))
            if(isWeightDialogShowing) {
                WeightDialog(initialWeight = weight, initialWeightUnit = weightUnit,
                    onSaveWeight = { w, u ->
                        setWeight(w)
                        setWeightUnit(u)
                    },
                    onDismiss = {
                        setIsWeightDialogShowing(false)
                    })
            }

            val primaryColor = MaterialTheme.colors.primary
            val otherColor = Color.Black
            Spacer(Modifier.height(16.dp))
            Row {
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
                        .padding(8.dp)
                        .size(24.dp),
                    tint = maleIconColor
                )
                Spacer(Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.face_woman),
                    contentDescription = "female",
                    Modifier
                        .clickable {
                            setGender("female")
                        }
                        .padding(8.dp)
                        .size(24.dp),
                    tint = femaleIconColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeightDialog(
    initialWeight: Float,
    initialWeightUnit: String,
    onSaveWeight: (weight: Float, weightUnit: String) -> Unit,
    onDismiss: () -> Unit
) {
    val units = arrayOf(
        Constants.UNIT_WEIGHT_POUNDS to "Imperial (lb)",
        Constants.UNIT_WEIGHT_KG to "Metric (kg)"
    )
    val (dialogWeight, setDialogWeight) = remember {
        mutableStateOf(initialWeight)
    }
    val (dialogWeightUnit, setDialogWeightUnit) = remember {
        mutableStateOf(initialWeightUnit)
    }
    val (dialogDropDownExpanded, setDialogDropDownExpanded) = remember {
        mutableStateOf(false)
    }
    AlertDialog(onDismissRequest = {},
        title = {
            Text(text = "Weight", style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text("Save",
                Modifier.padding(8.dp)
                    .clickable {
                    onSaveWeight(dialogWeight, dialogWeightUnit)
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Text("Cancel",
                Modifier.padding(8.dp)
                    .clickable {
                    onDismiss()
                }
            )
        },
        text = {
            Column(
                Modifier
                    .fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                val currentUnitText = if (dialogWeightUnit == Constants.UNIT_WEIGHT_POUNDS) {
                    "Imperial (lb)"
                } else if (dialogWeightUnit == Constants.UNIT_WEIGHT_KG) {
                    "Metric (kg)"
                } else {
                    "Metric (kg)"
                }
                ExposedDropdownMenuBox(
                    expanded = dialogDropDownExpanded,
                    onExpandedChange = { it -> setDialogDropDownExpanded(!dialogDropDownExpanded) }) {
                    Row(Modifier.clickable { }) {
                        Text(text = currentUnitText)
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                            contentDescription = "drop down"
                        )
                    }
                    ExposedDropdownMenu(expanded = dialogDropDownExpanded, onDismissRequest = { }) {
                        units.forEach { (u, text) ->
                            DropdownMenuItem(onClick = {
                                setDialogWeightUnit(u)
                                setDialogDropDownExpanded(false)
                            }) {
                                Text(text = text)
                            }
                        }

                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    if (dialogWeightUnit == Constants.UNIT_WEIGHT_POUNDS) {
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
                                    val currentFractionalPart = (dialogWeight) - floor(dialogWeight)
                                    setDialogWeight(new + currentFractionalPart)
                                }
                                if(dialogWeight !in 110F..330F) {
                                    setDialogWeight(110F)
                                } else {
                                    it.value = dialogWeight.toInt()
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
                                    val currentWholePart = dialogWeight.toInt()
                                    setDialogWeight(currentWholePart + (new * 0.1F))
                                }
                                it.value = getTensPart(dialogWeight)
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
                                    val currentFractionalPart = (dialogWeight) - floor(dialogWeight)
                                    setDialogWeight(new + currentFractionalPart)
                                }
                                if(dialogWeight !in 50F..150F) {
                                    setDialogWeight(50F)
                                } else {
                                    it.value = dialogWeight.toInt()
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
                                    val currentWholePart = dialogWeight.toInt()
                                    setDialogWeight(currentWholePart + (new * 0.1F))
                                }
                                it.value = getTensPart(dialogWeight)
                            }
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeightDialog(
    initialHeight: Int,
    initialHeightUnit: String,
    onSaveHeight: (height: Int, heightUnit: String) -> Unit,
    onDismiss: () -> Unit
) {
    val units = arrayOf(
        Constants.UNIT_HEIGHT_INCHES to "Imperial (ft)",
        Constants.UNIT_HEIGHT_CENTIMETERS to "Metric (cm)"
    )
    val (dialogHeight, setDialogHeight) = remember {
        mutableStateOf(initialHeight)
    }
    val (dialogHeightUnit, setDialogHeightUnit) = remember {
        mutableStateOf(initialHeightUnit)
    }
    val (dialogDropDownExpanded, setDialogDropDownExpanded) = remember {
        mutableStateOf(false)
    }
    AlertDialog(onDismissRequest = {},
        title = {
            Text(text = "Height", style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text("Save",
                Modifier.padding(8.dp)
                    .clickable {
                    onSaveHeight(dialogHeight, dialogHeightUnit)
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Text("Cancel",
                Modifier.padding(8.dp)
                    .clickable {
                    onDismiss()
                }
            )
        },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {

                val currentUnitText = if (dialogHeightUnit == Constants.UNIT_HEIGHT_INCHES) {
                    "Imperial (ft)"
                } else if (dialogHeightUnit == Constants.UNIT_HEIGHT_CENTIMETERS) {
                    "Metric (cm)"
                } else {
                    "Metric (cm)"
                }
                ExposedDropdownMenuBox(
                    expanded = dialogDropDownExpanded,
                    onExpandedChange = { it -> setDialogDropDownExpanded(!dialogDropDownExpanded) },
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
                    ExposedDropdownMenu(expanded = dialogDropDownExpanded, onDismissRequest = { }) {
                        units.forEach { (u, text) ->
                            DropdownMenuItem(onClick = {
                                setDialogHeightUnit(u)
                                setDialogDropDownExpanded(false)
                            }) {
                                Text(text = text)
                            }
                        }

                    }
                }
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    if (dialogHeightUnit == Constants.UNIT_HEIGHT_INCHES) {
                        //TODO Maybe add the min and max as util constants
                        var initialFeetAndInches = inchesToFeetAndInches(dialogHeight)
                        if(initialFeetAndInches.first !in (1..6)) {
                            initialFeetAndInches = 1 to initialFeetAndInches.second
                        }
                        if(initialFeetAndInches.second !in (0..11)) {
                            initialFeetAndInches = initialFeetAndInches.first to 0
                        }
                        val adjustedHeight = feetAndInchesToInches(initialFeetAndInches.first, initialFeetAndInches.second)
                        setDialogHeight(adjustedHeight)
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
                                    val currentInches = inchesToFeetAndInches(dialogHeight).second
                                    setDialogHeight(feetAndInchesToInches(new, currentInches))
                                }
                                it.value = inchesToFeetAndInches(dialogHeight).first
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
                                    val currentFeet = inchesToFeetAndInches(dialogHeight).first
                                    setDialogHeight(feetAndInchesToInches(currentFeet, new))
                                }
                                it.value = inchesToFeetAndInches(dialogHeight).second
                            }
                        )
                    } else {
                        if(dialogHeight !in 100..200) {
                            setDialogHeight(100)
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
                                    setDialogHeight(new)
                                }
                                view
                            },
                            update = {
                                it.value = dialogHeight
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun BirthdateDialog(
    initialBirthdate: Calendar,
    onSaveBirthdate: (birthdate: Calendar) -> Unit,
    onDismiss: () -> Unit
) {
    val (dialogDay, setDialogDay) = remember {
        mutableStateOf(initialBirthdate.get(Calendar.DAY_OF_MONTH))
    }
    val (dialogMonth, setDialogMonth) = remember {
        mutableStateOf(initialBirthdate.get(Calendar.MONTH))
    }
    val (dialogYear, setDialogYear) = remember {
        mutableStateOf(initialBirthdate.get(Calendar.YEAR))
    }
    AlertDialog(onDismissRequest = {},
        title = {
            Text(text = "Birthdate", style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text("Set",
                Modifier.padding(8.dp)
                    .clickable {
                    onSaveBirthdate(Calendar.getInstance().also {
                        it.clear()
                        it.set(dialogYear, dialogMonth, dialogDay)
                    })
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Text("Cancel",
                Modifier.padding(8.dp)
                    .clickable {
                    onDismiss()
                }
            )
        },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
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
                                setDialogDay(new)
                            }
                            view
                        },
                        update = {
                            val cal = GregorianCalendar()
                            cal.clear()
                            cal.set(dialogYear, dialogMonth, 1)
                            val maxDaysForMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                            it.maxValue = maxDaysForMonth
                            if(dialogDay !in 1..maxDaysForMonth) {
                                setDialogDay(1)
                            } else {
                                it.value = dialogDay
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
                                setDialogMonth(new)
                            }
                            view
                        },
                        update = {
                            if(dialogMonth !in 0..11) {
                                setDialogMonth(0)
                            } else {
                                it.value = dialogMonth
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
                                setDialogYear(new)
                            }
                            view
                        },
                        update = {
                            val endYear = GregorianCalendar().get(Calendar.YEAR)
                            if(dialogYear !in 1984..endYear) {
                                setDialogYear(1984)
                            } else {
                                it.value = dialogYear
                            }
                        }
                    )
                }
            }
        }
    )
}