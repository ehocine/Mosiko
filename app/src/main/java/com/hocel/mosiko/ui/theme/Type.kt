package com.hocel.mosiko.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hocel.mosiko.R

@Composable
fun dMSansFontFamily() = FontFamily(
    Font(R.font.sailec_regular)
)

@Composable
fun sKModernistFontFamily() = FontFamily(
    Font(R.font.sailec_regular)
)

@Composable
fun typographyDmSans() = Typography(
    body1 = TextStyle(
        fontFamily = dMSansFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = if (isSystemInDarkTheme()) white else black,
    )
)

@Composable
fun typographySkModernist() = Typography(
    body1 = TextStyle(
        fontFamily = sKModernistFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = if (isSystemInDarkTheme()) white else black,
    )
)
