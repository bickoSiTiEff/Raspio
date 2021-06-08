package me.bickositieff.raspio.ui.serverSelect

import android.app.Application
import androidx.lifecycle.*
import androidx.preference.PreferenceManager

class ServerSelectViewModel(application: Application) : AndroidViewModel(application) {
    val ip = MutableLiveData<String>().apply {
        value = ""
    }
    val valid: LiveData<Boolean> = ip.map { ip ->
        //check IP-syntax
        true
    }

    fun confirmIP(){
        //check connectivity

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        with(sharedPrefs.edit()){
            putString("ip", ip.value!!)
            apply()
        }
    }
}