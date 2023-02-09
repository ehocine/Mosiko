package com.hocel.mosiko.ui.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hocel.mosiko.R
import com.hocel.mosiko.data.MosikoDestination
import com.hocel.mosiko.ui.components.musicompose.LottieAnim
import com.hocel.mosiko.ui.theme.*

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
            LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
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
                        val route = MosikoDestination.Artist.createRoute(musicPair.second[0].artist)
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        tint = if (isSystemInDarkTheme()) white else black,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
