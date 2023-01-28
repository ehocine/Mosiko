package com.hocel.mosiko.data

import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist

interface MusyRepositoryImpl {

    fun getAllMusic(action: (List<Music>) -> Unit)

    fun getMusic(audioID: Long, action: (Music) -> Unit)

    fun updateMusic(music: Music, action: () -> Unit)

    fun deleteAllMusic(action: () -> Unit)

    fun deleteMusic(music: Music, action: () -> Unit)

    fun insertMusic(musicList: List<Music>, action: () -> Unit)

    fun insertMusic(music: Music, action: () -> Unit)



    fun getAllPlaylist(action: (List<Playlist>) -> Unit)

    fun getPlaylist(playlistID: Int, action: (Playlist) -> Unit)

    fun updatePlaylist(playlist: Playlist, action: () -> Unit)

    fun deletePlaylist(playlist: Playlist, action: () -> Unit)

    fun insertPlaylist(playlist: Playlist, action: () -> Unit)

    fun insertPlaylist(playlist: List<Playlist>, action: () -> Unit)

}