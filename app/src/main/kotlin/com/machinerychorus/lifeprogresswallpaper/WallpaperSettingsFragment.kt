package com.machinerychorus.lifeprogresswallpaper

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference

class WallpaperSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //androidx EditTextPreference doesn't respect the hint attribute in the xml for some reason
        //we set it here to work around that
        val textPref = findPreference<EditTextPreference>(getString(R.string.goalsKey))
        textPref?.setOnBindEditTextListener { editText -> editText.setHint(R.string.goalsHint) }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is DateDialogPreference) {
            preference.displayDialog(requireContext())
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}
