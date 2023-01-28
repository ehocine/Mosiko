package com.hocel.mosiko.ui.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hocel.mosiko.data.MusyRepository
import com.hocel.mosiko.model.Music
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: MusyRepository
): ViewModel() {

    private val _filteredMusicList = MutableLiveData(emptyList<Music>())
    val filteredMusicList: LiveData<List<Music>> = _filteredMusicList

    fun filterMusic(artist: String) {
        repository.getAllMusic { musicList ->
            _filteredMusicList.value = musicList.filter { it.artist == artist }
        }
    }
}