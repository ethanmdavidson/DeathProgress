package com.machinerychorus.lifeprogresswallpaper

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference

class WallpaperSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is DateDialogPreference) {
            preference.displayDialog(requireContext())
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}