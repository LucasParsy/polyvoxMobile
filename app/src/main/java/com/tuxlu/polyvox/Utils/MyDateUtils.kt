package com.tuxlu.polyvox.Utils

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.tuxlu.polyvox.R
import java.text.SimpleDateFormat
import java.util.*

import java.util.Calendar.DATE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

/**
 * Created by tuxlu on 25/11/17.
 */

object MyDateUtils {

    @JvmStatic
    fun setSpinnersToDate(dateStr: String, format: String, root: View) {
        val ft = SimpleDateFormat(format)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR);
        var date: Date = ft.parse(dateStr)
        val calendar = Calendar.getInstance()
        calendar.time = date
        root.findViewById<Spinner>(R.id.spinnerMonth).setSelection(calendar.get(Calendar.MONTH) + 1)
        root.findViewById<Spinner>(R.id.spinnerDays).setSelection(calendar.get(Calendar.DAY_OF_MONTH))
        root.findViewById<Spinner>(R.id.spinnerYear).setSelection(currentYear - calendar.get(Calendar.YEAR) + 1)
    }


    @JvmStatic
    fun getDiffYears(a: Calendar, b: Calendar): Int {
        var diff = b.get(YEAR) - a.get(YEAR)
        if (a.get(MONTH) > b.get(MONTH) || a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE)) {
            diff--
        }
        return diff
    }


    @JvmStatic
    fun checkDate(v: View, errMess: String): Date? {
        val hint = v.findViewById<TextView>(R.id.RegisterDateHint)
        val spinnerYear = v.findViewById<Spinner>(R.id.spinnerYear)
        val month = v.findViewById<Spinner>(R.id.spinnerMonth).selectedItemPosition - 1
        val day = v.findViewById<Spinner>(R.id.spinnerDays).selectedItemPosition

        if (spinnerYear.selectedItemPosition == 0 || month == -1 || day == 0) {
            hint.error = errMess
            return null
        }

        val year = Integer.parseInt(spinnerYear.selectedItem.toString())

        val cal = Calendar.getInstance()
        cal.set(year, month, day)
        val today = Calendar.getInstance()
        if (MyDateUtils.getDiffYears(cal, today) < 13) {
            hint.error = errMess
            return null
        }
        hint.error = null
        return cal.time
    }

    @JvmStatic
    fun setDateSpinners(v: View, context: Context) {
        val spinMonth = v.findViewById<Spinner>(R.id.spinnerMonth)
        val spinDays = v.findViewById<Spinner>(R.id.spinnerDays)
        val spinYears = v.findViewById<Spinner>(R.id.spinnerYear)

        val adapterMonth = ArrayAdapter(context,
                R.layout.item_spinner, context.resources.getStringArray(R.array.months_spinner))
        spinMonth.adapter = adapterMonth

        val years = ArrayList<String>()
        years.add(context.resources.getString(R.string.year))
        val thisYear = Calendar.getInstance().get(Calendar.YEAR)
        for (i in thisYear downTo 1900) {
            years.add(Integer.toString(i))
        }
        val adapterYear = ArrayAdapter(context, R.layout.item_spinner, years)
        spinYears.adapter = adapterYear

        val days = ArrayList<String>()
        days.add(context.resources.getString(R.string.day))
        for (i in 1..31) {
            days.add(Integer.toString(i))
        }
        val adapterDays = ArrayAdapter(context, R.layout.item_spinner, days)
        spinDays.adapter = adapterDays
    }

}