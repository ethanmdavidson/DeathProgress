package com.machinerychorus.lifeprogresswallpaper

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference
import com.machinerychorus.lifeprogresswallpaper.customPrefs.ColorPreference
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager


class WallpaperSettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)

		//androidx EditTextPreference doesn't respect the hint attribute in the xml for some reason
		//we set it here to work around that
		val textPref = findPreference<EditTextPreference>(getString(R.string.goalsKey))
		textPref?.setOnBindEditTextListener { editText -> editText.setHint(R.string.goalsHint) }

		val manager = ColorPickerPreferenceManager.getInstance(context)

		// listeners to keep hex and color selector in sync
		val bgPref = findPreference<ColorPreference>(getString(R.string.bgColorKey))
		val bgHexPref = findPreference<EditTextPreference>(getString(R.string.bgColorHexKey))
		bgPref?.getColorPickerView()?.setColorListener(ColorEnvelopeListener { envelope, _ ->
			bgHexPref?.text = "#"+envelope.hexCode
		})
		bgHexPref?.setOnPreferenceChangeListener { _, newValue ->
			manager.clearSavedColor(getString(R.string.bgColorKey))
			manager.clearSavedSelectorPosition(getString(R.string.bgColorKey))
			manager.clearSavedAlphaSliderPosition(getString(R.string.bgColorKey))
			manager.clearSavedBrightnessSlider(getString(R.string.bgColorKey))
			manager.setColor(getString(R.string.bgColorKey), Color.parseColor(newValue.toString()))
			manager.restoreColorPickerData(bgPref?.getColorPickerView())
			preferenceManager
				.sharedPreferences?.edit {
					putInt(getString(R.string.bgColorKey), Color.parseColor(newValue.toString()))
				}
			bgPref?.refresh()
			true
		}

		// listeners to keep hex and color selector in sync
		val fgPref = findPreference<ColorPreference>(getString(R.string.fgColorKey))
		val fgHexPref = findPreference<EditTextPreference>(getString(R.string.fgColorHexKey))
		fgPref?.getColorPickerView()?.setColorListener(ColorEnvelopeListener { envelope, _ ->
			fgHexPref?.text = "#"+envelope.hexCode
		})
		fgHexPref?.setOnPreferenceChangeListener { _, newValue ->
			manager.clearSavedColor(getString(R.string.fgColorKey))
			manager.clearSavedSelectorPosition(getString(R.string.fgColorKey))
			manager.clearSavedAlphaSliderPosition(getString(R.string.fgColorKey))
			manager.clearSavedBrightnessSlider(getString(R.string.fgColorKey))
			manager.setColor(getString(R.string.fgColorKey), Color.parseColor(newValue.toString()))
			manager.restoreColorPickerData(fgPref?.getColorPickerView())
			preferenceManager
				.sharedPreferences?.edit {
					putInt(getString(R.string.fgColorKey), Color.parseColor(newValue.toString()))
				}
			fgPref?.refresh()
			true
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
