package com.bignerdranch.android.noteyourday

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val  ARG_DATE = "date"

class DatePickerFragment:DialogFragment() {

    interface Callbacks{

        fun onDateSelected(Date:Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Sending back the date to MemoryFragment
        val dateListener = DatePickerDialog.OnDateSetListener{
                _: DatePicker, year: Int, month: Int, day: Int ->
            val resultDate : Date = GregorianCalendar(year, month, day).time
            targetFragment?.let {fragment ->
                (fragment as Callbacks).onDateSelected(resultDate)
            }
        }

        // Get date from arguments
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time =  date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
           dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    // Hide the date in argument, where Fragment can access
    companion object{
        fun newInstance(date:Date):DatePickerFragment{
            val args = Bundle().apply{
                putSerializable(ARG_DATE,date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}