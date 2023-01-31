package com.hocel.mosiko.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hocel.mosiko.data.MosikoDestination
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.ui.components.AlbumItem
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.R
import com.hocel.mosiko.ui.components.musicompose.LottieAnim


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalUnitApi::class
)
@Composable
fun AlbumPagerScreen(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
) {

    val albumList by homeViewModel.albumList.observeAsState(initial = emptyMap())

    homeViewModel.getAllAlbum()

    if (albumList.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
            Text(
                text = stringResource(id = R.string.no_album),
                style = typographyDmSans().body1.copy(
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(
                    fraction = if (albumList.isNotEmpty()) 1f else 0f
                )
                .padding(bottom = 64.dp)
        ) {
            itemsIndexed(albumList.toList()) { i, musicPair ->
                AlbumItem(
                    musicList = musicPair.second,
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
        }
    }
}
