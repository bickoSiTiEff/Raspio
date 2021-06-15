package me.bickositieff.raspio.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import me.bickositieff.raspio.R


class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val button: Preference? = findPreference(getString(R.string.remove_button))
        button?.setOnPreferenceClickListener {
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            with(sharedPrefs.edit()) {
                remove("ip")
                apply()
            }
            true
        }
    }
}