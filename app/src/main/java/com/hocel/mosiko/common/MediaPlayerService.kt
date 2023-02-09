package com.hocel.mosiko.common

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaMetadata
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat
import com.hocel.mosiko.model.MediaPlayerState
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.utils.AlarmUtil
import com.hocel.mosiko.utils.NotificationUtil


class MediaPlayerService : Service() {

    private var mediaPLayerAction: MediaPlayerAction? = null

    private val mBinder: IBinder = MediaPlayerServiceBinder()
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaStyle: NotificationCompat.MediaStyle
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var audioManager: AudioManager

    private var isForegroundService = false

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        notificationManager = NotificationManagerCompat.from(this)

        mediaSession = MediaSessionCompat(this, "MediaPlayerSessionService")
        mediaStyle = NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {

                if (Intent.ACTION_MEDIA_BUTTON == mediaButtonIntent.action) {
                    val event =
                        mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    when (event!!.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PLAY -> mediaPLayerAction?.resume()
                        KeyEvent.KEYCODE_MEDIA_PAUSE -> mediaPLayerAction?.pause()
                        KeyEvent.KEYCODE_MEDIA_NEXT -> mediaPLayerAction?.next()
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> mediaPLayerAction?.previous()
                    }
                }
                return true
            }
        })

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, Music.unknown.title)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, Music.unknown.album)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, Music.unknown.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, Music.unknown.albumPath)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, Music.unknown.duration)
                .build()
        )

        startForeground(123, NotificationUtil.foregroundNotification(this)).also {
            isForegroundService = true
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            MediaPlayerState.ACTION_PLAY -> mediaPLayerAction?.resume()
            MediaPlayerState.ACTION_PAUSE -> mediaPLayerAction?.pause()
            MediaPlayerState.ACTION_NEXT -> mediaPLayerAction?.next()
            MediaPlayerState.ACTION_PREVIOUS -> mediaPLayerAction?.previous()
        }

        intent.getSerializableExtra("mediaPLayerState")?.let { newState ->
            newState as MediaPlayerState

            if (isForegroundService and (newState.duration != 0L)) {
                mediaSession.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            if (newState.isMusicPlayed) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                            newState.currentPosition,
                            1f
                        )
                        .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .build()
                )
                mediaSession.setMetadata(
                    MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, newState.title)
                        .putString(MediaMetadata.METADATA_KEY_ALBUM, newState.album)
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, newState.artist)
                        .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, newState.albumArtPath)
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, newState.duration)
                        .build()
                )

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return 0
                }
                notificationManager.notify(
                    0,
                    NotificationUtil.notificationMediaPlayer(
                        applicationContext,
                        NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSession.sessionToken),
                        newState
                    )
                )
            }
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaSession.isActive = false
        mediaPLayerAction?.stop()
        mediaSession.release()
        notificationManager.cancelAll()
        AlarmUtil.cancelTimer(this)
        stopForeground(STOP_FOREGROUND_DETACH).also {
            isForegroundService = false
        }
        super.onTaskRemoved(rootIntent)
    }

    fun setMediaPlayerAction(playerAction: MediaPlayerAction) {
        this.mediaPLayerAction = playerAction
    }

    inner class MediaPlayerServiceBinder : Binder() {

        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }
}