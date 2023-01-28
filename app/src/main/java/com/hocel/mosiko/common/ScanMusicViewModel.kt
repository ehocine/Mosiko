package com.hocel.mosiko.common

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hocel.mosiko.data.MusyRepositoryImpl
import com.hocel.mosiko.utils.MusicUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanMusicViewModel @Inject constructor(
    private val repository: MusyRepositoryImpl
) : ViewModel() {

    private var _scannedMusicInPercent = MutableLiveData(0)
    val scannedMusicInPercent: LiveData<Int> = _scannedMusicInPercent

    val songsScanState = mutableStateOf(false)

    fun scanLocalSong(context: Context, onComplete: () -> Unit) {
        songsScanState.value = true
        val totalMusic = MusicUtil.getMusicCount(context)
        val musicList = MusicUtil.getMusic(
            context = context,
            scannedMusicCount = { scannedMusicCount ->
                // ((scanned / total) * 100%) * 100
                val percent = (((scannedMusicCount / totalMusic) * (100 / 100)) * 100)
                _scannedMusicInPercent.value = percent
            },
        )

        repository.deleteAllMusic {
            repository.insertMusic(musicList) {
                songsScanState.value = false
                onComplete()
            }
        }
    }
}
