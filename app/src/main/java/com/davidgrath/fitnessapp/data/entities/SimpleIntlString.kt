package com.davidgrath.fitnessapp.data.entities

import com.google.gson.annotations.SerializedName

data class SimpleIntlString(
    @SerializedName("default")
    val _default: String,
    val jp: String? = null //No, I won't do Japanese. It's just here to represent the concept of
    // handling internationalization that I made up. Pretty sure real i18n is more involved
)
