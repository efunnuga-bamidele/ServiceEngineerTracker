package com.bjtmtech.servicejobtracker

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_active_job.*
import kotlinx.android.synthetic.main.fragment_create_job.*
//import kotlinx.android.synthetic.main.fragment_create_job.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.logging.SimpleFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [activeJobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class activeJobFragment : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private val calendar = Calendar.getInstance()

    var startDate:String? = null
    var stopDate:String? = null

    var startYear:Int = calendar.get(Calendar.YEAR)
    var startMonth:Int = calendar.get(Calendar.MONTH)
    var startDay:Int = calendar.get(Calendar.DAY_OF_MONTH)
    var createdYear:Int ?= 0
    var createdMonth:Int ?= 0

    var stopYear:Int = calendar.get(Calendar.YEAR)
    var stopMonth:Int = calendar.get(Calendar.MONTH)
    var stopDay:Int = calendar.get(Calendar.DAY_OF_MONTH)

    private var PRIVATE_MODE = 0
    val db = Firebase.firestore
    var dataRef: String ?= null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_job, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        set sharedpreference to get email of user
        val sharedPref : SharedPreferences = context!!.getSharedPreferences("myProfile", PRIVATE_MODE)
//        create variable to collect the email address
        val engineerEmailQuery = sharedPref.getString("email", "defaultemail@mail.com").toString()

//        create date format for database filtering
        val sdfy = SimpleDateFormat("yyyy")
        val timeStamp = Timestamp(System.currentTimeMillis())
        val currentYear = sdfy.format(timeStamp).toString()





        db.collection("createdJobs").whereEqualTo("engineerEmail", engineerEmailQuery.toString())
            .whereEqualTo("jobStatus", "ACTIVE")
            .whereEqualTo("createdYear",currentYear)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    dataRef = document.id
//                    Toast.makeText(context, dataRef, Toast.LENGTH_SHORT).show()
                    ajCustomerName.setText(document.data.get("customerName").toString())
                    ajCustomerCountry.setText(document.data.get("customerCountry").toString())
                    ajCustomerState.setText(document.data.get("customerState").toString())
                    ajBtnStartDate.setText("START:\n"+document.data.get("startDate").toString())
                    ajBtnStopDate.setText("STOP:\n"+document.data.get("stopDate").toString())
                    ajDateRangeText.setText(document.data.get("jobDuration").toString())
                    ajJobType.setText(document.data.get("jobType").toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        var navView: NavigationView = (activity as MainActivity).findViewById(R.id.nav_view)

        ajCompleteJob.setOnClickListener {
            val updateRef = db.collection("createdJobs").document(dataRef.toString())
            updateRef
//                .update("jobStatus", "COMPLETED")
                .update(mapOf(
                    "jobStatus" to "COMPLETED",
                    "updatedDate" to FieldValue.serverTimestamp()
                ))

                .addOnSuccessListener {
//                    Log.d(TAG, "Job completed successfully!")
                    (activity as MainActivity).replaceFragment(dashboardFragment(), "Dashboard")
                    navView.setCheckedItem(R.id.nav_dashboard)
                    Toast.makeText(context, "Job completed successfully", Toast.LENGTH_SHORT).show()
                }

                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(context, "Error completing job! Please try again", Toast.LENGTH_SHORT).show()
                }
        }

        ajPendJob.setOnClickListener {
            val updateRef = db.collection("createdJobs").document(dataRef.toString())
            updateRef
                .update(mapOf(
                    "jobStatus" to "PENDING",
                    "updatedDate" to FieldValue.serverTimestamp()
                ))
                .addOnSuccessListener {
//                    Log.d(TAG, "Job completed successfully!")
                    (activity as MainActivity).replaceFragment(dashboardFragment(), "Dashboard")
                    navView.setCheckedItem(R.id.nav_dashboard)
                    Toast.makeText(context, "Job pended successfully", Toast.LENGTH_SHORT).show()
                }

                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(context, "Error pending job! Please try again", Toast.LENGTH_SHORT).show()
                }
        }

        ajCancelJob.setOnClickListener {
            val updateRef = db.collection("createdJobs").document(dataRef.toString())
            updateRef
                .update(mapOf(
                    "jobStatus" to "CANCELED",
                    "updatedDate" to FieldValue.serverTimestamp()
                ))
                .addOnSuccessListener {
//                    Log.d(TAG, "Job completed successfully!")
                    (activity as MainActivity).replaceFragment(dashboardFragment(), "Dashboard")
                    navView.setCheckedItem(R.id.nav_dashboard)
                    Toast.makeText(context, "Job canceled successfully", Toast.LENGTH_SHORT).show()
                }

                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(context, "Error canceling job! Please try again", Toast.LENGTH_SHORT).show()
                }
        }

        ajEditJob.setOnClickListener {
            Toast.makeText(context, "Edit function is now enabled!", Toast.LENGTH_SHORT).show()
            enableInterfaceComponents()
            getAllListDetails()
            ajBtnStopDate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.btnFaded))

        }


        val dataSetListenerStart = object: DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, startYear: Int, startMonth: Int, startDay: Int) {
//                Log.d(ContentValues.TAG,"Get the date")
                calendar.set(Calendar.YEAR, startYear)
                calendar.set(Calendar.MONTH, startMonth)
                calendar.set(Calendar.DAY_OF_MONTH, startDay)
                updateDateStartDate()
            }
        }

        ajBtnStartDate.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(context!!, dataSetListenerStart,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()

            }

        } )


        val dataSetListenerStop = object: DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, stopYear: Int, stopMonth: Int, stopDay: Int) {
//                Log.d(ContentValues.TAG,"Get the date")
                calendar.set(Calendar.YEAR, stopYear)
                calendar.set(Calendar.MONTH, stopMonth)
                calendar.set(Calendar.DAY_OF_MONTH, stopDay)
                updateDateStopDate()
            }
        }

        ajBtnStopDate.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(context!!, dataSetListenerStop,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()

            }

        } )

        ajCancelUpdate.setOnClickListener {
            (activity as MainActivity).replaceFragment(activeJobFragment(), "View Active Job")
            navView.setCheckedItem(R.id.nav_view_active_job)
        }

        ajUpdateJob.setOnClickListener {
            var startDateQuery : String? = null
            var stopDateQuery :String? = null
            var customerNameQuery :String? = null
            var customerCountryQuery :String? = null
            var customerStateQuery :String? = null
            var jobDurationQuery :String? = null
            var jobTypeQuery :String? = null


            if (ajBtnStopDate.text.toString()  != "STOP"  ) {
                val customerNameQuery = ajCustomerName.text.toString()
                val customerCountryQuery = ajCustomerCountry.text.toString()
                val customerStateQuery = ajCustomerState.text.toString().capitalize()
                val jobDurationQuery = ajDateRangeText.text.toString()
                val jobTypeQuery = ajJobType.text.toString()

                if (ajBtnStartDate.text.split(" ").size == 1) {
                    Log.d(TAG, "one index "+ajBtnStartDate.text.split(" ").size )
                    startDateQuery = ajBtnStartDate.text.split(":")[1].toString().trim()
                    stopDateQuery = ajBtnStopDate.text.split(":")[1].toString().trim()

                    //push update here


                } else if (ajBtnStartDate.text.split(" ").size > 1) {
                    Log.d(TAG, "two index "+ajBtnStartDate.text.split(" ").size)
                    startDateQuery = ajBtnStartDate.text.split(":")[1].toString().trim()
                    stopDateQuery = ajBtnStopDate.text.split(":")[1].toString().trim()

//                    Toast.makeText(context, "Reset Page with original data", Toast.LENGTH_SHORT).show()

                }
                if(customerNameQuery!!.isNotEmpty() && customerCountryQuery!!.isNotEmpty()
                    && customerStateQuery!!.isNotEmpty() && startDateQuery!!.isNotEmpty()
                    && stopDateQuery!!.isNotEmpty() && jobDurationQuery!!.isNotEmpty()
                    && jobTypeQuery!!.isNotEmpty()){

                    db.collection("createdJobs").document(dataRef.toString())
                        .update(mapOf(
                            "customerName" to customerNameQuery.toString(),
                            "customerCountry" to customerCountryQuery.toString(),
                            "customerState" to customerStateQuery.toString(),
                            "startDate" to startDateQuery.toString(),
                            "stopDate" to stopDateQuery.toString(),
                            "jobDuration" to jobDurationQuery.toString(),
                            "jobType" to jobTypeQuery.toString(),
                            "updatedDate" to FieldValue.serverTimestamp()
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Job updated created!", Toast.LENGTH_SHORT).show()
                            (activity as MainActivity).replaceFragment(activeJobFragment(), "View Active Job")
                            navView.setCheckedItem(R.id.nav_view_active_job)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Error updating job!", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(context, "Some details are missing, Please check all fields!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "The start date, stop date and duration are not set", Toast.LENGTH_SHORT).show()
            }



        }

    }

    private fun updateDateStartDate() {
        val myFormat = "MM.dd.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        startDate = sdf.format(calendar.time)

        startYear = sdf.format(calendar.time).split(".")[2].toInt()
        createdYear = startYear
        startMonth = sdf.format(calendar.time).split(".")[0].toInt()
        createdMonth = startMonth
        startDay = sdf.format(calendar.time).split(".")[1].toInt()
        ajBtnStartDate.text = "Start: "+startDate
        if(ajBtnStartDate.text != "START"){
//            Toast.makeText(context, "is not empty", Toast.LENGTH_SHORT).show()
            ajBtnStopDate.isEnabled = true
            ajBtnStopDate.isClickable = true
            ajBtnStopDate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.btnInverse))
            ajBtnStopDate.text = "STOP"
            ajDateRangeText.text = "0"
        }

    }


    private fun updateDateStopDate() {
        val myFormat = "MM.dd.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        stopDate = sdf.format(calendar.time)

        stopYear= sdf.format(calendar.time).split(".")[2].toInt()
        stopMonth = sdf.format(calendar.time).split(".")[0].toInt()
        stopDay = sdf.format(calendar.time).split(".")[1].toInt()
        ajBtnStopDate.text = "Stop: "+stopDate
        daysBetweenDates()
    }

    fun daysBetweenDates(){
        val start = LocalDate.of(startYear, startMonth, startDay)
        val end = LocalDate.of(stopYear, stopMonth, stopDay)

        val days = ChronoUnit.DAYS.between(start, end) + 1
//        Toast.makeText(context, "days $days",Toast.LENGTH_SHORT).show()
        ajDateRangeText.text = days.toString()
    }

    private fun getAllListDetails() {
        //Get Names of customers on view Created
        db.collection("customerNames")
            .get()
            .addOnSuccessListener { result ->
                val customersName = ArrayList<String>()
                for (document in result) {
//                        Log.d(TAG, "${document.id} => ${document.data["name"]}")
                    customersName.add(document.data["name"].toString())
                }


                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.customer_name_dropdown_items, customersName)
                ajCustomerName.setAdapter(arrayAdapter)

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        //        Code to getting country list
        val countryNamesList = ArrayList<String>()
        val countriesList: MutableList<String> = ArrayList()
        val locales = Locale.getISOCountries()
        for (countryCode in locales) {
            val obj = Locale("", countryCode)
            countriesList.add(obj.getDisplayCountry(Locale.ENGLISH))
            Collections.sort(countriesList)
        }
        for (s in countriesList) {
            countryNamesList.add(s)
        }

        val arrayAdapterCountry = ArrayAdapter(requireContext(), R.layout.customer_name_dropdown_items, countryNamesList)
        ajCustomerCountry.setAdapter(arrayAdapterCountry)


        //Get jobTypes on view Created
        db.collection("jobTypes")
            .get()
            .addOnSuccessListener { result ->
                val jobTypesList = ArrayList<String>()
                for (document in result) {
                    //                        Log.d(TAG, "${document.id} => ${document.data["name"]}")
                    jobTypesList.add(document.data["name"].toString())
                }


                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.customer_name_dropdown_items, jobTypesList)
                ajJobType.setAdapter(arrayAdapter)

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun enableInterfaceComponents() {
        ajCustomerName.isEnabled = true
        ajCustomerName.isClickable = true
///////////////////////////////////////////////
        ajCustomerCountry.isEnabled = true
        ajCustomerCountry.isClickable = true
///////////////////////////////////////////////
        ajCustomerState.isEnabled = true
        ajCustomerState.isClickable = true
///////////////////////////////////////////////
        ajJobType.isEnabled = true
        ajJobType.isClickable = true
///////////////////////////////////////////////
        ajBtnStartDate.isEnabled = true
        ajBtnStartDate.isClickable = true
        ajBtnStartDate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.btnInverse))
///////////////////////////////////////////////
//        ajBtnStopDate.isEnabled = true
//        ajBtnStopDate.isClickable = true
///////////////////////////////////////////////
        ajCompleteJob.visibility = View.GONE
        ajCancelJob.visibility = View.GONE
        ajPendJob.visibility = View.GONE
        ajEditJob.visibility = View.GONE
///////////////////////////////////////////////
        ajUpdateJob.visibility = View.VISIBLE
        ajCancelUpdate.visibility = View.VISIBLE
        textLogoActive.setText("Edit Active Job")
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment activeJobFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            activeJobFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}