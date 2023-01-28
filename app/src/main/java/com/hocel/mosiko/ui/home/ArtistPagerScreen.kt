package com.hocel.mosiko.ui.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
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
import com.hocel.mosiko.R
import com.hocel.mosiko.data.MusyDestination
import com.hocel.mosiko.ui.home.HomeViewModel
import com.hocel.mosiko.ui.theme.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ArtistPagerScreen(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
) {

    val artistList by homeViewModel.artistList.observeAsState(initial = emptyMap())

    homeViewModel.getAllArtist()

    if (artistList.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_audio_square_outlined),
                tint = if (isSystemInDarkTheme()) background_content_dark else background_content_light,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
            )

            Text(
                text = stringResource(id = R.string.no_artist),
                style = typographyDmSans().body1.copy(
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(
                fraction = if (artistList.isNotEmpty()) 1f else 0f
            )
            .padding(bottom = 64.dp)
    ) {
        items(artistList.toList()) { musicPair ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = musicPair.second[0].artist,
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
                        val route = MusyDestination.Artist.createRoute(musicPair.second[0].artist)
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
    }
}
