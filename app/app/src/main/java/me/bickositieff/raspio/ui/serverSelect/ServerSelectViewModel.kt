package me.bickositieff.raspio.ui.serverSelect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.preference.PreferenceManager

class ServerSelectViewModel(application: Application) : AndroidViewModel(application) {
    val ip = MutableLiveData<String>().apply {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        value = sharedPrefs.getString("ip", "")
    }
    val valid: LiveData<Boolean> = ip.map { ip ->
        //check IP-syntax
        val regex = Regex("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$")
        ip.matches(regex)
    }

    fun confirmIP() {
        //check connectivity
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication())
        with(sharedPrefs.edit()) {
            putString("ip", ip.value!!)
            apply()
        }
    }
}