package com.hocel.mosiko

import android.app.Application
import com.hocel.mosiko.utils.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MosikoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannel(this)
    }
}