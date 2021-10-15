package com.machinerychorus.lifeprogresswallpaper

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference
import com.machinerychorus.lifeprogresswallpaper.customPrefs.chrono.DatePreferenceDialogFragment

class WallpaperSettingsFragment : PreferenceFragmentCompat() {
    val DIALOG_FRAGMENT_TAG = "ChronoPreferenceFragment.DIALOG"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        val context = preferenceManager.context
//        val screen = preferenceManager.createPreferenceScreen(context)
//
//        val birthDatePref = DateDialogPreference(context)
//        birthDatePref.key = "birth-date"
//        birthDatePref.title = "Birth Date"
//        screen.addPreference(birthDatePref)
//
//        val lifeExpectancyPref = IntegerPreference(context)
//        lifeExpectancyPref.key = "life-expectancy"
//        lifeExpectancyPref.title = "Life Expectancy"
//        screen.addPreference(lifeExpectancyPref)
//
//        val backgroundColorPref = ColorPickerPreference(context)
//        backgroundColorPref.key = "background-color"
//        backgroundColorPref.title = "Background Color"
//        screen.addPreference(backgroundColorPref)
//
//        val foregroundColorPref = ColorPickerPreference(context)
//        foregroundColorPref.key = "foreground-color"
//        foregroundColorPref.title = "Foreground Color"
//        screen.addPreference(foregroundColorPref)
//
//        val progressFontSizePref = IntegerPreference(context)
//        progressFontSizePref.key = "progress-font-size"
//        progressFontSizePref.title = "Progress Font Size"
//        screen.addPreference(progressFontSizePref)
//
//        val decimalPlacesPref = IntegerPreference(context)
//        decimalPlacesPref.key = "decimal-places"
//        decimalPlacesPref.title = "Decimal Places"
//        screen.addPreference(decimalPlacesPref)
//
//        val goalsTextPref = EditTextPreference(context)
//        goalsTextPref.key = "goals-text"
//        goalsTextPref.title = "Goals"
//        screen.addPreference(goalsTextPref)
//
//        val goalsFontSizePref = IntegerPreference(context)
//        goalsFontSizePref.key = "goals-font-size"
//        goalsFontSizePref.title = "Goals Font Size"
//        screen.addPreference(goalsFontSizePref)
//
//        preferenceScreen = screen
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        var dialogFragment: DialogFragment? = null
        if (preference is DateDialogPreference) {
            val dialogPreference = preference
            dialogFragment = DatePreferenceDialogFragment
                .newInstance(
                    dialogPreference.key,
                    dialogPreference.minDate,
                    dialogPreference.maxDate,
                    dialogPreference.customFormat
                ).apply {
                    pickerView?.calendarViewShown = false
                    pickerView?.spinnersShown = true
                }
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}