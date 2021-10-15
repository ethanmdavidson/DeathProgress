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

import android.os.Bundle
import android.widget.DatePicker
import com.machinerychorus.lifeprogresswallpaper.customPrefs.DateDialogPreference
import java.lang.AssertionError
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.util.*

class DatePreferenceDialogFragment : ChronoPreferenceDialogFragmentCompat() {
    override var pickerView: DatePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val minDate = requireArguments().getString(ARG_MIN_DATE)
        val maxDate = requireArguments().getString(ARG_MAX_DATE)
        val text: String = if (savedInstanceState == null) {
            dateDialogPreference.serializedValue
        } else {
            savedInstanceState.getString(SAVE_STATE_DATE, "1994-05-31")
        }
        pickerView = DatePicker(activity)
        val calendar: Calendar
        calendar = try {
            ChronoUtil.dateToCalendar(ChronoUtil.DATE_FORMATTER.parse(text))
        } catch (e: ParseException) {
            throw AssertionError("Date format is always known and parsable", e)
        }
        pickerView!!.updateDate(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        if (minDate != null) {
            val date: Date
            date = try {
                ChronoUtil.DATE_FORMATTER.parse(minDate)!!
            } catch (e: ParseException) {
                throw IllegalArgumentException("minDate is not in the correct format", e)
            }
            pickerView!!.minDate = date.time
        }
        if (maxDate != null) {
            val date: Date
            date = try {
                ChronoUtil.DATE_FORMATTER.parse(maxDate)!!
            } catch (e: ParseException) {
                throw IllegalArgumentException("maxDate is not in the correct format", e)
            }
            pickerView!!.maxDate = date.time
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            SAVE_STATE_DATE,
            ChronoUtil.DATE_FORMATTER.format(
                calendarFromDatePicker.time
            )
        )
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val value = ChronoUtil.DATE_FORMATTER.format(
                calendarFromDatePicker.time
            )
            if (dateDialogPreference.callChangeListener(value)) {
                dateDialogPreference.serializedValue = value
            }
        }
    }

    private val dateDialogPreference: DateDialogPreference
        get() = preference as DateDialogPreference
    private val calendarFromDatePicker: Calendar
        get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = pickerView!!.year
            calendar[Calendar.MONTH] = pickerView!!.month
            calendar[Calendar.DAY_OF_MONTH] = pickerView!!.dayOfMonth
            return calendar
        }

    companion object {
        private const val ARG_MIN_DATE = "min_date"
        private const val ARG_MAX_DATE = "max_date"
        private const val ARG_CUSTOM_FORMAT = "custom_format"
        private const val SAVE_STATE_DATE = "save_state_time"
        fun newInstance(
            key: String,
            minDate: String?,
            maxDate: String?,
            customFormat: String?
        ): DatePreferenceDialogFragment {
            val fragment = DatePreferenceDialogFragment()
            val b = Bundle(4)
            b.putString(ARG_KEY, key)
            b.putString(ARG_MIN_DATE, minDate)
            b.putString(ARG_MAX_DATE, maxDate)
            b.putString(ARG_CUSTOM_FORMAT, customFormat)
            fragment.arguments = b
            return fragment
        }
    }
}