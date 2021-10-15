package com.machinerychorus.lifeprogresswallpaper.customPrefs.chrono

/** Copied from https://github.com/koji-1009/ChronoDialogPreference
MIT License

Copyright (c) 2018-2019 Koji Wakamiya, Serhii Yolkin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceDialogFragmentCompat
import com.machinerychorus.lifeprogresswallpaper.R

abstract class ChronoPreferenceDialogFragmentCompat :
    PreferenceDialogFragmentCompat() {
    override fun onCreateDialogView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(R.layout.chrono_preference, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context: Context? = activity
        val linearLayout = onCreateDialogView(context!!) as LinearLayout
        linearLayout.addView(pickerView)
        val builder = AlertDialog.Builder(
            context
        )
        val preference = preference
        builder.setTitle(preference.dialogTitle)
            .setPositiveButton(preference.positiveButtonText, this)
            .setNegativeButton(preference.negativeButtonText, this)
            .setView(linearLayout)
        return builder.create()
    }

    abstract val pickerView: View?
}