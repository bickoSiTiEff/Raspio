package me.bickositieff.raspio.ui.songSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.bickositieff.raspio.generated.api.LibraryApi
import me.bickositieff.raspio.generated.api.PlaylistApi
import me.bickositieff.raspio.ui.models.Song

class SongSelectViewModel : ViewModel() {
    val availableSongs = liveData {
        emit(emptyList())
        emit(LibraryApi.getSongs().body()!!.map{Song(it.title!!, it.artist!!, it.duration!!, null)})
    }
}