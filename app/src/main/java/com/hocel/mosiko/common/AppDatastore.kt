package com.hocel.mosiko.common

import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppDatastore(context: Context): ContextWrapper(context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_datastore")

    private val sortMusicOption = stringPreferencesKey(AppUtils.PreferencesKey.SORT_MUSIC_OPTION)
    private val lastMusicPlayed = longPreferencesKey(AppUtils.PreferencesKey.LAST_MUSIC_PLAYED)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private fun postAction(action: () -> Unit) = Handler(Looper.getMainLooper()).post { action() }

    fun setSortMusicOption(option: String, action: () -> Unit) {
        scope.launch {
            datastore.edit { preferences ->
                preferences[sortMusicOption] = option
            }
        }.invokeOnCompletion { postAction(action) }
    }

    fun setLastMusicPlayed(audioID: Long, action: () -> Unit) {
        scope.launch {
            datastore.edit { preferences ->
                preferences[lastMusicPlayed] = audioID
            }
        }.invokeOnCompletion { postAction(action) }
    }


    val getSortMusicOption: Flow<String> = datastore.data.map { preferences ->
        preferences[sortMusicOption] ?: AppUtils.PreferencesValue.SORT_MUSIC_BY_NAME
    }

    val getLastMusicPlayed: Flow<Long> = datastore.data.map { preferences ->
        preferences[lastMusicPlayed] ?: Music.unknown.audioID
    }

    companion object {
        private var INSTANCE: AppDatastore? = null

        fun getInstance(base: Context): AppDatastore {
            if (INSTANCE == null) {
                synchronized(AppDatastore::class.java) {
                    INSTANCE = AppDatastore(base)
                }
            }

            return INSTANCE!!
        }
    }

}