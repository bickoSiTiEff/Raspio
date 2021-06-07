package me.bickositieff.raspio.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import me.bickositieff.raspio.generated.api.PlaylistApi
import me.bickositieff.raspio.ui.models.Song

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    val playlist = liveData {
        emit(emptyList())
        emit(PlaylistApi.getPlaylist().body()!!.map { Song(it.title!!, it.artist!!, it.duration!!, null) })
    }
}