package com.bjtmtech.servicejobtracker






import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_create_job.*
import kotlinx.android.synthetic.main.fragment_jobtype.*
import kotlinx.android.synthetic.main.nav_header.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class createJobFragment : Fragment(){

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

    private var param1: String? = null
    private var param2: String? = null
    val db = Firebase.firestore

    private var engineerEmailQuery: String? = null
    private var engineerNameQuery: String? = null
    private var engineerCountryQuery: String? = null

    private var queryFirstName: String? = null
    private var queryLastName: String? = null
    private var queryContry: String? = null

//    var sharedData : SharedPreferences = activity!!.getSharedPreferences("myProfile",Context.MODE_PRIVATE)
    private var PRIVATE_MODE = 0
//    private var firstRun : String = "myProfile"
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
                    cjCustomerName.setAdapter(arrayAdapter)

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
                cjCustomerCountry.setAdapter(arrayAdapterCountry)


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
                    cjJobType.setAdapter(arrayAdapter)

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
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

        cjBtnStartDate.setOnClickListener(object: View.OnClickListener{
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
    cjBtnStopDate.setOnClickListener(object: View.OnClickListener{
        override fun onClick(view: View?) {
            DatePickerDialog(context!!, dataSetListenerStop,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()

        }

    } )

    cjSaveData.setOnClickListener {
        if(cjBtnStopDate.text.toString() != "Stop Date"){
            createJobRecord()
        }else{
            Toast.makeText(context, "The start date, stop date and duration are not set", Toast.LENGTH_SHORT).show()
        }


    }

    cjClearData.setOnClickListener{
        clearField()
    }

//    get Engineer Profile details
    val sharedPref: SharedPreferences = context!!.getSharedPreferences("myProfile", PRIVATE_MODE)
    engineerEmailQuery = sharedPref.getString("email" , "defaultemail@mail.com" ).toString()
//    Log.d(TAG, "documents: ${engineerEmailQuery.toString()}")
    getuserProfileDetails(engineerEmailQuery.toString())


//

    }

    private fun createJobRecord() {
        val sdf = SimpleDateFormat("MMM dd,yyyy")
        val sdfwt = SimpleDateFormat("MMM dd,yyyy-HH-mm-ss")
        val sdfm = SimpleDateFormat("MMMM")
        val sdfy = SimpleDateFormat("yyyy")
        val timestamp = Timestamp(System.currentTimeMillis())

        val customerNameQuery = cjCustomerName.text.toString().trim()
        val customerCountryQuery = cjCustomerCountry.text.toString().trim()
        val customerStateQuery = cjCustomerState.text.toString().capitalize().trim()
        val startDateQuery = startDate.toString().trim()
        val stopDateQuery = stopDate.toString().trim()
        val jobDurationQuery = cjDateRangeText.text.toString().trim()
        val jobTypeQuery = cjJobType.text.toString().trim()
        val jobStatusQuery = "ACTIVE"

//Date formats
        val createdYearQ = sdfy.format(timestamp).toString() //Used to Query database to check for active jobs
        val createdDateQuery = sdf.format(timestamp).toString() //createdDate time stamp

//      Check if there is an active job if yes prevent new job creation else allow job creation

       db.collection("createdJobs")
            .whereEqualTo("engineerEmail",engineerEmailQuery.toString())
            .whereEqualTo("jobStatus","ACTIVE")
            .whereEqualTo("createdYear",createdYearQ.toString())
            .get()
            .addOnSuccessListener { documents ->
//                check if document is equal to 0
                if (documents.size() == 0) {

                    //Check for empty input fields
            if(customerNameQuery.isNotEmpty() && customerCountryQuery.isNotEmpty()
                && customerStateQuery.isNotEmpty() && startDateQuery.isNotEmpty()
                && stopDateQuery.isNotEmpty() && jobDurationQuery.isNotEmpty()
                && engineerEmailQuery.toString().isNotEmpty() && engineerNameQuery.toString().isNotEmpty()
                && engineerCountryQuery.toString().isNotEmpty()
                && createdDateQuery.isNotEmpty() && jobTypeQuery.isNotEmpty()){
//create a data map
                val jobData = hashMapOf(
                    "customerName" to customerNameQuery.toString(),
                    "customerCountry" to customerCountryQuery.toString(),
                    "customerState" to customerStateQuery.toString(),
                    "startDate" to startDateQuery.toString(),
                    "stopDate" to stopDateQuery.toString(),
                    "jobDuration" to jobDurationQuery.toString(),
                    "jobType" to jobTypeQuery.toString(),
                    "engineerEmail" to engineerEmailQuery.toString(),
                    "engineerName" to engineerNameQuery.toString(),
                    "engineerCountry" to engineerCountryQuery.toString(),
                    "createdDate" to createdDateQuery.toString(),
                    "createdMonth" to createdMonth.toString(),
                    "createdYear" to createdYear.toString(),
                    "jobStatus" to jobStatusQuery.toString()
                )
//Database Update query
                var docRef  = customerNameQuery.toString().split(" ")[0]+"-"+sdfwt.format(timestamp).toString().replace("\\s".toRegex(), "")
                db.collection("createdJobs").document(docRef.toString())
                    .set(jobData)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(context, "Job successfully created!", Toast.LENGTH_SHORT).show()
                        clearField()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error creating job!", Toast.LENGTH_SHORT).show()
                    }

            }else{
                Toast.makeText(context, "Some details are missing, Please check all fields!", Toast.LENGTH_SHORT).show()
            }
//feedback if result is greater than 0 meaning data exist

                } else {

                    Toast.makeText(context, "There Is Currently An Active Job! \nPlease [ COMPLETE / PEND /CANCEL ] \nTo Be Able To Create New Jobs", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    private fun clearField() {
//        Toast.makeText(context, "I was called", Toast.LENGTH_SHORT).show()
        cjCustomerName.text.clear()
        cjCustomerCountry.text.clear()
        cjCustomerState.text!!.clear()
        cjBtnStartDate.text = "Start Date"
        cjBtnStopDate.text = "Stop Date"
        startDate = null
        stopDate = null
        cjDateRangeText.text = "0"
        cjBtnStopDate.isEnabled = false
        cjBtnStopDate.isClickable = false
        cjBtnStopDate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.btnFaded))
        cjJobType.text.clear()
    }

    private fun getuserProfileDetails(FIELDREF : String){
        Log.d(TAG, "Function Called")
        db.collection("users")
            .whereEqualTo("email", FIELDREF)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    queryFirstName = document.data.get("firstName").toString()
                    queryLastName = document.data.get("lastName").toString()
                    queryContry = document.data.get("country").toString()

//                    Log.d(TAG, "getting documents: $queryContry, $queryLastName ,$queryFirstName ")


                    engineerNameQuery = queryFirstName.toString()+" "+queryLastName.toString()
                    engineerCountryQuery = queryContry.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


    private fun updateDateStartDate(){
        val myFormat = "MM.dd.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        startDate = sdf.format(calendar.time)

        startYear = sdf.format(calendar.time).split(".")[2].toInt()
        createdYear = startYear
        startMonth = sdf.format(calendar.time).split(".")[0].toInt()
        createdMonth = startMonth
        startDay = sdf.format(calendar.time).split(".")[1].toInt()
        cjBtnStartDate.text = "Start: "+startDate
        if(cjBtnStartDate.text != "START"){
//            Toast.makeText(context, "is not empty", Toast.LENGTH_SHORT).show()
            cjBtnStopDate.isEnabled = true
            cjBtnStopDate.isClickable = true
            cjBtnStopDate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),R.color.btnInverse))
        }
    }

    private fun updateDateStopDate(){
        val myFormat = "MM.dd.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        stopDate = sdf.format(calendar.time)

        stopYear= sdf.format(calendar.time).split(".")[2].toInt()
        stopMonth = sdf.format(calendar.time).split(".")[0].toInt()
        stopDay = sdf.format(calendar.time).split(".")[1].toInt()
        cjBtnStopDate.text = "Stop: "+stopDate
        daysBetweenDates()
    }


    fun daysBetweenDates(){
        val start = LocalDate.of(startYear, startMonth, startDay)
        val end = LocalDate.of(stopYear, stopMonth, stopDay)

        val days = ChronoUnit.DAYS.between(start, end) + 1
//        Toast.makeText(context, "days $days",Toast.LENGTH_SHORT).show()
        cjDateRangeText.text = days.toString()
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
