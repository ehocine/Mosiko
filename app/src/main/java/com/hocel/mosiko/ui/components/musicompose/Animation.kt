package com.hocel.mosiko.ui.components.musicompose

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*


/**
 * @param modifier change the component behavior
 * @param lottie introduce a .json based animation file
 */
@Composable
fun LottieAnim(modifier: Modifier, lottie: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottie))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        modifier = modifier.size(300.dp),
        composition = composition,
        progress = { progress }
    )
}