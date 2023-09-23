package com.davidgrath.fitnessapp.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FitnessAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        typography = NunitoTypography,
        content = content
    )
}