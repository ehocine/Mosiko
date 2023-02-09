package com.hocel.mosiko.ui.artist

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.components.MusicItem
import com.hocel.mosiko.ui.theme.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ArtistScreen(
    artistName: String,
    artistViewModel: ArtistViewModel,
    musicControllerViewModel: MusicControllerViewModel,
    navController: NavHostController
) {

    val currentMusicPlayed by musicControllerViewModel.currentMusicPlayed.observeAsState(initial = Music.unknown)
    val filteredMusicList by artistViewModel.filteredMusicList.observeAsState(initial = emptyList())

    var hasNavigate by remember { mutableStateOf(false) }

    if (!hasNavigate) {
        artistViewModel.filterMusic(artistName)
        true.also { hasNavigate = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light,
                elevation = 0.dp,
                title = {
                    Text(
                        text = artistName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographyDmSans().body1.copy(
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            tint = if (isSystemInDarkTheme()) white else black,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 64.dp)
        ) {
            items(filteredMusicList) { music ->
                MusicItem(
                    music = music,
                    isMusicPlayed = currentMusicPlayed.audioID == music.audioID,
                    onClick = {
                        if (currentMusicPlayed.audioID != music.audioID) {
                            musicControllerViewModel.play(music.audioID)
                            musicControllerViewModel.getPlaylist()
                        }
                    },
                    deleteMusic = {}
                )
            }
        }
    }
}
