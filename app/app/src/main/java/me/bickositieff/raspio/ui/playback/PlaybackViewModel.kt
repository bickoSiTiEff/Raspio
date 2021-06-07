package me.bickositieff.raspio.ui.playback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaybackViewModel : ViewModel() {
    val playbackRunning = MutableLiveData(false)

    fun playPause() {
        if (playbackRunning.value!!) {
            //pause playback API call
            playbackRunning.value = false
            println("Playback off")
        } else {
            //start/resume playback API call
            playbackRunning.value = true
            println("Playback on")
        }
    }

    fun skipSong() {
        //API call
    }

    fun stopPlayback() {
        //API call
    }
}