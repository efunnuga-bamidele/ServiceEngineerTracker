package com.bjtmtech.servicejobtracker

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.SharedPreferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.sql.Timestamp
import java.text.SimpleDateFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [dashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class dashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var PRIVATE_MODE = 0
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref: SharedPreferences = context!!.getSharedPreferences("myProfile", PRIVATE_MODE)
        val engineerEmailQuery = sharedPref.getString("email" , "defaultemail@mail.com" ).toString()

        val sdf = SimpleDateFormat("MMMM dd, yyyy")
        val sdfy = SimpleDateFormat("yyyy")
        val sdfm = SimpleDateFormat("MM") //Number representations
        val timestamp = Timestamp(System.currentTimeMillis())
        val currentYear = sdfy.format(timestamp).toString()
        val currentDate = sdf.format(timestamp).toString()


//        Active Jobs
        db.collection("createdJobs")
            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("jobStatus", "ACTIVE")
            .whereEqualTo("createdYear", currentYear)

            .get()
            .addOnSuccessListener { documents ->
                dbActiveCounter.setText(documents.size().toString())

            }
            .addOnFailureListener { exception ->
                dbActiveCounter.setText("0")
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
        activeJobLayout.setOnClickListener {
            Toast.makeText(context, "Total Active Jobs : ${dbActiveCounter.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }

        //        Completed Jobs
        db.collection("createdJobs")
            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("jobStatus", "COMPLETED",)
            .whereEqualTo("createdYear", currentYear,)
            .get()
            .addOnSuccessListener { documents ->
                dbCompletedCounter.setText(documents.size().toString())

            }
            .addOnFailureListener { exception ->
                dbCompletedCounter.setText("0")
            }

        completedJobsLayout.setOnClickListener {
            Toast.makeText(context, "Total Completed Jobs : ${dbCompletedCounter.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }

        //        Canceled Jobs
        db.collection("createdJobs")
            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("jobStatus", "CANCELED",)
            .whereEqualTo("createdYear", currentYear,)

            .get()
            .addOnSuccessListener { documents ->
                dbCanceledCounter.setText(documents.size().toString())

            }
            .addOnFailureListener { exception ->
                dbCanceledCounter.setText("0")
            }

        canceledJobsLayout.setOnClickListener {
            Toast.makeText(context, "Total Canceled Jobs : ${dbCanceledCounter.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }

        //        Pending Jobs
        db.collection("createdJobs")
            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("jobStatus", "PENDING",)
            .whereEqualTo("createdYear", currentYear,)

            .get()
            .addOnSuccessListener { documents ->
                dbPendingCounter.setText(documents.size().toString())

            }
            .addOnFailureListener { exception ->
                dbPendingCounter.setText("0")
            }
        pendingJobsLayout.setOnClickListener {
            Toast.makeText(context, "Total Pending Jobs : ${dbPendingCounter.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }
//        Days Worked
        var duration : Int = 0
        db.collection("createdJobs")

            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("jobStatus", "COMPLETED",)
            .whereEqualTo("createdYear", currentYear,)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    duration += document.data["jobDuration"].toString().toInt()
                }
                dbWorkedDays.setText(duration.toString())

            }
            .addOnFailureListener { exception ->
                dbWorkedDays.setText("0")
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        workDaysLayout.setOnClickListener {
            Toast.makeText(context, "Total Worked Days : ${dbWorkedDays.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }

        //        Site Visited
        var siteVisited : Int = 0
        db.collection("createdJobs")

            .whereEqualTo("engineerEmail", engineerEmailQuery.toString()).whereEqualTo("createdYear", currentYear,).whereEqualTo("jobStatus", "COMPLETED",)
            .get()
            .addOnSuccessListener { documents ->
                dbCustomerVisited.setText(documents.size().toString())

            }
            .addOnFailureListener { exception ->
                dbCustomerVisited.setText("0")
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        customerVisitLayout.setOnClickListener {
            Toast.makeText(context, "Total Customer Visits : ${dbCustomerVisited.text.toString()} \n Job Year :  $currentYear" +
                    "\n Current date : $currentDate", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            dashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}