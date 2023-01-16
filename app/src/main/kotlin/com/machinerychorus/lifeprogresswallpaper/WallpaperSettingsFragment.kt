package com.machinerychorus.lifeprogresswallpaper

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference
import com.machinerychorus.lifeprogresswallpaper.customPrefs.IntegerPreference
import com.skydoves.colorpickerpreference.ColorPickerPreference
import com.skydoves.colorpickerview.flag.BubbleFlag

class WallpaperSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val birthDatePref = DateDialogPreference(context).apply {
            key = getString(R.string.birthdateKey)
            title = "Birth Date"
            setDefaultValue("1994-05-31")
        }
        screen.addPreference(birthDatePref)

        val expectancyPref = IntegerPreference(context).apply {
            key = getString(R.string.expectancyKey)
            title = "Life Expectancy"
            setDefaultValue("85")
        }
        screen.addPreference(expectancyPref)

        val bgColorPref = ColorPickerPreference(context).apply {
            key = getString(R.string.bgColorKey)
            title = "Background Color"
            defaultColor = R.color.blackAsMySOUUUUUUUULLLL
            attachAlphaSlideBar = false
            positive = getString(R.string.confirm)
            negative = getString(R.string.cancel)
            onInit()
            getColorPickerView().flagView = BubbleFlag(context)
        }
        screen.addPreference(bgColorPref)

        //androidx EditTextPreference doesn't respect the hint attribute in the xml for some reason
        //we set it here to work around that
        val textPref = findPreference<EditTextPreference>(getString(R.string.goalsKey))
        textPref?.setOnBindEditTextListener { editText -> editText.setHint(R.string.goalsHint) }

        preferenceScreen = screen
    }

	override fun onDisplayPreferenceDialog(preference: Preference) {
		if (preference is DateDialogPreference) {
			preference.displayDialog(requireContext())
		} else {
			super.onDisplayPreferenceDialog(preference)
		}
	}
}
