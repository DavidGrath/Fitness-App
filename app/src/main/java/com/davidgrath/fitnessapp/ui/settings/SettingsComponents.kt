package com.davidgrath.fitnessapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidgrath.fitnessapp.R
import com.davidgrath.fitnessapp.ui.components.SimpleAppBar


@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        val scrollState = rememberScrollState()
        val (syncToGoogleFitChecked, setSyncToGoogleFitChecked) = remember {
            mutableStateOf(false)
        }
        val (remindWorkout, setRemindWorkout) = remember {
            mutableStateOf(false)
        }

        SimpleAppBar("Settings", false, onNavigateBack)
        Spacer(Modifier.height(8.dp))

        Column(
            Modifier
                .verticalScroll(scrollState).fillMaxSize()) {
            Spacer(Modifier.height(16.dp))
            Column {
                Text(
                    "General Settings",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                SimpleSettingsItem(R.drawable.sync,"Sync to Google Fit", {},
                    checkable = true, syncToGoogleFitChecked, setSyncToGoogleFitChecked)
                SimpleSettingsItem(R.drawable.bell_outline,"Remind me to workout", {},
                    checkable = true, remindWorkout, setRemindWorkout)
                SimpleSettingsItem(iconResId = R.drawable.ruler, text = "Units", onClick = {}, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.translate, text = "Language options", onClick = {}, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.restart, text = "Restart Progress", onClick = {}, checkable = false)
            }

            Spacer(Modifier.height(16.dp))
            Divider(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            Column {
                Text(
                    "More",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                SimpleSettingsItem(iconResId = R.drawable.face_agent, text = "Support & Feedback", onClick = {}, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.pen, text = "Terms & Conditions", onClick = {}, checkable = false)
                SimpleSettingsItem(iconResId = R.drawable.shield, text = "Privacy Policy", onClick = {}, checkable = false)
            }

            Spacer(Modifier.height(16.dp))
            Divider(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            SimpleSettingsItem(iconResId = R.drawable.logout, text = "Log out", onClick = {}, checkable = false)

            Spacer(Modifier.height(16.dp))
        }
    }
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