package me.bickositieff.raspio.ui.serverControls

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServerControlsViewModel : ViewModel() {

    val frequency = MutableLiveData("88")

    val transmissionOn = MutableLiveData(false)

    fun changeTransmissionState(on: Boolean) {
        transmissionOn.value = on
    }

}