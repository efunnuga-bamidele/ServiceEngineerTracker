package com.bjtmtech.servicejobtracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{

    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {

//        Log.d(TAG,"Get the date")
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)


        val selectedDate = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(calendar.time)
        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_DATE", selectedDate)

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
    }
}