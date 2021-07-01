package me.bickositieff.raspio.ui.playback

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi
import me.bickositieff.raspio.generated.models.GETPlaybackResponse
import me.bickositieff.raspio.generated.models.GETPlaybackResponseState

class PlaybackViewModel : ViewModel() {
    val playbackState = liveData {
        emit(
            GETPlaybackResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                GETPlaybackResponseState.STOP,
                null,
                null
            )
        )
        while(true) {
            emit(PlaybackApi.getPlayback().body()!!)
            delay(500)
        }
    }

    val playbackRunning = playbackState.map { it.state }


    fun playPause() {
        if (playbackRunning.value == GETPlaybackResponseState.PLAY) {
            viewModelScope.launch { PlaybackApi.postPlaybackPause() }
        } else {
            viewModelScope.launch { PlaybackApi.postPlaybackPlay() }
        }
    }

    fun skipSong() {
        viewModelScope.launch { PlaybackApi.postPlaybackNext() }
    }

    fun previousSong() {
        viewModelScope.launch { PlaybackApi.postPlaybackPrevious() }
    }
}
