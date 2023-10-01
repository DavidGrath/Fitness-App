package com.davidgrath.fitnessapp.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.davidgrath.fitnessapp.R

val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_extra_light, FontWeight.ExtraLight),
    Font(R.font.nunito_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.nunito_semi_bold, FontWeight.SemiBold),
    Font(R.font.nunito_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.nunito_extra_bold, FontWeight.ExtraBold),
    Font(R.font.nunito_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.nunito_black, FontWeight.Black),
    Font(R.font.nunito_black_italic, FontWeight.Black, FontStyle.Italic),
)

val NunitoTypography = Typography(
    defaultFontFamily = NunitoFontFamily,
    h4 = TextStyle(fontSize = 36.sp, letterSpacing = 0.25.sp, fontWeight = FontWeight.Bold),
    h5 = TextStyle(fontSize = 20.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Bold),
    button = TextStyle(fontSize = 14.sp, letterSpacing = -0.4.sp, fontWeight = FontWeight.Bold),
    caption = TextStyle(fontSize = 12.sp, letterSpacing = 0.4.sp, fontWeight = FontWeight.Bold)
)
