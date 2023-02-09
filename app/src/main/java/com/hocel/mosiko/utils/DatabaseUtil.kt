package com.hocel.mosiko.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hocel.mosiko.database.MosikoDatabase
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DatabaseUtil(context: Context) {

    private val musicDao = MosikoDatabase.getInstance(context).musicDAO()
    private val playlistDao = MosikoDatabase.getInstance(context).playlistDAO()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private fun postAction(action: () -> Unit) = Handler(Looper.getMainLooper()).post { action() }

    fun getAllMusic(action: (MutableList<Music>) -> Unit) {
        val musicList = ArrayList<Music>()
        scope.launch {
            musicList.addAll(musicDao.getAllMusic())
        }.invokeOnCompletion { postAction { action(musicList) } }
    }

    fun getMusic(audioID: Long, action: (Music) -> Unit) {
        var music: Music? = null
        scope.launch {
            music = musicDao.getMusic(audioID)
        }.invokeOnCompletion { postAction { action(music ?: Music.unknown) } }
    }

    fun updateMusic(music: Music, action: () -> Unit) {
        scope.launch {
            musicDao.update(music)
        }.invokeOnCompletion { postAction(action) }
    }

    fun deleteAllMusic(action: () -> Unit) {
        scope.launch {
            musicDao.deleteAllMusic()
        }.invokeOnCompletion { postAction(action) }
    }

    fun deleteMusic(music: Music, action: () -> Unit) {
        scope.launch {
            musicDao.deleteMusic(music)
        }.invokeOnCompletion { postAction(action) }
    }

    fun insertMusic(musicList: List<Music>, action: () -> Unit) {
        scope.launch {
            musicDao.insertMusic(musicList)
        }.invokeOnCompletion { postAction(action) }
    }

    fun insertMusic(music: Music, action: () -> Unit) {
        scope.launch {
            musicDao.insertMusic(music)
        }.invokeOnCompletion { postAction(action) }
    }





    fun getAllPlaylist(action: (List<Playlist>) -> Unit) {
        val playlist = ArrayList<Playlist>()
        scope.launch {
            playlist.addAll(playlistDao.getAllPlaylist())
        }.invokeOnCompletion { postAction { action(playlist) } }
    }

    fun getPlaylist(playlistID: Int, action: (Playlist) -> Unit) {
        var playlist = Playlist.unknown
        scope.launch {
            playlist = playlistDao.getPlaylist(playlistID)
        }.invokeOnCompletion { postAction { action(playlist) } }
    }

    fun updatePlaylist(playlist: Playlist, action: () -> Unit) {
        scope.launch {
            playlistDao.update(playlist)
        }.invokeOnCompletion { postAction(action) }
    }

    fun deletePlaylist(playlist: Playlist, action: () -> Unit) {
        scope.launch {
            playlistDao.delete(playlist)
        }.invokeOnCompletion { postAction(action) }
    }

    fun insertPlaylist(playlist: Playlist, action: () -> Unit) {
        scope.launch {
            playlistDao.insert(playlist)
        }.invokeOnCompletion { postAction(action) }
    }

    fun insertPlaylist(playlist: List<Playlist>, action: () -> Unit) {
        scope.launch {
            playlistDao.insert(playlist)
        }.invokeOnCompletion { postAction(action) }
    }





    companion object {
        private var INSTANCE: DatabaseUtil? = null

        fun getInstance(base: Context): DatabaseUtil {
            if (INSTANCE == null) {
                synchronized(DatabaseUtil::class) {
                    INSTANCE = DatabaseUtil(base)
                }
            }

            return INSTANCE!!
        }
    }

}