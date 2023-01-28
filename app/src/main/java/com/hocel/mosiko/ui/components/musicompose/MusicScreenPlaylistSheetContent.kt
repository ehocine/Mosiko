package com.hocel.mosiko.ui.components.musicompose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hocel.mosiko.R
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.MusicControllerState
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.home.PlaylistList
import com.hocel.mosiko.ui.theme.background_light
import com.hocel.mosiko.ui.theme.typographyDmSans
import com.hocel.mosiko.ui.theme.typographySkModernist
import com.hocel.mosiko.ui.theme.white
import com.hocel.mosiko.utils.ComposeUtils

@OptIn(
    ExperimentalUnitApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun MusicScreenPlaylistSheetContent(
    dominantBackgroundColor: Color,
    isMusicPlayed: Boolean,
    currentMusicPlayed: Music,
    musicPlayList: List<Music>,
    musicControllerState: MusicControllerState,
    musicControllerViewModel: MusicControllerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.isCollapsed or (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.targetValue == BottomSheetValue.Collapsed)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(
                        if (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.targetValue == BottomSheetValue.Expanded) 0f else 1f
                    )
                    .height(
                        if (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.targetValue == BottomSheetValue.Expanded) 0.dp else 64.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 16.dp)
                        .size(32.dp, 2.dp)
                        .clip(RoundedCornerShape(100))
                        .background(white.copy(alpha = 0.2f))
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = stringResource(id = R.string.playlist),
                    style = typographyDmSans().body1.copy(
                        color = white.copy(alpha = 0.6f),
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }



        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .alpha(
                    if (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.targetValue == BottomSheetValue.Collapsed) 0f else 1f
                )
                .height(
                    if (musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.targetValue == BottomSheetValue.Expanded) 64.dp else 0.dp
                )
        ) {

            Row(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = currentMusicPlayed.albumPath.toUri()).apply(block = fun ImageRequest.Builder.() {
                                error(R.drawable.ic_music_unknown)
                                placeholder(R.drawable.ic_music_unknown)
                            }).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = currentMusicPlayed.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographyDmSans().body1.copy(
                            color = white,
                            fontSize = TextUnit(12f, TextUnitType.Sp),
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "${currentMusicPlayed.artist} • ${currentMusicPlayed.album}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographySkModernist().body1.copy(
                            color = white.copy(alpha = 0.7f),
                            fontSize = TextUnit(11f, TextUnitType.Sp),
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
            ) {

                // Previous Button
                IconButton(
                    onClick = {
                        musicControllerViewModel.previous()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_previous_filled_rounded),
                        tint = background_light,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }

                // Play or Pause Button
                IconButton(
                    onClick = {
                        if (isMusicPlayed) {
                            musicControllerViewModel.pause()
                        } else musicControllerViewModel.resume()
                    }
                ) {
                    AnimatedContent(
                        targetState = isMusicPlayed,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(300)) with
                                    scaleOut(animationSpec = tween(200))
                        }
                    ) { target ->
                        Icon(
                            painter = painterResource(
                                id = if (target) R.drawable.ic_pause_filled_rounded else R.drawable.ic_play_filled_rounded
                            ),
                            tint = background_light,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }

                // Next Button
                IconButton(
                    onClick = {
                        musicControllerViewModel.next()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next_filled_rounded),
                        tint = background_light,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            PlaylistList(
                items = musicPlayList,
                itemBackgroundColor = ComposeUtils.darkenColor(dominantBackgroundColor, 0.6f),
                currentMusicPlayed = currentMusicPlayed,
                musicControllerViewModel = musicControllerViewModel,
            )
        }
    }
}
