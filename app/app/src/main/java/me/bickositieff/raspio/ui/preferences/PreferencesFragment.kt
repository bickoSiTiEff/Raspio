package me.bickositieff.raspio.ui.preferences

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import me.bickositieff.raspio.R


class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val button: Preference? = findPreference("remove_button")
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        button?.setOnPreferenceClickListener {
            with(sharedPrefs.edit()) {
                remove("ip")
                apply()
            }
            requireActivity().finish()
            true
        }
        button?.title = button?.title.toString() + " ${sharedPrefs.getString("ip", "")}"
    }
}