package com.machinerychorus.lifeprogresswallpaper.customPrefs

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.EditTextPreference

/**
 * A custom preference object. Works the same as a normal EditTextPreference, but
 * only allows integer input.
 */
class IntegerPreference(context: Context?, attrs: AttributeSet?) : EditTextPreference(context, attrs) {
    override fun setText(text: String) {
        var isValid = false
        try {
            if (text.toInt() >= 0) {
                isValid = true
            }
        } catch (ignored: NumberFormatException) {
        }
        if (isValid) {
            super.setText(text)
        } else {
            //alert user that it wasn't changed
            val toast = Toast.makeText(
                context,
                "Only positive numbers are allowed in this field",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
    }
}