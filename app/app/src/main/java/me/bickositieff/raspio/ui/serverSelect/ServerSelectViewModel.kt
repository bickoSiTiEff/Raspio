package me.bickositieff.raspio.ui.serverSelect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServerSelectViewModel : ViewModel() {
    val ip = MutableLiveData<String>().apply {
        value = ""
    }
}