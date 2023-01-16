package com.machinerychorus.lifeprogresswallpaper.customPrefs

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

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.text.format.DateUtils
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.machinerychorus.lifeprogresswallpaper.R
import com.machinerychorus.lifeprogresswallpaper.LifeWallpaper.Companion.DATE_FORMATTER
import com.machinerychorus.lifeprogresswallpaper.LifeWallpaper.Companion.DEFAULT_DATE
import com.machinerychorus.lifeprogresswallpaper.customPrefs.chrono.SavedState
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDialogPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) :
    DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private val maxDate: String?
    private val minDate: String?
    private val customFormat: String?
    private val mCustomSimpleDateFormat: SimpleDateFormat?
    private var date: Calendar = Calendar.getInstance()
    private var serializedValue: String
        get() = DATE_FORMATTER.format(date.time)
        set(serializedDate) {
            try {
                DATE_FORMATTER.parse(serializedDate)?.toCalendar()?.apply {date = this}
            } catch (e: ParseException) {
                throw IllegalArgumentException("Date format is not parsable", e)
            }
            val wasBlocking = shouldDisableDependents()
            persistString(serializedDate)
            val isBlocking = shouldDisableDependents()
            if (isBlocking != wasBlocking) {
                notifyDependencyChange(isBlocking)
            }
            summary = summary
        }

    private fun Date.toCalendar() : Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar
    }

    override fun getSummary(): CharSequence {
        mCustomSimpleDateFormat?.apply {
            return mCustomSimpleDateFormat.format(date.time)
        }
        return DateUtils.formatDateTime(
            context,
            date.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE
        )
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getString(index)!!
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        serializedValue = getPersistedString((defaultValue as String?) ?: DEFAULT_DATE)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent && superState != null) {
            // No need to save instance state since it's persistent
            return superState
        }
        val myState = SavedState(superState)
        myState.text = serializedValue
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state.javaClass != SavedState::class.java) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state)
            return
        }
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)
        serializedValue = myState.text ?: ""
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.Dialog_Preference_DatePicker, defStyleAttr, defStyleRes
        )
        minDate = a.getString(R.styleable.Dialog_Preference_DatePicker_minDate)
        maxDate = a.getString(R.styleable.Dialog_Preference_DatePicker_maxDate)
        customFormat = a.getString(R.styleable.Dialog_Preference_DatePicker_customFormat)
        mCustomSimpleDateFormat = if (customFormat != null && customFormat.isNotEmpty()) {
            SimpleDateFormat(customFormat, Locale.getDefault())
        } else {
            null
        }
        a.recycle()
    }

    fun displayDialog(context: Context){
        DatePickerDialog(context, R.style.SpinnerDatePickerDialog,
            { _, year, month, day ->
                val cal = Calendar.getInstance().apply {
                    this[Calendar.YEAR] = year
                    this[Calendar.MONTH] = month
                    this[Calendar.DAY_OF_MONTH] = day
                }
                serializedValue = DATE_FORMATTER.format(cal.time)
            },
            date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH]).apply {
            if (minDate != null) {
                val date: Date = try {
                    DATE_FORMATTER.parse(minDate)!!
                } catch (e: ParseException) {
                    throw IllegalArgumentException("minDate is not in the correct format", e)
                }
                datePicker.minDate = date.time
            }
            if (maxDate != null) {
                val date: Date = try {
                    DATE_FORMATTER.parse(maxDate)!!
                } catch (e: ParseException) {
                    throw IllegalArgumentException("maxDate is not in the correct format", e)
                }
                datePicker.maxDate = date.time
            }
        }.show()
    }
}
