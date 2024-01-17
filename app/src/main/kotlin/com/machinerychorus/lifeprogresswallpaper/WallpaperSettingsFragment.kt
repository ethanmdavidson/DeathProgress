package com.machinerychorus.lifeprogresswallpaper

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference
import com.machinerychorus.lifeprogresswallpaper.customPrefs.ColorPreference
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager


class WallpaperSettingsFragment : PreferenceFragmentCompat() {

	private fun setColor(prefKey: String, pref: ColorPreference?, newColor: String): Boolean {
		val manager = ColorPickerPreferenceManager.getInstance(context)

		var isvalid = false
		var hexColor = 0
		try {
			hexColor = Color.parseColor(newColor)
			isvalid = true
		} catch (ignored: IllegalArgumentException) {
		}

		if (isvalid) {
			manager.clearSavedColor(prefKey)
			manager.clearSavedSelectorPosition(prefKey)
			manager.clearSavedAlphaSliderPosition(prefKey)
			manager.clearSavedBrightnessSlider(prefKey)
			manager.setColor(prefKey, hexColor)
			manager.restoreColorPickerData(pref?.getColorPickerView())
			preferenceManager
				.sharedPreferences?.edit {
					putInt(prefKey, hexColor)
				}
			pref?.refresh()
		} else {
			val toast = Toast.makeText(
				context,
				"Invalid hex color",
				Toast.LENGTH_SHORT
			)
			toast.show()
		}
		return isvalid
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)

		//androidx EditTextPreference doesn't respect the hint attribute in the xml for some reason
		//we set it here to work around that
		val textPref = findPreference<EditTextPreference>(getString(R.string.goalsKey))
		textPref?.setOnBindEditTextListener { editText -> editText.setHint(R.string.goalsHint) }

		// listeners to keep hex and color selector in sync
		val bgPref = findPreference<ColorPreference>(getString(R.string.bgColorKey))
		val bgHexPref = findPreference<EditTextPreference>(getString(R.string.bgColorHexKey))
		bgPref?.getColorPickerView()?.setColorListener(ColorEnvelopeListener { envelope, _ ->
			bgHexPref?.text = "#"+envelope.hexCode
		})
		bgHexPref?.setOnPreferenceChangeListener { _, newValue ->
			setColor(getString(R.string.bgColorKey),bgPref,newValue.toString())
		}

		// listeners to keep hex and color selector in sync
		val fgPref = findPreference<ColorPreference>(getString(R.string.fgColorKey))
		val fgHexPref = findPreference<EditTextPreference>(getString(R.string.fgColorHexKey))
		fgPref?.getColorPickerView()?.setColorListener(ColorEnvelopeListener { envelope, _ ->
			fgHexPref?.text = "#"+envelope.hexCode
		})
		fgHexPref?.setOnPreferenceChangeListener { _, newValue ->
			setColor(getString(R.string.fgColorKey),fgPref,newValue.toString())
		}
	}

	override fun onDisplayPreferenceDialog(preference: Preference) {
		if (preference is DateDialogPreference) {
			preference.displayDialog(requireContext())
		} else {
			super.onDisplayPreferenceDialog(preference)
		}
	}
}
