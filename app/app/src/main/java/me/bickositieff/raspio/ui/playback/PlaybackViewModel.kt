package me.bickositieff.raspio.ui.playback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi
import me.bickositieff.raspio.generated.api.TransmissionApi

class PlaybackViewModel : ViewModel() {
    val playbackRunning = MutableLiveData(false)

    fun playPause() {
        if (playbackRunning.value!!) {
            //Pause API call when implemented
            playbackRunning.value = false
            println("Playback off")
        } else {
            viewModelScope.launch { PlaybackApi.postPlaybackPlay() }
            playbackRunning.value = true
            println("Playback on")
        }
    }

    fun skipSong() {
        viewModelScope.launch { PlaybackApi.postPlaybackNext() }
    }

    fun stopPlayback() {
        viewModelScope.launch { PlaybackApi.postPlaybackStop() }
    }
}