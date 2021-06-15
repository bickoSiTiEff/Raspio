package me.bickositieff.raspio.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import me.bickositieff.raspio.generated.api.LibraryApi
import me.bickositieff.raspio.ui.models.Song

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    val playlist = liveData {
        emit(emptyList())
        while (true) {
            emit(emptyList())
            emit(LibraryApi.getSongs().body()!!.map { Song(it.path!!, it.title!!, it.artist!!, it.duration!!, null) })
            kotlinx.coroutines.delay(1000)
        }
    }
}