package com.hocel.mosiko.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hocel.mosiko.R
import com.hocel.mosiko.ui.components.musicompose.LottieAnim
import com.hocel.mosiko.ui.theme.scanMusicColor
import com.hocel.mosiko.ui.theme.typographySkModernist


@Composable
fun ScanMusicProgressIndicator(
    scannedMusicInPercent: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnim(modifier = Modifier.size(150.dp), lottie = R.raw.data_scanning)
            Text(
                text = stringResource(R.string.loading_local_music),
                textAlign = TextAlign.Center,
                style = typographySkModernist().body1.copy(
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                color = scanMusicColor
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = "$scannedMusicInPercent%",
                style = typographySkModernist().body1.copy(
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                color = scanMusicColor
            )
        }
    }
}