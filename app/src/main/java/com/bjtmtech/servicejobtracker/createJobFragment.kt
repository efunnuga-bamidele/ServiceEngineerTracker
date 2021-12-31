package com.bjtmtech.servicejobtracker






import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import com.google.type.Date
import kotlinx.android.synthetic.main.fragment_create_job.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class createJobFragment : Fragment(){

//    var cal = Calendar.getInstance()

//    private lateinit var binding: ActivityMainBinding
private val calendar = Calendar.getInstance()

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
//Manually Implemented function to check when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dataSetListenerStart = object: DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//                Log.d(ContentValues.TAG,"Get the date")
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateStartDate()
            }
        }
        cjBtnStartDate.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(context!!, dataSetListenerStart,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()

            }

        } )

    val dataSetListenerStop = object: DatePickerDialog.OnDateSetListener{
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//                Log.d(ContentValues.TAG,"Get the date")
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateStopDate()
        }
    }
    cjBtnStopDate.setOnClickListener(object: View.OnClickListener{
        override fun onClick(view: View?) {
            DatePickerDialog(context!!, dataSetListenerStop,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()

        }

    } )

    }

    private fun updateDateStartDate(){
        val myFormat = "dd-MMM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        cjBtnStartDate.text = sdf.format(calendar.time)
    }

    private fun updateDateStopDate(){
        val myFormat = "dd-MMM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        cjBtnStopDate.text = sdf.format(calendar.time)
//
//
//        val d1: String = cjBtnStopDate.text.toString()
//        val d2: String = cjBtnStartDate.text.toString()
//
//        val difference: Long = Math.abs(d1- d2)
//
//        val days: Int = Days.daysBetween(date1, date2).getDays()
//
//        val difftDays = difference / (24 * 60 * 60 * 1000)

//        Log.i("Testing", "days$difftDays")
//        days.setText("days$difftDays")
//        val instant1 = cjBtnStopDate.text
//        val instant2 = cjBtnStartDate.text
//        val diff: Duration = Duration.between(instant1, instant2)
//        val minutes = diff.toMinutes()
//        Toast.makeText(context, "days$difftDays",Toast.LENGTH_SHORT).show()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_job, container, false)

    }




    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            createJobFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }


}
