package com.hocel.mosiko.ui.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hocel.mosiko.MainActivity
import com.hocel.mosiko.R
import com.hocel.mosiko.common.AppDatastore
import com.hocel.mosiko.common.ScanMusicViewModel
import com.hocel.mosiko.data.MosikoDestination
import com.hocel.mosiko.model.MusicControllerState
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.utils.AppUtils
import com.hocel.mosiko.utils.ComposeUtils.LifecycleEventListener
import com.hocel.mosiko.utils.showInterstitial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class, ExperimentalUnitApi::class
)
@Composable
fun HomeScreen(
    navController: NavHostController,
    musicControllerViewModel: MusicControllerViewModel,
    homeViewModel: HomeViewModel,
    scanMusicViewModel: ScanMusicViewModel = hiltViewModel(),
    datastore: AppDatastore
) {

    val context = LocalContext.current

    val sortMusicItem = listOf(
        stringResource(id = R.string.date_added) to AppUtils.PreferencesValue.SORT_MUSIC_BY_DATE_ADDED,
        stringResource(id = R.string.song_name) to AppUtils.PreferencesValue.SORT_MUSIC_BY_NAME,
        stringResource(id = R.string.artist_name) to AppUtils.PreferencesValue.SORT_MUSIC_BY_ARTIST_NAME,
    )

    val pages = listOf(
        stringResource(id = R.string.song),
        stringResource(id = R.string.album),
        stringResource(id = R.string.artist),
        stringResource(id = R.string.playlist),
    )

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val modalBottomSheetSortOptionState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val sortMusicOption by datastore.getSortMusicOption.collectAsState(initial = AppUtils.PreferencesValue.SORT_MUSIC_BY_NAME)
    val musicControllerState by musicControllerViewModel.musicControllerState.observeAsState(
        initial = MusicControllerState.initial
    )

    var hasNavigate by remember { mutableStateOf(false) }

    musicControllerViewModel.showMiniMusicPlayer()

    // get all playlist when currentPage is "playlist"
    if (pagerState.currentPage == 3) {
        homeViewModel.getAllPlaylist()
    }

    if (!hasNavigate || homeViewModel.refreshSongsList.value) {
        (context as MainActivity).LifecycleEventListener {
            if (it == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    // get current sort music option and get all music
                    datastore.getSortMusicOption.collect { option ->
                        withContext(Dispatchers.Main) {
                            homeViewModel.getAllMusic(option)
                        }
                    }
                }
            }
        }
        true.also { hasNavigate = it }
    }

    BackHandler {
        when {
            modalBottomSheetSortOptionState.isVisible -> scope.launch {
                modalBottomSheetSortOptionState.hide()
            }
            musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.isExpanded -> scope.launch {
                musicControllerState.playlistScaffoldBottomSheetState.bottomSheetState.collapse()
            }
            musicControllerState.addToPlaylistModalBottomSheetState.isVisible -> scope.launch {
                musicControllerState.addToPlaylistModalBottomSheetState.hide()
            }
            musicControllerState.setTimerModalBottomSheetState.isVisible -> scope.launch {
                musicControllerState.setTimerModalBottomSheetState.hide()
            }
            musicControllerState.musicMoreOptionModalBottomSheetState.isVisible -> scope.launch {
                musicControllerState.musicMoreOptionModalBottomSheetState.hide()
            }
            musicControllerState.musicScaffoldBottomSheetState.bottomSheetState.isExpanded -> scope.launch {
                musicControllerState.musicScaffoldBottomSheetState.bottomSheetState.collapse()
            }
            pagerState.currentPage != 0 -> scope.launch {
                pagerState.animateScrollToPage(0)
            }
            else -> (context as MainActivity).finishAffinity()
        }
    }

    when (modalBottomSheetSortOptionState.isVisible) {
        true -> musicControllerViewModel.hideMiniMusicPlayer()
        else -> musicControllerViewModel.showMiniMusicPlayer()
    }

    // BottomSheet sort option
    ModalBottomSheetLayout(
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(32.dp),
        scrimColor = black.copy(alpha = 0.6f),
        sheetBackgroundColor = if (isSystemInDarkTheme()) background_content_dark else white,
        sheetState = modalBottomSheetSortOptionState,
        sheetContent = {

            // BottomSheet sort option sheet content
            Column(
                modifier = Modifier
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by),
                    style = typographyDmSans().body1.copy(
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp, top = 16.dp)
                )

                sortMusicItem.forEach { pair ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                datastore.setSortMusicOption(pair.second) {
                                    scope.launch { modalBottomSheetSortOptionState.hide() }
                                    homeViewModel.getAllMusic(pair.second)
                                }
                            }
                    ) {
                        Text(
                            text = pair.first,
                            style = typographySkModernist().body1.copy(
                                fontSize = TextUnit(16f, TextUnitType.Sp)
                            ),
                            modifier = Modifier
                                .wrapContentWidth(Alignment.Start)
                                .padding(top = 14.dp, bottom = 14.dp, start = 16.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        RadioButton(
                            selected = pair.second == sortMusicOption,
                            onClick = {
                                datastore.setSortMusicOption(pair.second) {
                                    scope.launch { modalBottomSheetSortOptionState.hide() }
                                    homeViewModel.getAllMusic(pair.second)
                                }
                            },
                            modifier = Modifier
                                .wrapContentSize(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                    }
                }
            }
            // BottomSheet sort option sheet content ~
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    elevation = 0.dp,
                    backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(MosikoDestination.Search.route) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = iconColor
                            )
                        }
                    }
                }
            }
        ) {
            Surface(Modifier.fillMaxSize(), color = if (isSystemInDarkTheme()) black else white) {
                showInterstitial(context)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSystemInDarkTheme()) black else white)
                ) {
                    ScrollableTabRow(
                        backgroundColor = if (isSystemInDarkTheme()) black else white,
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 8.dp,
                        divider = {},
                        indicator = {
                            TabRowDefaults.Indicator(
                                height = 2.4f.dp,
                                color = sunset_orange,
                                modifier = Modifier
                                    .pagerTabIndicatorOffset(pagerState, it)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    ) {
                        pages.forEachIndexed { index, label ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                selectedContentColor = Color.Transparent,
                                text = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        style = typographyDmSans().body1.copy(
                                            color = if (pagerState.currentPage == index) sunset_orange
                                            else {
                                                if (isSystemInDarkTheme()) white else black
                                            },
                                            fontSize = TextUnit(14f, TextUnitType.Sp),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                },
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        count = pages.size,
                        modifier = Modifier
                            .padding(top = 16.dp)
                    ) { page ->
                        when (page) {
                            0 -> SongPagerScreen(
                                homeViewModel = homeViewModel,
                                musicControllerViewModel = musicControllerViewModel,
                                scanMusicViewModel = scanMusicViewModel
                            )
                            1 -> AlbumPagerScreen(
                                homeViewModel = homeViewModel,
                                navController = navController,
                            )
                            2 -> ArtistPagerScreen(
                                homeViewModel = homeViewModel,
                                navController = navController,
                            )
                            3 -> PlaylistPagerScreen(
                                homeViewModel = homeViewModel,
                                navController = navController,
                                musicControllerViewModel = musicControllerViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
