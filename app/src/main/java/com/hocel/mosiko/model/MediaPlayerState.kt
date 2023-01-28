package com.hocel.mosiko.model

import java.io.Serializable

data class MediaPlayerState(
    var title: String,
    var album: String,
    var artist: String,
    var duration: Long,
    var currentPosition: Long,
    var albumArtPath: String,
    var isMusicPlayed: Boolean,
): Serializable {
    companion object {
        const val ACTION_PLAY = "com.hocel.mosiko:media:play"
        const val ACTION_PAUSE = "com.hocel.mosiko:media:pause"
        const val ACTION_PREVIOUS = "com.hocel.mosiko:media:previous"
        const val ACTION_NEXT = "com.hocel.mosiko:media:next"
    }
}