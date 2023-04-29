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

		screen.addPreference(DateDialogPreference(context).apply {
			key = getString(R.string.birthdateKey)
			title = "Birth Date"
			setDefaultValue("1994-05-31")
		})

		screen.addPreference(IntegerPreference(context).apply {
			key = getString(R.string.expectancyKey)
			title = "Life Expectancy"
			setDefaultValue("85")
		})

		screen.addPreference(ColorPickerPreference(context).apply {
			key = getString(R.string.bgColorKey)
			title = "Background Color"
			defaultColor = R.color.blackAsMySOUUUUUUUULLLL
			attachAlphaSlideBar = false
			positive = getString(R.string.confirm)
			negative = getString(R.string.cancel)
			onInit()
			getColorPickerView().flagView = BubbleFlag(context)
		})

		screen.addPreference(ColorPickerPreference(context).apply {
			key = getString(R.string.fgColorKey)
			title = "Foreground Color"
			defaultColor = R.color.wholesomeTeal
			attachAlphaSlideBar = false
			positive = getString(R.string.confirm)
			negative = getString(R.string.cancel)
			onInit()
			getColorPickerView().flagView = BubbleFlag(context)
		})

		screen.addPreference(IntegerPreference(context).apply {
			key = getString(R.string.progressFontSizeKey)
			title = "Progress Font Size"
			setDefaultValue("240")
		})

		screen.addPreference(IntegerPreference(context).apply {
			key = getString(R.string.decimalsKey)
			title = "Decimal Places"
			setDefaultValue("4")
		})

		screen.addPreference(EditTextPreference(context).apply {
			key = getString(R.string.goalsKey)
			title = "Goals"
			//Not sure what's the right way to set the hint, but this seems to work
			setOnBindEditTextListener { editText -> editText.setHint(R.string.goalsHint) }
		})

		screen.addPreference(IntegerPreference(context).apply {
			key = getString(R.string.goalsFontSizeKey)
			title = "Goals Font Size"
			setDefaultValue("75")
		})

		screen.addPreference(Preference(context).apply {
			key = getString(R.string.setWallpaperButtonId)
			widgetLayoutResource = R.layout.set_wallpaper_button
		})

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
