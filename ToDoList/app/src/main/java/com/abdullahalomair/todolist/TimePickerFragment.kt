package com.abdullahalomair.todolist

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_DATE = "time"
private const val TAG = "TimePickerFragment"

class TimePickerFragment: DialogFragment(){
    interface Callbacks{
        fun onTimeSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH) + 1
        val initialDate = calendar.get(Calendar.DAY_OF_MONTH)
        var initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        var initialMinute = calendar.get(Calendar.MINUTE)
        val dateListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val formats = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val resultDate: Date = formats.parse("$initialYear-$initialMonth-$initialDate $hourOfDay:$minute")
            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultDate)
            }
        }
        return TimePickerDialog(
                requireContext(),
                dateListener,
                initialHour,
                initialMinute,
                false)
    }
    companion object {

        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }

}