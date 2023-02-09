package com.hocel.mosiko.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.palette.graphics.Palette
import java.io.FileNotFoundException
import  com.hocel.mosiko.R

object ComposeUtils {

    fun getDominantColor(context: Context, uri: Uri, onGenerated: (Palette) -> Unit) {
        Palette.Builder(
            run {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                context.contentResolver,
                                uri
                            )
                        ).copy(Bitmap.Config.RGBA_F16, true)
                    } else MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } catch (e: FileNotFoundException) {
                    return@run ContextCompat.getDrawable(context, R.drawable.ic_music_unknown)!!
                        .toBitmap()
                }
            }
        ).generate {
            it?.let { palette ->
                onGenerated(palette)
            }
        }
    }

    @Composable
    fun ComponentActivity.LifecycleEventListener(event: (Lifecycle.Event) -> Unit) {
        val eventHandler by rememberUpdatedState(newValue = event)
        val lifecycle = this@LifecycleEventListener.lifecycle
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                eventHandler(event)
            }

            lifecycle.addObserver(observer)

            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }

    fun darkenColor(color: Color, factor: Float): Color {
        return Color(
            red = color.red * (1 - factor),
            green = color.green * (1 - factor),
            blue = color.blue * (1 - factor)
        )
    }

    fun lightenColor(color: Color, factor: Float): Color {
        return Color(
            red = color.red + (1 - color.red) * factor,
            green = color.green + (1 - color.green) * factor,
            blue = color.blue + (1 - color.blue) * factor
        )
    }
}