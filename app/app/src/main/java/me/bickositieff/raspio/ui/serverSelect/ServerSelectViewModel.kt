package me.bickositieff.raspio.ui.serverSelect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class ServerSelectViewModel : ViewModel() {
    val ip = MutableLiveData<String>().apply {
        value = ""
    }
    val valid: LiveData<Boolean> = ip.map { ip ->
        //check IP-syntax
        //check connectivity
        true
    }

    fun confirmIP(){
        //save as shared Pref
    }
}