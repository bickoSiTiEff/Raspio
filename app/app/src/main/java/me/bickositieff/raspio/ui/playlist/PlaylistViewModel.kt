package me.bickositieff.raspio.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi
import me.bickositieff.raspio.generated.api.PlaylistApi
import me.bickositieff.raspio.generated.models.POSTPlaylistBody
import me.bickositieff.raspio.ui.models.Song

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    val playlist = liveData {
        emit(emptyList())
        while (true) {
            emit(
                PlaylistApi.getPlaylist().body()!!
                    .map { Song(it.path!!, it.title!!, it.artist!!, it.duration!!, null) })
            kotlinx.coroutines.delay(5000)
        }
    }

    fun moveUp(songPos: Int, song: Song){
        viewModelScope.launch {
            PlaylistApi.deletePlaylist(songPos)
            PlaylistApi.postPlaylist(POSTPlaylistBody(song.path, songPos - 1))
        }
    }

    fun moveDown(songPos: Int, song: Song){
        viewModelScope.launch {
            PlaylistApi.deletePlaylist(songPos)
            PlaylistApi.postPlaylist(POSTPlaylistBody(song.path, songPos + 1))
        }
    }

    fun setAsNext(songPos: Int, song: Song){
        viewModelScope.launch {
            PlaylistApi.deletePlaylist(songPos)
            PlaylistApi.postPlaylist(POSTPlaylistBody(song.path, PlaybackApi.getPlayback().body()!!.currentlyPlayingIndex!!.toInt() + 1))
        }
    }

    fun remove(songPos: Int){
        viewModelScope.launch {
            PlaylistApi.deletePlaylist(songPos)
        }
    }
}