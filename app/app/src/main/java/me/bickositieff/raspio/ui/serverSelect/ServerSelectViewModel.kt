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
        //check connectivity
        true
    }

    fun confirmIP(){
        //save as shared Pref
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        with(sharedPrefs.edit()){
            putString("ip", ip.value!!)
            apply()
        }
    }
}