package com.bjtmtech.servicejobtracker

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_jobtype.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [jobtypeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class jobtypeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var EditUID : Int? = null

//    private lateinit var database : DatabaseReference

    // [START get_firestore_instance]
    val db = Firebase.firestore
    private var dataSize: Int = 0
    private lateinit var recyclerView : RecyclerView
    private lateinit var  jobsArrayList : ArrayList<JobTypes>
    private lateinit var  myAdapter : MyAdapterJobType
    private lateinit var  database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()

        var navView: NavigationView = (activity as MainActivity).findViewById(R.id.nav_view)

        database = FirebaseFirestore.getInstance()
        recyclerView = jtvRecyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        jobsArrayList = arrayListOf()

        myAdapter = MyAdapterJobType(jobsArrayList)

        recyclerView.adapter = myAdapter


        myAdapter.setOnItemClickListener(object: MyAdapterJobType.onItemClickListener{
            override fun onItemClick(position: Int) {
                EditUID = jobsArrayList[position].UID
                jtJobType.setText(jobsArrayList[position].name.toString())

            }

        } )

        //Add Divider
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            ))

        EventChangeListener()

        //Call the action to create the table data
        jtCreateButton.setOnClickListener {

            db.collection("jobTypes")
                .get()
                .addOnSuccessListener { result ->
                    dataSize = result.size()

                   // Get te datas from the text input
                    val jobType = jtJobType.text.toString().trim()
                    val jtID = dataSize + 1

                    // Create a job hashmap
                    val job = hashMapOf(
                        "UID" to jtID,
                        "name" to jobType
                    )

                    db.collection("jobTypes").document(job["UID"].toString())
                        .set(job)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(context,"Job type successfully written!", Toast.LENGTH_SHORT).show()
                            jobsArrayList.clear()
                            EventChangeListener()
                            jtJobType.text!!.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error adding job type!", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }

//
        }



        jtUpdateButton.setOnClickListener {

           db.collection("jobTypes").document(EditUID.toString())
                .update("name", jtJobType.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(context,"Job type successfully updated!", Toast.LENGTH_SHORT).show()
                    jobsArrayList.clear()
                    EventChangeListener()
                    jtJobType.text!!.clear()

                }
                .addOnFailureListener {  Toast.makeText(context,"Error updating job type!", Toast.LENGTH_SHORT).show()}
          
        }

        jtDeleteButton.setOnClickListener {

            db.collection("jobTypes").document(EditUID.toString())
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context,"Job type successfully deleted!", Toast.LENGTH_SHORT).show()
                    jobsArrayList.clear()
                    EventChangeListener()
                    jtJobType.text!!.clear()

                }
                .addOnFailureListener {  Toast.makeText(context,"Error deleting job type!", Toast.LENGTH_SHORT).show()}

        }

        jtClearButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(jobtypeFragment(), "Job Type")
            navView.setCheckedItem(R.id.nav_jobType)
        }
    }

    private fun triggerRecycler() {


    }

    private fun EventChangeListener() {
        db.collection("jobTypes").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                       if (error != null){
                           Log.e("Firestore", error.message.toString())
                           return
                       }

                        for(dc : DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                jobsArrayList.add(dc.document.toObject(JobTypes::class.java))
                            }
                        }

                        myAdapter.notifyDataSetChanged()
                    }

                })
    }

    private fun setup() {

        // [END get_firestore_instance]

        // [START set_firestore_settings]
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
        // [END set_firestore_settings]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jobtype, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment jobtypeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            jobtypeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}