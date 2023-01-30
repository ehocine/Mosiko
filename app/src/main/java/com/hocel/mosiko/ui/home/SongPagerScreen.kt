package com.hocel.mosiko.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.R
import com.hocel.mosiko.ui.components.MusicItem
import com.hocel.mosiko.common.ScanMusicViewModel
import com.hocel.mosiko.ui.components.ScanMusicProgressIndicator

//TODO: scan music on start
// Add refresh button to rescan
@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun SongPagerScreen(
    homeViewModel: HomeViewModel,
    musicControllerViewModel: MusicControllerViewModel,
    scanMusicViewModel: ScanMusicViewModel
) {
    val context = LocalContext.current
    val musicList by homeViewModel.musicList.observeAsState(initial = emptyList())
    val currentMusicPlayed by musicControllerViewModel.currentMusicPlayed.observeAsState(initial = Music.unknown)

    val numberMarker = 90
    val scannedMusicInPercent by scanMusicViewModel.scannedMusicInPercent.observeAsState(initial = 0)

    val progressAngle by animateFloatAsState(
        targetValue = (scannedMusicInPercent.toFloat() / 100f) * 360f
    )

    val markerActive by animateFloatAsState(
        targetValue = (scannedMusicInPercent.toFloat() / 100) * numberMarker
    )

    when (musicList.isEmpty()) {
        true -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp)
            ) {
                when (scanMusicViewModel.songsScanState.value) {
                    false -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_audio_square_outlined),
                            tint = if (isSystemInDarkTheme()) background_content_dark else background_content_light,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.no_song),
                            style = typographyDmSans().body1.copy(
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .padding(top = 8.dp)
                        )

                        Button(
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                hoveredElevation = 0.dp,
                                focusedElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = sunset_orange.copy(alpha = 0.4f),
                                contentColor = Color.Transparent
                            ),
                            onClick = {

                                scanMusicViewModel.scanLocalSong(context)
                                {
                                    homeViewModel.refreshSongsList.value = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 12.dp,
                                    start = 32.dp,
                                    end = 32.dp
                                )
                        ) {
                            Text(
                                text = stringResource(id = R.string.scan_local_songs),
                                style = typographySkModernist().body1.copy(
                                    color = sunset_orange,
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                    else -> {
                        ScanMusicProgressIndicator(
                            numberMarker = numberMarker,
                            markerActive = markerActive,
                            scannedMusicInPercent = scannedMusicInPercent,
                            progressAngle = progressAngle
                        )
                    }
                }
            }
        }
        else -> {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(
                            fraction = if (musicList.isNotEmpty()) 1f else 0f
                        )
                        .padding(bottom = 64.dp)
                ) {
                    items(musicList) { music ->
                        MusicItem(
                            music = music,
                            isMusicPlayed = currentMusicPlayed.audioID == music.audioID,
                            onClick = {
                                if (currentMusicPlayed.audioID != music.audioID) {
                                    musicControllerViewModel.play(music.audioID)
                                    musicControllerViewModel.getPlaylist()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
