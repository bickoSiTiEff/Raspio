package me.bickositieff.raspio.ui.playback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi

class PlaybackViewModel : ViewModel() {
    val playbackRunning = MutableLiveData(false)
    val playbackVolume = MutableLiveData<Int>().apply {
        value = 50
//        viewModelScope.launch { value = PlaybackApi.getPlayback().body()!!.volume!!.roundToInt() }
    }


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

    fun stopPlayback() {
        viewModelScope.launch { PlaybackApi.postPlaybackStop() }
    }

    fun increaseVolume() {
        if (playbackVolume.value != 100) {
            playbackVolume.value = playbackVolume.value!! + 1
//            viewModelScope.launch { PlaybackApi.postPlaybackVolume(playbackVolume.value!!) }
        }
    }

    fun decreaseVolume() {
        if (playbackVolume.value != 0) {
            playbackVolume.value = playbackVolume.value!! - 1
//            viewModelScope.launch { PlaybackApi.postPlaybackVolume(playbackVolume.value!!) }
        }
    }
}
