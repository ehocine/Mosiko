package com.hocel.mosiko.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hocel.mosiko.R
import com.hocel.mosiko.data.MosikoDestination
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.components.AlbumItem
import com.hocel.mosiko.ui.components.MusicItem
import com.hocel.mosiko.ui.theme.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    musicControllerViewModel: MusicControllerViewModel
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    val currentMusicPlayed by musicControllerViewModel.currentMusicPlayed.observeAsState(initial = Music.unknown)
    val filteredMusic by searchViewModel.filteredMusic.observeAsState(initial = emptyList())
    val filteredArtist by searchViewModel.filteredArtist.observeAsState(initial = emptyList())
    val filteredAlbum by searchViewModel.filteredAlbum.observeAsState(initial = emptyList())

    var query by remember { mutableStateOf("") }
    var hasNavigate by remember { mutableStateOf(false) }
    val searchTextFieldFocusRequester = remember { FocusRequester() }

    val albumList = filteredAlbum.groupBy { it.album }

    if (!hasNavigate) {
        LaunchedEffect(Unit) {
            searchTextFieldFocusRequester.requestFocus()
        }
        true.also { hasNavigate = it }
    }

    searchViewModel.filter(query)

    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextField(
                            value = query,
                            singleLine = true,
                            onValueChange = { s ->
                                query = s
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        when (query.isNotEmpty()) {
                                            true -> {
                                                query = ""
                                            }
                                            else -> {
                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(16.dp)
                                    )
                                }
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.search_placeholder),
                                    style = typographySkModernist().body1.copy(
                                        color = textColor
                                    )
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 8.dp)
                                .focusRequester(searchTextFieldFocusRequester)
                        )
                    }

                    Divider(
                        color = background_content_dark,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    ) {
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 64.dp)
            ) {
                item {
                    if (filteredMusic.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.song),
                                style = typographyDmSans().body1.copy(
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp, top = 16.dp, bottom = 8.dp)
                            )

                            Text(
                                text = "(${filteredMusic.size})",
                                style = typographyDmSans().body1.copy(
                                    color = typographyDmSans().body1.color.copy(alpha = 0.6f),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 14.dp, top = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }

                items(filteredMusic) { music ->
                    MusicItem(
                        music = music,
                        isMusicPlayed = currentMusicPlayed.audioID == music.audioID,
                        showImage = false,
                        showDuration = false,
                        onClick = {
                            if (currentMusicPlayed.audioID != music.audioID) {
                                musicControllerViewModel.play(music.audioID)
                            }
                        },
                        deleteMusic = {},
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    )
                }

                item {
                    if (filteredArtist.isNotEmpty()) {

                        if (filteredMusic.isNotEmpty()) {
                            Divider(
                                color = background_content_dark,
                                thickness = 1.4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.artist),
                                style = typographyDmSans().body1.copy(
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp, top = 16.dp, bottom = 8.dp)
                            )

                            Text(
                                text = "(${filteredArtist.size})",
                                style = typographyDmSans().body1.copy(
                                    color = typographyDmSans().body1.color.copy(alpha = 0.6f),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 14.dp, top = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }

                items(filteredArtist) { music ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = music.artist,
                            style = typographySkModernist().body1.copy(
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontWeight = FontWeight.SemiBold
                            ),
                        )

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val route = MosikoDestination.Artist.createRoute(music.artist)
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowRight,
                                tint = background_content_dark,
                                contentDescription = null
                            )
                        }
                    }
                }

                item {
                    if (albumList.isNotEmpty()) {

                        if (filteredMusic.isNotEmpty() and filteredArtist.isNotEmpty()) {
                            Divider(
                                color = background_content_dark,
                                thickness = 1.4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.album),
                                style = typographyDmSans().body1.copy(
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp, top = 16.dp, bottom = 8.dp)
                            )

                            Text(
                                text = "(${albumList.size})",
                                style = typographyDmSans().body1.copy(
                                    color = typographyDmSans().body1.color.copy(alpha = 0.6f),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 14.dp, top = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }

                items(albumList.size) { i ->
                    AlbumItem(
                        musicList = albumList[albumList.keys.toList()[i]]!!,
                        onClick = {
                            val route = MosikoDestination.Album.createRoute(
                                albumList[albumList.keys.toList()[i]]?.get(0)?.albumID
                                    ?: Music.unknown.albumID
                            )

                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    )
                }

            }
        }
    }
}
