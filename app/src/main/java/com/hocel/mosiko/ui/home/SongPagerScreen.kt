package com.hocel.mosiko.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hocel.mosiko.R
import com.hocel.mosiko.common.ScanMusicViewModel
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.components.MusicItem
import com.hocel.mosiko.ui.components.ScanMusicProgressIndicator
import com.hocel.mosiko.ui.components.TransparentButton
import com.hocel.mosiko.ui.components.musicompose.LottieAnim
import com.hocel.mosiko.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun SongPagerScreen(
    homeViewModel: HomeViewModel,
    musicControllerViewModel: MusicControllerViewModel,
    scanMusicViewModel: ScanMusicViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val musicList by homeViewModel.musicList.collectAsState(initial = emptyList())
    val currentMusicPlayed by musicControllerViewModel.currentMusicPlayed.observeAsState(initial = Music.unknown)

    var selectedMusic by remember { mutableStateOf(Music.unknown) }
    val scannedMusicInPercent by scanMusicViewModel.scannedMusicInPercent.observeAsState(initial = 0)

    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    when (modalBottomSheetState.isVisible) {
        true -> musicControllerViewModel.hideMiniMusicPlayer()
        else -> musicControllerViewModel.showMiniMusicPlayer()
    }

    ModalBottomSheetLayout(
        scrimColor = black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = if (isSystemInDarkTheme()) background_content_dark else background_content_light,
        sheetContent = {
            DeleteMusicSheetContent(
                music = selectedMusic,
                scope = scope,
                modalBottomSheetState = modalBottomSheetState,
                musicControllerViewModel = musicControllerViewModel,
                homeViewModel = homeViewModel
            )
        }
    ) {
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
                            LottieAnim(
                                modifier = Modifier.size(200.dp),
                                lottie = R.raw.empty_state
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
                                scannedMusicInPercent = scannedMusicInPercent
                            )
                        }
                    }
                }
            }
            else -> {
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = scanMusicViewModel.songsScanState.value),
                        onRefresh = {
                            scanMusicViewModel.scanLocalSong(context)
                            {
                                homeViewModel.refreshSongsList.value = true
                            }
                        }) {
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
                                    },
                                    enableDeleteAction = true,
                                    deleteMusic = { musicItem ->
                                        selectedMusic = musicItem
                                        scope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun DeleteMusicSheetContent(
    music: Music,
    scope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    musicControllerViewModel: MusicControllerViewModel,
    homeViewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.delete),
            style = typographyDmSans().body1.copy(
                color = typographyDmSans().body1.color.copy(alpha = 0.4f),
                fontSize = TextUnit(13f, TextUnitType.Sp)
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                //TODO: remove music from playlist, album and its artist if exist
                musicControllerViewModel.deleteMusic(music) {
                    homeViewModel.deleteMusicFromList(music)
                    if (musicControllerViewModel.currentMusicPlayed.value!!.audioID == music.audioID)
                        musicControllerViewModel.resetCurrentMusic()
                    scope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.ok),
                style = typographyDmSans().body1.copy(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
        }

        Divider(
            color = if (isSystemInDarkTheme()) {
                background_content_light.copy(alpha = 0.4f)
            } else background_content_dark.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                scope.launch {
                    modalBottomSheetState.hide()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.cancel),
                style = typographyDmSans().body1.copy(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
        }
    }
}
