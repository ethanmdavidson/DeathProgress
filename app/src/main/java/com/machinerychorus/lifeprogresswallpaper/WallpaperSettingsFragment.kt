package com.machinerychorus.lifeprogresswallpaper

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dr1009.app.chronodialogpreference.*

class WallpaperSettingsFragment : PreferenceFragmentCompat() {
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
        if (preference is TimeDialogPreference) {
            val dialogPreference = preference
            dialogFragment = TimePreferenceDialogFragment
                .newInstance(
                    dialogPreference.key,
                    dialogPreference.isForce12HourPicker,
                    dialogPreference.isForce24HourPicker,
                    dialogPreference.customFormat
                )
        } else if (preference is DateDialogPreference) {
            val dialogPreference = preference
            dialogFragment = DatePreferenceDialogFragment
                .newInstance(
                    dialogPreference.key,
                    dialogPreference.minDate,
                    dialogPreference.maxDate,
                    dialogPreference.customFormat
                )
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(getParentFragmentManager(), ChronoPreferenceFragment.DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}