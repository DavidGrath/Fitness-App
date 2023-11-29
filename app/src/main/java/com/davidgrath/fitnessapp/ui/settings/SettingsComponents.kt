package com.davidgrath.fitnessapp.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.BasicNavScreen
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles
import com.davidgrath.fitnessapp.util.centimetersToInches
import com.davidgrath.fitnessapp.util.inchesToCentimeters
import com.davidgrath.fitnessapp.util.kilogramsToPounds
import com.davidgrath.fitnessapp.util.poundsToKilograms
import kotlinx.coroutines.launch
import java.util.Locale


fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
//    navigation(BasicNavScreen.SettingsNav.allButLastSegment(),
//        BasicNavScreen.SettingsNav.lastSegment()
    navigation("home",
        "s"
    ) {
        composable(route = BasicNavScreen.SettingsNav.path) {
            SettingsScreen(
                {
                    navController.navigate(BasicNavScreen.PrivacyPolicyNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.TermsAndConditionsNav.path)
                },
                {
                    navController.navigate(BasicNavScreen.SettingsUnitsNav.path)
                },
                {
                    navController.popBackStack()
                }
            )
        }
        composable(route = BasicNavScreen.PrivacyPolicyNav.path) {
            PrivacyPolicyScreen( {
                navController.popBackStack()
            })
        }
        composable(route = BasicNavScreen.TermsAndConditionsNav.path) {
            TermsAndConditionsScreen({
                navController.popBackStack()
            })
        }
        composable(route = BasicNavScreen.SettingsUnitsNav.path) {
            SettingsUnitsScreen({
                navController.popBackStack()
            })
        }
    }
}
@Composable
fun SettingsScreen(
    onNavigatePrivacyPolicy: () -> Unit,
    onNavigateTermsAndConditions: () -> Unit,
    onNavigateUnits: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    Column(modifier = Modifier.fillMaxSize()) {

        val scrollState = rememberScrollState()
        val (syncToGoogleFitChecked, setSyncToGoogleFitChecked) = remember {
            mutableStateOf(false)
        }
        val (remindWorkout, setRemindWorkout) = remember {
            mutableStateOf(false)
        }

        SimpleAppBar(stringResource(R.string.settings_title), false, onBackClicked = onNavigateBack)
        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()) {
            Spacer(Modifier.height(16.dp))
            Column {
                Text(
                    stringResource(R.string.settings_label_general_settings),
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                SimpleSettingsItem(R.drawable.sync, stringResource(R.string.settings_item_sync_google_fit), {},
                    checkable = true, syncToGoogleFitChecked, setSyncToGoogleFitChecked)
                SimpleSettingsItem(R.drawable.bell_outline, stringResource(R.string.settings_item_remind_to_workout), {},
                    checkable = true, remindWorkout, setRemindWorkout)
                SimpleSettingsItem(iconResId = R.drawable.ruler, text = stringResource(R.string.settings_item_units),
                    onClick = {
                        onNavigateUnits()
                }, checkable = false)
                val (languageIndex, setLanguageIndex) = rememberSaveable {
                    mutableStateOf(-1)
                }
                val (isLanguageDialogShowing, setIsLanguageDialogShowing) = remember {
                    mutableStateOf(false)
                }
                SimpleSettingsItem(iconResId = R.drawable.translate, text = stringResource(R.string.settings_item_languages),
                    onClick = {
                        setIsLanguageDialogShowing(true)
                    },
                    checkable = false)
                if(isLanguageDialogShowing) {
                    GenericOptionsDialog(
                        optionsList = Constants.supportedLanguages,
                        selectedOptionsIndex = languageIndex,
                        onOptionPicked = { i ->
                            setLanguageIndex(i)
                            setIsLanguageDialogShowing(false)
                        },
                        {
                            setIsLanguageDialogShowing(false)
                        }
                    )
                }
                SimpleSettingsItem(iconResId = R.drawable.restart, text = stringResource(R.string.settings_item_restart_progress), onClick = {}, checkable = false)
            }

            Spacer(Modifier.height(16.dp))
            Divider(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            Column {
                Text(
                    stringResource(R.string.settings_label_more_settings),
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                SimpleSettingsItem(iconResId = R.drawable.face_agent, text = stringResource(R.string.settings_item_support_and_feedback), onClick = {}, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.pen, text = stringResource(R.string.settings_item_terms_and_conditions), onClick = onNavigateTermsAndConditions, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.shield, text = stringResource(R.string.settings_item_privacy_policy), onClick = onNavigatePrivacyPolicy, checkable = false)
            }
            Spacer(Modifier.height(16.dp))
            Divider(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            SimpleSettingsItem(iconResId = R.drawable.logout, text = stringResource(R.string.settings_item_log_out),
                onClick = {
                          preferences.edit()
                              .putString(PreferencesTitles.FIRST_NAME, null)
                              .putString(PreferencesTitles.LAST_NAME, null)
                              .putString(PreferencesTitles.EMAIL, null)
                              .putInt(PreferencesTitles.HEIGHT, 0)
                              .putString(PreferencesTitles.HEIGHT_UNIT, null)
                              .putFloat(PreferencesTitles.WEIGHT, 0f)
                              .putString(PreferencesTitles.WEIGHT_UNIT, null)
                              .putInt(PreferencesTitles.BIRTH_DATE_DAY, 0)
                              .putInt(PreferencesTitles.BIRTH_DATE_MONTH, 0)
                              .putInt(PreferencesTitles.BIRTH_DATE_YEAR, 0)
                              .apply()
                },
                checkable = false)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        SimpleAppBar(stringResource(R.string.privacy_policy_header), expanded = false, onBackClicked = onNavigateBack)
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Privacy Policy", style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(8.dp))
            val privacyPolicyPart1 = "Protecting your privacy policy is very important to us. We hope the following statement will help you understand how our app deals with personal identifiable information ('PII') you may occasionally provide to us via internet (the Google Play Platform).\n" +
                    "\n" +
                    "Generally, we do not collect any PII from you when you download our Android applications. To be specific, we do not require the consumers to get registered before downloading the application, nor do we keep track of the consumer's visit of our application, we even don't have a Server to store such PII."
//            Text(stringResource(R.string.privacy_policy_part_1), style = MaterialTheme.typography.body1)
            Text(privacyPolicyPart1, style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(16.dp))
            Text("Information we collect", style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(8.dp))
            val privacyPolicyPart2 = "Cookies (WebView browser cookies) - When you visit Forum service, we may send one or more cookies? files with small amount of data that is commonly used an anonymous unique identifier? to your mobile device that uniquely identifies your device and allows the application to help you log in faster and enhance your navigation through the Forum. You can reset your web browser to refuse all cookies or to indicate when a cookie is being sent. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                    "\n" +
                    "Analytics - We use third party analytics tool, Google Analytics, to help us measure traffic and usage trends for the service. Google Analytics collects information such as how often users visit our services, what pages they visit, when they do so, and what other pages they use prior to coming to our services. We use the information that we get from Google Analytics only to improve our services.\n" +
                    "\n" +
                    "Health Kit - You will also have an option to permit us to import health data into the App from third-party services such as Google Fit. Permitting us to access third party information can help you to maximize your App experience, and we will handle any such third-party information in accordance with this Privacy Policy."
//            Text(stringResource(R.string.privacy_policy_part_2), style = MaterialTheme.typography.body1)
            Text(privacyPolicyPart2, style = MaterialTheme.typography.body1)
        }
    }
}



@Composable
fun TermsAndConditionsScreen(
    onNavigateBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        SimpleAppBar(stringResource(R.string.terms_and_conditions_header), expanded = false, onBackClicked = onNavigateBack)
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Terms & Conditions", style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(8.dp))
            val termsAndConditions = "These Terms & Conditions applies to use of the FitnessZone app. In order to be able to use the complete spectrum of FitnessZone, a one time registration is required. However, certain content (e.g. information regarding your age, weight, and height).\n" +
                    "\n" +
                    "• FitnessZone's goal is to make sports enjoyable and to make a positive contribution to people's health.\n" +
                    "\n" +
                    "• Your health is very important to us. Always consult your doctor about your athletic behavior. FitnessZone neither substitutes your doctor, is responsible for your behavior. The content of the FitnessZone product whether it is provided by FitnessZone, its partners or users, are not meant to supplement, let alone replace, the information provided by doctors or pharmacies. By accepting these Terms & Conditions, you confirm that you are solely responsible for your own health."
//            Text(stringResource(R.string.terms_and_conditions), style = MaterialTheme.typography.body1)
            Text(termsAndConditions, style = MaterialTheme.typography.body1)
        }
    }
}

@Composable
fun SettingsUnitsScreen(
    onNavigateBack: () -> Unit
) {

    val distanceUnits = arrayOf(Constants.UNIT_DISTANCE_KILOMETERS, Constants.UNIT_DISTANCE_MILES)
    val distanceUnitsDisplay = arrayOf("Metric (km)", "Imperial (mi)")
    val temperatureUnits = arrayOf(Constants.UNIT_TEMPERATURE_CELSIUS, Constants.UNIT_TEMPERATURE_FAHRENHEIT)
    val temperatureUnitsDisplay = arrayOf("Celsius", "Fahrenheit")
    val weightUnits = arrayOf(Constants.UNIT_WEIGHT_KG, Constants.UNIT_WEIGHT_POUNDS)
    val weightUnitsDisplay = arrayOf("Kilograms", "Pounds")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferences = remember {
        context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    val (currentDistanceUnit, setCurrentDistanceUnit)  = remember {
        mutableStateOf(preferences.getString(PreferencesTitles.DISTANCE_UNIT, Constants.UNIT_DISTANCE_KILOMETERS)!!)
    }
    val (currentTemperatureUnit, setCurrentTemperatureUnit)  = remember {
        mutableStateOf(preferences.getString(PreferencesTitles.TEMPERATURE_UNIT, Constants.UNIT_TEMPERATURE_CELSIUS)!!)
    }
    val (currentWeightUnit, setCurrentWeightUnit)  = remember {
        mutableStateOf(preferences.getString(PreferencesTitles.WEIGHT_UNIT, Constants.UNIT_WEIGHT_KG)!!)
    }

    Column(Modifier.fillMaxSize()) {
        SimpleAppBar(title = stringResource(R.string.units_header), expanded = false, centeredWhileCollapsed = true, onBackClicked = onNavigateBack)

        val (currentDialogUnitType, setCurrentUnitDialogType) = remember {
            mutableStateOf("")
        }
        val (isDialogShowing, setIsDialogShowing)  = remember {
            mutableStateOf(false)
        }

        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        setCurrentUnitDialogType("distance")
                        setIsDialogShowing(true)
                    }) {
                Text(stringResource(R.string.units_label_distance), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                val t = getDisplayTextForUnit("distance", preferences)
                Text(t, style = MaterialTheme.typography.body2)
            }
            Spacer(Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        setCurrentUnitDialogType("temperature")
                        setIsDialogShowing(true)
                    }) {
                Text(stringResource(R.string.units_label_temperature), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                val t = getDisplayTextForUnit("temperature", preferences)
                Text(t, style = MaterialTheme.typography.body2)
            }
            Spacer(Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        setCurrentUnitDialogType("weight")
                        setIsDialogShowing(true)
                    }) {
                Text(stringResource(R.string.units_label_weight), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                val t = getDisplayTextForUnit("weight", preferences)
                Text(t, style = MaterialTheme.typography.body2)
            }
            if(isDialogShowing) {
                val options: Array<String>
                val currentOptionsIndex: Int
                when(currentDialogUnitType) {
                    "distance" -> {
                        options = distanceUnitsDisplay
                        currentOptionsIndex = distanceUnits.indexOf(currentDistanceUnit)
                    }
                    "temperature" -> {
                        options = temperatureUnitsDisplay
                        currentOptionsIndex = temperatureUnits.indexOf(currentTemperatureUnit)
                    }
                    "weight" -> {
                        options = weightUnitsDisplay
                        currentOptionsIndex = weightUnits.indexOf(currentWeightUnit)
                    }
                    else -> {
                        options = distanceUnits
                        currentOptionsIndex = distanceUnits.indexOf(currentDistanceUnit)
                    }
                }

                GenericOptionsDialog(optionsList = options,
                    selectedOptionsIndex = currentOptionsIndex,
                    onOptionPicked = {
                        coroutineScope.launch {
                            when (currentDialogUnitType) {
                                "distance" -> {
                                    setCurrentDistanceUnit(distanceUnits[it])
                                    preferences.edit().putString(PreferencesTitles.DISTANCE_UNIT, distanceUnits[it])
                                        .apply()
                                }
                                "temperature" -> {
                                    setCurrentTemperatureUnit(temperatureUnits[it])
                                    preferences.edit().putString(PreferencesTitles.TEMPERATURE_UNIT, temperatureUnits[it])
                                        .apply()
                                }
                                "weight" -> {
                                    val unit = weightUnits[it]
                                    val editor = preferences.edit()
                                    val w = preferences.getFloat(PreferencesTitles.WEIGHT, 0f)
                                    if(currentWeightUnit == Constants.UNIT_WEIGHT_POUNDS && unit == Constants.UNIT_WEIGHT_KG) {
                                        editor.putFloat(PreferencesTitles.WEIGHT, poundsToKilograms(w))
                                    } else if(currentWeightUnit == Constants.UNIT_WEIGHT_KG && unit == Constants.UNIT_WEIGHT_POUNDS) {
                                        editor.putFloat(PreferencesTitles.WEIGHT, kilogramsToPounds(w))
                                    }
                                    setCurrentWeightUnit(weightUnits[it])
                                    editor
                                        .putString(PreferencesTitles.WEIGHT_UNIT, unit)
                                        .apply()
                                }
                            }
                            setIsDialogShowing(false)
                        }
                    },
                    onDismiss = {
                        setIsDialogShowing(false)
                    }
                )
            }
        }
    }
}

//TODO Maybe there's some ISO/Internationalization standard to make this neater
fun getDisplayTextForUnit(unitType: String, preferences: SharedPreferences): String {
    return when(unitType) {
        "distance" -> {
            when(preferences.getString(PreferencesTitles.DISTANCE_UNIT, Constants.UNIT_DISTANCE_KILOMETERS)) {
                Constants.UNIT_DISTANCE_KILOMETERS -> "Metric (km)"
                Constants.UNIT_DISTANCE_MILES -> "Imperial (mi)"
                else -> "Metric (km)"
            }
        }
        "temperature" -> {
            when(preferences.getString(PreferencesTitles.TEMPERATURE_UNIT, Constants.UNIT_TEMPERATURE_CELSIUS)) {
                Constants.UNIT_TEMPERATURE_CELSIUS -> "Celsius"
                Constants.UNIT_TEMPERATURE_FAHRENHEIT -> "Fahrenheit"
                else -> "Celsius"
            }
        }
        "weight" -> {
            when(preferences.getString(PreferencesTitles.WEIGHT_UNIT, Constants.UNIT_WEIGHT_KG)) {
                Constants.UNIT_WEIGHT_KG -> "Kilograms"
                Constants.UNIT_WEIGHT_POUNDS -> "Pounds"
                else -> "Kilograms"
            }
        }
        else -> {
            "unknown"
        }
    }
}


@Composable
fun GenericOptionsDialog(
    optionsList: Array<String>,
    selectedOptionsIndex: Int,
    onOptionPicked: (optionIndex: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val (selectedItemIndex, setSelectedItemIndex) = remember {
        mutableStateOf(selectedOptionsIndex)
    }
    val scrollState = rememberScrollState()
    AlertDialog(onDismissRequest = onDismiss,
        buttons = {},
        text = {
            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .selectableGroup()
            ) {
                optionsList.forEachIndexed { i, lang ->
//                    val locale = Locale(lang)
//                    val displayLanguage = locale.getDisplayLanguage(locale)
                    Row(
                        Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .clickable {
                                if (selectedItemIndex == i) {
                                    onOptionPicked(i)
                                } else {
                                    setSelectedItemIndex(i)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedItemIndex == i,
                            onClick = null
                        )
                        Spacer(Modifier.width(8.dp))
//                        val textDirectionInt = TextUtils.getLayoutDirectionFromLocale(locale)
//                        val textDirection = if(textDirectionInt == View.LAYOUT_DIRECTION_RTL) {
//                            TextDirection.Rtl
//                        } else {
//                            TextDirection.Ltr
//                        }
//                        Text(displayLanguage, Modifier.fillMaxWidth(), style = MaterialTheme.typography.body1.copy(textDirection = textDirection))
                        Text(lang, Modifier.fillMaxWidth(), style = MaterialTheme.typography.body1)
                    }

                }
            }
        }
    )
}

@Composable
fun SimpleSettingsItem(
    iconResId: Int,
    text: String,
    onClick: () -> Unit,
    checkable: Boolean,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit) = {  }
) {
    Row(
        Modifier
            .clickable { onClick() }
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = "",
            Modifier.size(24.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        if(checkable) {
            Switch(checked = checked, onCheckedChange = onCheckedChange,
                Modifier.padding(horizontal = 16.dp),
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary, uncheckedThumbColor = Color.White)
            )
        }
    }
}