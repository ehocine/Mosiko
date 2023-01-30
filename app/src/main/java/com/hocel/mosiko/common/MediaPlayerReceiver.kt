package com.hocel.mosiko.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hocel.mosiko.model.MediaPlayerState

const val MediaPlayerReceiver_Tag = "MediaPlayerReceiver"

class MediaPlayerReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MediaPlayerService::class.java)

        when (
            requireNotNull(
                intent.action,
                lazyMessage = { "$MediaPlayerReceiver_Tag:null action" }
            )
        ) {
            MediaPlayerState.ACTION_PLAY -> {
                context.startForegroundService(
                    serviceIntent.setAction(MediaPlayerState.ACTION_PLAY)
                )
            }
            MediaPlayerState.ACTION_PAUSE -> {
                context.startForegroundService(
                    serviceIntent.setAction(MediaPlayerState.ACTION_PAUSE)
                )
            }
            MediaPlayerState.ACTION_NEXT -> {
                context.startForegroundService(
                    serviceIntent.setAction(MediaPlayerState.ACTION_NEXT)
                )
            }
            MediaPlayerState.ACTION_PREVIOUS -> {
                context.startForegroundService(
                    serviceIntent.setAction(MediaPlayerState.ACTION_PREVIOUS)
                )
            }
        }
    }

}