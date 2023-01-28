package com.hocel.mosiko.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hocel.mosiko.common.AppDatastore
import com.hocel.mosiko.data.MusyDestination
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist
import com.hocel.mosiko.ui.album.AlbumScreen
import com.hocel.mosiko.ui.album.AlbumViewModel
import com.hocel.mosiko.ui.artist.ArtistScreen
import com.hocel.mosiko.ui.artist.ArtistViewModel
import com.hocel.mosiko.ui.home.HomeScreen
import com.hocel.mosiko.ui.home.HomeViewModel
import com.hocel.mosiko.ui.playlist.PlaylistScreen
import com.hocel.mosiko.ui.playlist.PlaylistViewModel
import com.hocel.mosiko.ui.search.SearchScreen
import com.hocel.mosiko.ui.search.SearchSongScreen
import com.hocel.mosiko.ui.search.SearchViewModel

@Composable
fun MusyNavigation(
    navigationController: NavHostController,
    datastore: AppDatastore,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    playlistViewModel: PlaylistViewModel,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    musicControllerViewModel: MusicControllerViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navigationController,
        startDestination = MusyDestination.Home.route,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {

        composable(MusyDestination.Home.route) {
            HomeScreen(
                navController = navigationController,
                musicControllerViewModel = musicControllerViewModel,
                homeViewModel = homeViewModel,
                datastore = datastore
            )
        }

        composable(MusyDestination.Search.route) {
            SearchScreen(
                navController = navigationController,
                searchViewModel = searchViewModel,
                musicControllerViewModel = musicControllerViewModel
            )
        }

        composable(
            route = MusyDestination.SearchSong.route,
            arguments = listOf(
                navArgument("playlistID") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val playlistID = entry.arguments?.getInt("playlistID") ?: Playlist.unknown.id
            SearchSongScreen(
                playlistID = playlistID,
                navController = navigationController,
                searchViewModel = searchViewModel,
                musicControllerViewModel = musicControllerViewModel
            )
        }

        composable(
            route = MusyDestination.Artist.route,
            arguments = listOf(
                navArgument("artistName") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val artistName = entry.arguments?.getString("artistName") ?: Music.unknown.artist
            ArtistScreen(
                artistName = artistName,
                artistViewModel = artistViewModel,
                musicControllerViewModel = musicControllerViewModel,
                navController = navigationController
            )
        }

        composable(
            route = MusyDestination.Album.route,
            arguments = listOf(
                navArgument("albumID") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val albumID = entry.arguments?.getString("albumID") ?: Music.unknown.albumID
            AlbumScreen(
                albumID = albumID,
                albumViewModel = albumViewModel,
                musicControllerViewModel = musicControllerViewModel,
                navController = navigationController
            )
        }

        composable(
            route = MusyDestination.Playlist.route,
            arguments = listOf(
                navArgument("playlistID") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val playlistID = entry.arguments?.getInt("playlistID") ?: Playlist.unknown.id
            PlaylistScreen(
                playlistID = playlistID,
                playlistViewModel = playlistViewModel,
                musicControllerViewModel = musicControllerViewModel,
                navController = navigationController
            )
        }

    }
}
