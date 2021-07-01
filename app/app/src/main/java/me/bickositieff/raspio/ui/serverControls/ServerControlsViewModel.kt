package me.bickositieff.raspio.ui.serverControls

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.TransmissionApi

class ServerControlsViewModel(application: Application) : AndroidViewModel(application) {

    val frequency = MutableLiveData<String>().apply {
        value = "88" //range: 76 - 108
    }

    val transmissionOn = MutableLiveData(false)

    fun changeTransmissionState(on: Boolean, frequencyNum: Int) {
        viewModelScope.launch {
            if (on) TransmissionApi.postTransmissionStart(frequencyNum) else TransmissionApi.postTransmissionStop()
        }
    }
}
