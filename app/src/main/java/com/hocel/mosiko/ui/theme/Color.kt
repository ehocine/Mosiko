package com.hocel.mosiko.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val white = Color(0xFFFFFFFF)
val black = Color(0xFF000000)

//val black = Color(0xFF000000)
val sunset_orange = Color(0xFFFE554A)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)

val Teal200 = Color(0xFF03DAC5)
val Teal900 = Color(0xFF018786)

val primary_dark = Purple200
val primary_light = Purple500

val primary_variant_dark = Purple700
val primary_variant_light = Purple700

val on_primary_dark = black
val on_primary_light = white

val secondary_dark = Teal200
val secondary_light = Teal200
val secondary_variant_light = Teal900

val on_secondary_dark = black
val on_secondary_light = black

val surface_dark = black
val surface_light = white

val on_surface_dark = white
val on_surface_light = black

val background_content_dark = Color(0xFF222222)
val background_content_light = Color(0xFFEEEEEE)

val background_dark = black
val background_light = white

val on_background_dark = white
val on_background_light = black

val error_dark = Color(0xFFCF6679)
val error_light = Color(0xFFB00020)

val on_error_dark = black
val on_error_light = white

val BlackWithAlpha = Color(0xAA000000)

val backgroundColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) black else white

val iconColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) white else black

val textColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) white else black

val scanMusicColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val progressIndicatorColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val cursorIndicator: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black
