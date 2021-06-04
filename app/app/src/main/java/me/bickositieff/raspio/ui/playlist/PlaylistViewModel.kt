package me.bickositieff.raspio.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import me.bickositieff.raspio.models.Song

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    val playlist = MutableLiveData<List<Song>>().apply {
        value = ArrayList()
    }

    fun addSong(song: Song){
        val tmp = playlist.value!! as ArrayList<Song>
        tmp.add(song)
        playlist.value = tmp
    }

    fun removeSong(index: Int){
        val tmp = playlist.value!! as ArrayList<Song>
        tmp.removeAt(index)
        playlist.value = tmp
    }

}