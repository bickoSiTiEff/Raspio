package me.bickositieff.raspio.ui.playback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi

class PlaybackViewModel : ViewModel() {
    val playbackRunning = MutableLiveData(false)


    fun playPause() {
        if (playbackRunning.value!!) {
            viewModelScope.launch { PlaybackApi.postPlaybackPause() }
            playbackRunning.value = false
        } else {
            viewModelScope.launch { PlaybackApi.postPlaybackPlay() }
            playbackRunning.value = true
        }
    }

    fun skipSong() {
        viewModelScope.launch { PlaybackApi.postPlaybackNext() }
    }

    fun previousSong() {
        viewModelScope.launch { PlaybackApi.postPlaybackPrevious() }
    }
}
