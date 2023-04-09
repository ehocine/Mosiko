package com.hocel.mosiko

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hocel.mosiko.common.AppDatastore
import com.hocel.mosiko.common.MediaPlayerAction
import com.hocel.mosiko.common.MediaPlayerService
import com.hocel.mosiko.common.SettingsContentObserver
import com.hocel.mosiko.model.Playlist
import com.hocel.mosiko.ui.album.AlbumViewModel
import com.hocel.mosiko.ui.artist.ArtistViewModel
import com.hocel.mosiko.ui.home.HomeViewModel
import com.hocel.mosiko.ui.playlist.PlaylistViewModel
import com.hocel.mosiko.common.ScanMusicViewModel
import com.hocel.mosiko.ui.search.SearchViewModel
import com.hocel.mosiko.ui.theme.MusyTheme
import com.hocel.mosiko.utils.AppUtils.containBy
import com.hocel.mosiko.utils.AppUtils.toast
import com.hocel.mosiko.utils.DatabaseUtil
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.MosikoApp
import com.hocel.mosiko.utils.loadInterstitial
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ServiceConnection {

    @Inject
    lateinit var datastore: AppDatastore

    @Inject
    lateinit var databaseUtil: DatabaseUtil

    @Inject
    lateinit var musicControllerViewModel: MusicControllerViewModel

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var scanMusicViewModel: ScanMusicViewModel

    @Inject
    lateinit var searchViewModel: SearchViewModel

    @Inject
    lateinit var artistViewModel: ArtistViewModel

    @Inject
    lateinit var albumViewModel: AlbumViewModel

    @Inject
    lateinit var playlistViewModel: PlaylistViewModel

    private var mediaPlayerService: MediaPlayerService? = null

    // if there is a volume change, it will call musicControllerViewModel.onVolumeChange()
    private val settingsContentObserver = SettingsContentObserver {
        musicControllerViewModel.onVolumeChange()
    }

    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                "Permission is required in order to access music".toast(this, Toast.LENGTH_LONG)
                finishAffinity()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    // relaunch app
                    startActivity(Intent(this, MainActivity::class.java))
                }, 800)
                finishAffinity()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            val serviceIntent = Intent(this, MediaPlayerService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            startForegroundService(serviceIntent)
            bindService(
                serviceIntent,
                this,
                BIND_AUTO_CREATE
            )

            // register SettingsContentObserver, used to observe changes in volume
            contentResolver.registerContentObserver(
                android.provider.Settings.System.CONTENT_URI,
                true,
                settingsContentObserver
            )

            val defaultPlaylist = listOf(
                Playlist.favorite,
                Playlist.justPlayed
            )

            // check if default playlist exists or not, if not add playlist
            databaseUtil.getAllPlaylist { playlistList ->
                defaultPlaylist.forEach { playlist ->
                    if (!playlistList.containBy { it.id == playlist.id }) {
                        databaseUtil.insertPlaylist(playlist) {
                        }
                    }
                }
            }

            setContent {
                MusyTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        MosikoApp(
                            datastore = datastore,
                            homeViewModel = homeViewModel,
                            searchViewModel = searchViewModel,
                            playlistViewModel = playlistViewModel,
                            albumViewModel = albumViewModel,
                            artistViewModel = artistViewModel,
                            musicControllerViewModel = musicControllerViewModel
                        )
                    }
                }
            }
        }
        loadInterstitial(this)
    }

    override fun onStart() {
        super.onStart()

        // register SettingsContentObserver, used to observe changes in volume
        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            settingsContentObserver
        )
    }

    override fun onStop() {
        super.onStop()
        contentResolver.unregisterContentObserver(settingsContentObserver)
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unbindService(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        mediaPlayerService = binder.getService()
        mediaPlayerService!!.setMediaPlayerAction(object : MediaPlayerAction {
            override fun resume() {
                musicControllerViewModel.resume()
            }

            override fun pause() {
                musicControllerViewModel.pause()
            }

            override fun next() {
                musicControllerViewModel.next()
            }

            override fun previous() {
                musicControllerViewModel.previous()
            }

            override fun stop() {
                musicControllerViewModel.stop()
            }
        })
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mediaPlayerService = null
    }
}
