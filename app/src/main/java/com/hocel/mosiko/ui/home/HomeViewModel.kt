package com.hocel.mosiko.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.hocel.mosiko.MosikoApplication
import com.hocel.mosiko.data.MosikoRepositoryImpl
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist
import com.hocel.mosiko.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: MosikoApplication,
    private val repository: MosikoRepositoryImpl
) : ViewModel() {

    //    MutableLiveData(emptyList<Music>())
    private val _musicList: MutableStateFlow<MutableList<Music>> = MutableStateFlow(mutableListOf())
    val musicList: StateFlow<MutableList<Music>> = _musicList

    private val _albumList = MutableLiveData(emptyMap<String, List<Music>>())
    val albumList: LiveData<Map<String, List<Music>>> = _albumList

    private val _artistList = MutableLiveData(emptyMap<String, List<Music>>())
    val artistList: LiveData<Map<String, List<Music>>> = _artistList

    private val _playlist = MutableLiveData(emptyList<Playlist>())
    val playlist: LiveData<List<Playlist>> = _playlist

    private val collator: Collator =
        Collator.getInstance(application.resources.configuration.locales[0]).apply {
            strength = Collator.PRIMARY
        }

    val refreshSongsList = mutableStateOf(false)

    fun getAllMusic(
        sortOption: String = AppUtils.PreferencesValue.SORT_MUSIC_BY_NAME
    ) = viewModelScope.launch {
        repository.getAllMusic { list ->

            when (sortOption) {
                AppUtils.PreferencesValue.SORT_MUSIC_BY_NAME -> {
                    Collections.sort(list, Comparator { o1, o2 ->
                        return@Comparator collator.compare(o1.title, o2.title)
                    })
                }
                AppUtils.PreferencesValue.SORT_MUSIC_BY_ARTIST_NAME -> {
                    Collections.sort(list, Comparator { o1, o2 ->
                        return@Comparator collator.compare(o1.artist, o2.artist)
                    })
                }
                AppUtils.PreferencesValue.SORT_MUSIC_BY_DATE_ADDED -> {
                    Collections.sort(list, Comparator { o1, o2 ->
                        return@Comparator collator.compare(
                            o1.dateAdded.toString(),
                            o2.dateAdded.toString()
                        )
                    })
                }
                else -> Collections.sort(list, Comparator { o1, o2 ->
                    return@Comparator collator.compare(o1.title, o2.title)
                })
            }
            _musicList.value = list
        }
    }

    fun getAllAlbum() {
        repository.getAllMusic { mMusicList ->
            _albumList.value = mMusicList.groupBy { it.album }.toSortedMap(
                Comparator { o1, o2 ->
                    return@Comparator collator.compare(o1, o2)
                }
            )
        }
    }

    fun getAllArtist() {
        repository.getAllMusic { mMusicList ->
            _artistList.value = mMusicList.groupBy { it.artist }.toSortedMap(
                Comparator { o1, o2 ->
                    return@Comparator collator.compare(o1, o2)
                }
            )
        }
    }

    fun getAllPlaylist() {
        repository.getAllPlaylist { playlist ->
            val defaultPlaylist = playlist.filter { it.isDefault }
            val filteredPlaylist = playlist.filter { !it.isDefault }

            Collections.sort(filteredPlaylist, Comparator { o1, o2 ->
                return@Comparator collator.compare(o1.name, o2.name)
            })

            _playlist.value = filteredPlaylist.toMutableList().apply {
                defaultPlaylist.forEachIndexed { i, playlist ->
                    add(i, playlist)
                }
            }
        }
    }

    fun deleteMusicFromList(music: Music) {
        _musicList.value!!.apply {
            remove(music)
        }
    }
}