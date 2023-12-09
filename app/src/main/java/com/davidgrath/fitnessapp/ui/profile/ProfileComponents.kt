package com.davidgrath.fitnessapp.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.NumberPicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar
import com.davidgrath.fitnessapp.ui.components.UnderlineTextField
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingScreenState
import com.davidgrath.fitnessapp.ui.onboarding.OnboardingViewModel
import com.davidgrath.fitnessapp.util.Constants
import com.davidgrath.fitnessapp.util.Constants.PreferencesTitles
import com.davidgrath.fitnessapp.util.Constants.avatarMap
import com.davidgrath.fitnessapp.util.SimpleResult
import com.davidgrath.fitnessapp.util.centimetersToInches
import com.davidgrath.fitnessapp.util.feetAndInchesToInches
import com.davidgrath.fitnessapp.util.getTensPart
import com.davidgrath.fitnessapp.util.inchesToCentimeters
import com.davidgrath.fitnessapp.util.inchesToFeetAndInches
import com.davidgrath.fitnessapp.util.kilogramsToPounds
import com.davidgrath.fitnessapp.util.poundsToKilograms
import com.davidgrath.fitnessapp.util.tempGetUri
import kotlinx.coroutines.launch
import java.io.File
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.floor

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {

    val screenState = viewModel.profileScreenStateLiveData.observeAsState().value?: ProfileScreenState()
    val context = LocalContext.current
    //TODO Maybe abstract this use case of profile picture and preferences through a Repository
    val preferences = remember {
        context.getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    Column(modifier = Modifier
        .fillMaxSize()) {

        val scrollState = rememberScrollState()
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()

        SimpleAppBar(stringResource(R.string.profile_header), false, onBackClicked = onNavigateBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_general_settings), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))
            val (isAvatarDialogShowing, setIsAvatarDialogShowing) = remember {
                mutableStateOf(false)
            }
            val (isDefaultAvatarDialogShowing, setIsDefaultAvatarDialogShowing) = remember {
                mutableStateOf(false)
            }
            val imageModifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .clip(CircleShape)
                .clickable {
                    setIsAvatarDialogShowing(true)
                }

            val avatar = screenState.userAvatar
            val avatarType = screenState.userAvatarType
            val avatarFileExists = screenState.userAvatarFileExists
            val uuid = preferences.getString(PreferencesTitles.CURRENT_USER_UUID, null)!!
            val avatarFile = remember {
                val avatarFolder = File(context.filesDir, "avatars")
                if(!avatarFolder.exists()) {
                    avatarFolder.mkdir()
                }
                File(avatarFolder, "$uuid.jpg")
            }
            when(avatarType) {
                "default" -> {
                    val resId = avatarMap[avatar]?:R.drawable.avatar_02
                    Image(
                        painter = painterResource(resId),
                        contentDescription = "avatar",
                        Modifier
                            .border(2.dp, Color.Black, CircleShape)
                            .then(imageModifier),
                        contentScale = ContentScale.FillHeight
                    )
                }
                "media" -> {
                    if (avatarFileExists) {
                        GlideImage(
                            model = avatarFile,
                            contentDescription = "avatar",
                            imageModifier
                        ) {
                            it.circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                        }
                    } else {
                        Image(
                            painter = painterResource(R.drawable.account_circle),
                            contentDescription = "avatar",
                            imageModifier
                        )
                    }
                }
                else -> {
                    Image(
                        painter = painterResource(R.drawable.account_circle),
                        contentDescription = "avatar",
                        imageModifier
                    )
                }
            }
            val (uri, setUri) = rememberSaveable {
                mutableStateOf<String?>(tempGetUri(context, preferences).toString())
            }
            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
                coroutineScope.launch {
                    viewModel.setAvatarFile(context.contentResolver.openInputStream(Uri.parse(uri!!))!!, avatarFile, preferences).observe(lifecycleOwner) {
                        if(it is SimpleResult.Success) {
                            context.contentResolver.delete(Uri.parse(uri), null, null)
                            setUri(null)
                        }
                    }
                }
            }
            if(isAvatarDialogShowing) {
                AvatarDialog(
                    onChooseAvatarAction = {
                        when(it) {
                            AVATAR_DIALOG_ACTION_MEDIA -> {
                                coroutineScope.launch {
                                    viewModel.submitAvatarType("media").observe(lifecycleOwner) {
                                        if(it is SimpleResult.Success) {
//                                            val b = if (avatarFile.exists()) {
//                                                BitmapFactory.decodeStream(avatarFile.inputStream())
//                                            } else {
//                                                null
//                                            }
//                                            if (b == null) {
                                            if (!avatarFile.exists()) {
                                                if(uri == null) {
                                                    val u = tempGetUri(context, preferences)
                                                    setUri(u.toString())
                                                    launcher.launch(u)
                                                } else {
                                                    launcher.launch(Uri.parse(uri))
                                                }
                                            } /*else {
                                                setBitmap(b)
                                            }*/
                                        }
                                    }
                                }

                            }
                            AVATAR_DIALOG_ACTION_DEFAULT_AVATAR -> {
                                setIsDefaultAvatarDialogShowing(true)
                            }
                            AVATAR_DIALOG_ACTION_DELETE -> {
                                viewModel.submitAvatarType("none").observe(lifecycleOwner) {
                                }
                            }
                        }
                    },
                    onDismiss = {
                        setIsAvatarDialogShowing(false)
                    }
                )
            }
            if(isDefaultAvatarDialogShowing) {
                DefaultAvatarDialog(
                    onSelectDefaultAvatar = {
                        viewModel.submitAvatarType("default", it).observe(lifecycleOwner) {
                        }
                    },
                    initialDefaultAvatarId = if(avatar.isNotBlank()) avatar else "avatar_02",
                    onDismiss = {
                        setIsDefaultAvatarDialogShowing(false)
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_first_name), style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = screenState.firstName, onValueChange = viewModel::setFirstName,
                modifier = Modifier.fillMaxWidth(), textStyle = MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary)) {
                coroutineScope.launch {
                    viewModel.submitNameAndEmail().observe(lifecycleOwner) {

                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_last_name), style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = screenState.lastName, onValueChange = viewModel::setLastName,
                modifier = Modifier.fillMaxWidth(), textStyle = MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary)) {
                coroutineScope.launch {
                    viewModel.submitNameAndEmail().observe(lifecycleOwner) {

                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_email), style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
            UnderlineTextField(value = screenState.email, onValueChange = viewModel::setEmail,
                modifier = Modifier.fillMaxWidth(), true, MaterialTheme.typography.body1.copy(MaterialTheme.colors.primary)) {
                coroutineScope.launch {
                    viewModel.submitNameAndEmail().observe(lifecycleOwner) {

                    }
                }
            }

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

            val calendar = Calendar.getInstance().also {
                it.set(
                    screenState.birthDateYear,
                    screenState.birthDateMonth,
                    screenState.birthDateDay
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_birthdate), style = MaterialTheme.typography.body1)
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
                        viewModel.setBirthDate(
                            c.get(Calendar.DAY_OF_MONTH),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.YEAR),
                        )
                        coroutineScope.launch {
                            viewModel.submitBirthDate().observe(lifecycleOwner) {}
                        }
                    },
                    onDismiss = {
                        setIsBirthdateDialogShowing(false)
                    })
            }

            val (isHeightDialogShowing, setIsHeightDialogShowing) = remember {
                mutableStateOf(false)
            }
            val heightString = if(screenState.heightUnit == Constants.UNIT_HEIGHT_CENTIMETERS) {
                "${screenState.height} cm"
            } else {
                val feetAndInches = inchesToFeetAndInches(screenState.height)
                "${feetAndInches.first}'${feetAndInches.second}\""
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_height), style = MaterialTheme.typography.body1)
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
                    initialHeight = screenState.height,
                    initialHeightUnit = screenState.heightUnit,
                    onSaveHeight = { h, u ->
                        viewModel.setHeightAndUnit(h, u)
                        coroutineScope.launch {
                            viewModel.submitHeight().observe(lifecycleOwner) {}
                        }
                    },
                    onDismiss = {
                        setIsHeightDialogShowing(false)
                    })
            }
            val (isWeightDialogShowing, setIsWeightDialogShowing) = remember {
                mutableStateOf(false)
            }
            val weightString = if(screenState.weightUnit == Constants.UNIT_WEIGHT_KG) {
                "${screenState.weight} kg"
            } else {
                "${screenState.weight} pounds"
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.profile_label_weight), style = MaterialTheme.typography.body1)
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
                WeightDialog(initialWeight = screenState.weight, initialWeightUnit = screenState.weightUnit,
                    onSaveWeight = { w, u ->
                        viewModel.setWeightAndUnit(w, u)
                        coroutineScope.launch {
                            viewModel.submitWeight().observe(lifecycleOwner) {}
                        }
                    },
                    onDismiss = {
                        setIsWeightDialogShowing(false)
                    })
            }

            val primaryColor = MaterialTheme.colors.primary
            val otherColor = Color.Black
            Spacer(Modifier.height(16.dp))
            Row {
                val maleIconColor = if (screenState.gender.equals("male", true)) {
                    primaryColor
                } else {
                    otherColor
                }
                val femaleIconColor = if (screenState.gender.equals("female", true)) {
                    primaryColor
                } else {
                    otherColor
                }
                Icon(
                    painter = painterResource(id = R.drawable.face),
                    contentDescription = "male",
                    Modifier
                        .clickable {
                            viewModel.setGender("male")
                            coroutineScope.launch {
                                viewModel
                                    .submitGender()
                                    .observe(lifecycleOwner) {}
                            }
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
                            viewModel.setGender("female")
                            coroutineScope.launch {
                                viewModel
                                    .submitGender()
                                    .observe(lifecycleOwner) {}
                            }
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
            Text(text = stringResource(R.string.dialog_weight_title), style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text(
                stringResource(R.string.dialog_weight_button_save),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onSaveWeight(dialogWeight, dialogWeightUnit)
                        onDismiss()
                    }
            )
        },
        dismissButton = {
            Text(stringResource(R.string.dialog_weight_button_cancel),
                Modifier
                    .padding(8.dp)
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
                                if(u != dialogWeightUnit) {
                                    var w = dialogWeight
                                    if(dialogWeightUnit == Constants.UNIT_WEIGHT_KG && u == Constants.UNIT_WEIGHT_POUNDS) {
                                        w = kilogramsToPounds(dialogWeight)
                                    } else if(dialogWeightUnit == Constants.UNIT_WEIGHT_POUNDS && u == Constants.UNIT_WEIGHT_KG) {
                                        w = poundsToKilograms(dialogWeight)
                                    }
                                    setDialogWeight(w)
                                }
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
            Text(text = stringResource(R.string.dialog_height_title), style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text(stringResource(R.string.dialog_height_button_save),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onSaveHeight(dialogHeight, dialogHeightUnit)
                        onDismiss()
                    }
            )
        },
        dismissButton = {
            Text(stringResource(R.string.dialog_button_height_cancel),
                Modifier
                    .padding(8.dp)
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
                                if(u != dialogHeightUnit) {
                                    var h = dialogHeight
                                    if(dialogHeightUnit == Constants.UNIT_HEIGHT_CENTIMETERS && u == Constants.UNIT_HEIGHT_INCHES) {
                                        h = centimetersToInches(dialogHeight)
                                    } else if(dialogHeightUnit == Constants.UNIT_HEIGHT_INCHES && u == Constants.UNIT_HEIGHT_CENTIMETERS) {
                                        h = inchesToCentimeters(dialogHeight)
                                    }
                                    setDialogHeight(h)
                                }
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
            Text(text = stringResource(R.string.dialog_birthdate_header), style = MaterialTheme.typography.h5)
        },
        confirmButton = {
            Text(stringResource(R.string.dialog_birthdate_button_save),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onSaveBirthdate(
                            Calendar
                                .getInstance()
                                .also {
                                    it.clear()
                                    it.set(dialogYear, dialogMonth, dialogDay)
                                })
                        onDismiss()
                    }
            )
        },
        dismissButton = {
            Text(stringResource(R.string.dialog_birthdate_button_cancel),
                Modifier
                    .padding(8.dp)
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

val AVATAR_DIALOG_ACTION_MEDIA = 1
val AVATAR_DIALOG_ACTION_DEFAULT_AVATAR = 2
val AVATAR_DIALOG_ACTION_DELETE = 3
@Composable
fun AvatarDialog(
    onChooseAvatarAction: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val (currentAction, setCurrentAction) = remember {
        mutableStateOf(-1)
    }
    AlertDialog(onDismissRequest = {},
        title = {
            Text(text = "Set Avatar", style = MaterialTheme.typography.h5)
        },
        dismissButton = {
            Text(stringResource(R.string.dialog_birthdate_button_cancel),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onDismiss()
                    },
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            val buttonActiveModifier = if(currentAction != -1) {
                Modifier.clickable {
                    onChooseAvatarAction(currentAction)
                    onDismiss()
                }
            } else {
                Modifier
            }
            Text("Choose",
                Modifier
                    .padding(8.dp)
                    .then(buttonActiveModifier),
                color = if(currentAction != -1) {
                    MaterialTheme.colors.primary
                } else {
                    Color.Gray
                },
                style = MaterialTheme.typography.body1
            )
        },
        text = {
            //TODO For some reason, when an item is selected, the "text" Composable changes boundaries
            // in a weird way. Tried wrapping the Column in a Box to see if it changes anything, but no luck
            Box {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Row(
                        Modifier
                            .selectableGroup()
                            .fillMaxWidth(),
                        Arrangement.SpaceBetween
                    ) {
                        SimpleAvatarAction(
                            isSelected = currentAction == AVATAR_DIALOG_ACTION_MEDIA,
                            imageResId = R.drawable.baseline_photo_camera_24,
                            imageContentDescription = "take picture",
                            onClick = {
                                if (currentAction == AVATAR_DIALOG_ACTION_MEDIA) {
                                    onChooseAvatarAction(currentAction)
                                    onDismiss()
                                } else {
                                    setCurrentAction(AVATAR_DIALOG_ACTION_MEDIA)
                                }

                            },
                        )
                        SimpleAvatarAction(
                            isSelected = currentAction == AVATAR_DIALOG_ACTION_DEFAULT_AVATAR,
                            imageResId = R.drawable.avatar_02,
                            imageContentDescription = "use default",
                            onClick = {
                                if (currentAction == AVATAR_DIALOG_ACTION_DEFAULT_AVATAR) {
                                    onChooseAvatarAction(currentAction)
                                    onDismiss()
                                } else {
                                    setCurrentAction(AVATAR_DIALOG_ACTION_DEFAULT_AVATAR)
                                }
                            },
                        )
                        SimpleAvatarAction(
                            isSelected = currentAction == AVATAR_DIALOG_ACTION_DELETE,
                            imageResId = R.drawable.baseline_delete_24,
                            imageContentDescription = "delete",
                            onClick = {
                                if (currentAction == AVATAR_DIALOG_ACTION_DELETE) {
                                    onChooseAvatarAction(currentAction)
                                    onDismiss()
                                } else {
                                    setCurrentAction(AVATAR_DIALOG_ACTION_DELETE)
                                }
                            },
                        )
                    }
                    val text = when (currentAction) {
                        AVATAR_DIALOG_ACTION_MEDIA -> {
                            "Take a picture"
                        }

                        AVATAR_DIALOG_ACTION_DEFAULT_AVATAR -> {
                            "Use a default avatar"
                        }

                        AVATAR_DIALOG_ACTION_DELETE -> {
                            "Delete avatar"
                        }

                        else -> {
                            null
                        }
                    }
                    if (text != null) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text,
                            Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )

}

@Composable
fun SimpleAvatarAction(
    isSelected: Boolean,
    imageResId: Int,
    imageContentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedModifier = if(isSelected){
        modifier
            .background(
                MaterialTheme.colors.primary.copy(alpha = .5f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(4.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(8.dp))
    } else {
        modifier
    }
    Image(painter = painterResource(id = imageResId),
        contentDescription = imageContentDescription,
        modifier
            .clickable {
                onClick()
            }
            .then(selectedModifier)
            .padding(16.dp)
            .size(48.dp)
    )
}

@Composable
fun DefaultAvatarDialog(
    initialDefaultAvatarId: String = "avatar_02",
    onSelectDefaultAvatar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (currentDefaultAvatar, setCurrentDefaultAvatar) = remember {
        mutableStateOf(initialDefaultAvatarId)
    }
    AlertDialog(onDismissRequest = {},
        title = {
            Text(text = "Select Default Avatar", style = MaterialTheme.typography.h5)
        },
        dismissButton = {
            Text(stringResource(R.string.dialog_birthdate_button_cancel),
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onDismiss()
                    },
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            val buttonActiveModifier = Modifier.clickable {
                    onSelectDefaultAvatar(currentDefaultAvatar)
                    onDismiss()
                }
            Text("Choose",
                Modifier
                    .padding(8.dp)
                    .then(buttonActiveModifier),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1
            )
        },
        text = {
            val itemList = avatarMap.entries.toList()
            LazyVerticalGrid(GridCells.Adaptive(80.dp), Modifier.selectableGroup(),horizontalArrangement = Arrangement.SpaceEvenly) {
                items(itemList.size, { i -> itemList[i].key}) { index ->
                    val selectedModifier = if(itemList[index].key == currentDefaultAvatar){
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(64.dp)
                            .background(
                                MaterialTheme.colors.primary.copy(alpha = .5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                4.dp,
                                MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(8.dp)
                            )

                    } else {
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(64.dp)
                    }
                    Box(Modifier.padding(vertical = 4.dp)) {
                        Image(
                            painter = painterResource(id = itemList[index].value),
                            contentDescription = "default avatar",
                            selectedModifier
                                .clickable {
                                    setCurrentDefaultAvatar(itemList[index].key)
                                }
                        )
                    }
                }
            }
        }
    )
}