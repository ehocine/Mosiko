package com.hocel.mosiko.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hocel.mosiko.ui.theme.typographySkModernist


@OptIn(ExperimentalUnitApi::class)
@Composable
fun ScanMusicProgressIndicator(
    numberMarker: Int,
    markerActive: Float,
    scannedMusicInPercent: Int,
    progressAngle: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        for (i in 0 until numberMarker) {
            ScanMusicProgressMarker(
                angle = i * (360 / numberMarker),
                active = i < markerActive
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(95.dp)
        ) {
            Text(
                text = "$scannedMusicInPercent%",
                style = typographySkModernist().body1.copy(
                    fontSize = TextUnit(36f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        ScanMusicProgress(
            angle = progressAngle
        )
    }
}