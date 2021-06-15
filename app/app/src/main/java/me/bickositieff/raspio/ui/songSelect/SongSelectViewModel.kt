package me.bickositieff.raspio.ui.songSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.LibraryApi
import me.bickositieff.raspio.generated.api.PlaylistApi
import me.bickositieff.raspio.generated.models.POSTPlaylistBody
import me.bickositieff.raspio.ui.models.Song

class SongSelectViewModel : ViewModel() {
    val availableSongs = liveData {
        emit(emptyList())
        while (true) {
            emit(LibraryApi.getSongs().body()!!.map { Song(it.path!!, it.title!!, it.artist!!, it.duration!!, null) })
            kotlinx.coroutines.delay(1000)
        }
    }

    fun addAsNext(song: Song) {
        viewModelScope.launch {
            PlaylistApi.postPlaylist(POSTPlaylistBody(song.path, 0))
        }
    }

    fun addToQueue(song: Song) {
        viewModelScope.launch {
            PlaylistApi.postPlaylist(POSTPlaylistBody(song.path))
        }
    }

    fun uploadNewSong() {
        //TODO
        viewModelScope.launch {
        }
    }
}