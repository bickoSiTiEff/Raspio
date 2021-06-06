package me.bickositieff.raspio.ui.serverControls

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.TransmissionApi

class ServerControlsViewModel(application: Application) : AndroidViewModel(application) {

    val frequency = MutableLiveData<String>().apply {
        value = "88"
    }

    val transmissionOn = MutableLiveData(false)

    fun changeTransmissionState(on: Boolean) {
        if(transmissionOn.value == on) return
        val frequencyNum: Int
        try {
            frequencyNum = frequency.value?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            Toast.makeText(getApplication(), "Unusable frequency. Please input a positive number", Toast.LENGTH_LONG).show()
            return
        }
        viewModelScope.launch {
            if (on) TransmissionApi.postTransmissionStart(frequencyNum) else TransmissionApi.postTransmissionStop()
        }
        transmissionOn.value = on
    }
}