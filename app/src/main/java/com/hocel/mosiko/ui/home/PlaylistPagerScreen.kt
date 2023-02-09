package com.hocel.mosiko.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hocel.mosiko.R
import com.hocel.mosiko.data.MosikoDestination
import com.hocel.mosiko.model.Playlist
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.components.PlaylistItem
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.utils.AppUtils.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun PlaylistPagerScreen(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    musicControllerViewModel: MusicControllerViewModel
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val newPlaylistModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val playlist by homeViewModel.playlist.observeAsState(initial = emptyList())

    var playlistName by remember { mutableStateOf("") }.apply {
        value = stringResource(id = R.string.my_playlist)
    }
    val textFieldPlaylistNameFocusRequester = remember { FocusRequester() }

    BackHandler(
        enabled = newPlaylistModalBottomSheetState.isVisible,
        onBack = {
            when {
                newPlaylistModalBottomSheetState.isVisible -> scope.launch {
                    newPlaylistModalBottomSheetState.hide()
                }
            }
        }
    )

    if (newPlaylistModalBottomSheetState.isVisible) {
        musicControllerViewModel.hideMiniMusicPlayer()
        LaunchedEffect(Unit) {
            delay(800)
            textFieldPlaylistNameFocusRequester.requestFocus()
        }
    } else {
        focusManager.clearFocus(force = true)
        LaunchedEffect(Unit) {
            delay(800)
            musicControllerViewModel.showMiniMusicPlayer()
        }
    }

    homeViewModel.getAllPlaylist()

    // New playlist bottom sheet
    ModalBottomSheetLayout(
        sheetState = newPlaylistModalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = black.copy(alpha = 0.6f),
        sheetBackgroundColor = if (isSystemInDarkTheme()) background_content_dark else white,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 8.dp)
                        .size(32.dp, 2.dp)
                        .clip(RoundedCornerShape(100))
                        .background(white.copy(alpha = 0.2f))
                        .align(Alignment.CenterHorizontally)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 24.dp)
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                newPlaylistModalBottomSheetState.hide()
                                musicControllerViewModel.showMiniMusicPlayer()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = if (isSystemInDarkTheme()) white else black,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.new_playlist),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        style = typographyDmSans().body1.copy(
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (playlistName.isNotBlank()) {
                                musicControllerViewModel.newPlaylist(
                                    playlist = Playlist(
                                        name = playlistName,
                                        musicList = emptyList()
                                    ),
                                    action = {
                                        playlistName = context.getString(R.string.my_playlist)
                                        scope.launch {
                                            newPlaylistModalBottomSheetState.hide()
                                            musicControllerViewModel.showMiniMusicPlayer()
                                        }
                                    }
                                )
                            } else context.getString(R.string.playlist_name_cannot_be_empty)
                                .toast(context)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            tint = if (isSystemInDarkTheme()) white else black,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
                TextField(
                    value = playlistName,
                    singleLine = true,
                    textStyle = typographySkModernist().body1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                        }
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = cursorIndicator,
                        cursorColor = cursorIndicator
                    ),
                    onValueChange = { s ->
                        if (playlistName.length < 25) playlistName = s
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.enter_playlist_name)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 32.dp)
                        .focusRequester(textFieldPlaylistNameFocusRequester)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp)
                    .align(Alignment.Center)
            ) {
                items(playlist) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = {
                            val route = MosikoDestination.Playlist.createRoute(playlist.id)
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            ExtendedFloatingActionButton(
                backgroundColor = if (isSystemInDarkTheme()) white else black,
                onClick = {
                    scope.launch {
                        newPlaylistModalBottomSheetState.show()
                    }
                },
                modifier = Modifier
                    .padding(
                        bottom = 32.dp,
                        end = 32.dp
                    )
                    .align(Alignment.BottomEnd),
                text = {
                    Text(
                        text = stringResource(R.string.create_new_playlist),
                        color = if (isSystemInDarkTheme()) black else white
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        tint = if (isSystemInDarkTheme()) black else white,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
