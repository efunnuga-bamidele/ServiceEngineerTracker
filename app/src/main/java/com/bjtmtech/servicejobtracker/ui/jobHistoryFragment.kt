package com.bjtmtech.servicejobtracker.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bjtmtech.servicejobtracker.R
import com.bjtmtech.servicejobtracker.adapter.MyJobHistoryAdapter
import com.bjtmtech.servicejobtracker.data.JobHistoryData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_job_history.*
import kotlin.collections.ArrayList


class jobHistoryFragment : Fragment() {

    private var EditUID : Int? = null
    val db = Firebase.firestore
    private var dataSize: Int = 0
    private lateinit var recyclerViewHistory : RecyclerView
    private lateinit var  jobsHistoryList : ArrayList<JobHistoryData>
    private lateinit var  myAdapterHistory : MyJobHistoryAdapter

    private lateinit var engineerEmailQuery : String
    private lateinit var sharedPref : SharedPreferences
    private var PRIVATE_MODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        set sharedpreference to get email of user
        sharedPref = context!!.getSharedPreferences("myProfile", PRIVATE_MODE)
//        create variable to collect the email address
        engineerEmailQuery = sharedPref.getString("email", "defaultemail@mail.com").toString()
        setup()

        recyclerViewHistory = jhvRecyclerView

        recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        recyclerViewHistory.setHasFixedSize(true)

        jobsHistoryList = arrayListOf()
//        Toast.makeText(context, jobsHistoryList.toString(), Toast.LENGTH_SHORT).show()

        myAdapterHistory = MyJobHistoryAdapter(jobsHistoryList)

        recyclerViewHistory.adapter = myAdapterHistory

        myAdapterHistory.setOnItemClickListener(object : MyJobHistoryAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
//                Toast.makeText(context, "You clicked item "+position, Toast.LENGTH_SHORT).show()
//                ViewJobsHistoryFragment().show(childFragmentManager, "View")
                val args = Bundle()
                args.putString("key", position.toString())
                args.putString("action", "Clicked")
                val fm: FragmentManager = activity!!.supportFragmentManager
                val overlay = ViewJobsHistoryFragment()
                overlay.setArguments(args)
                overlay.show(fm, "FragmentDialog")

            }

        })

        EventChangeListener()

        //Add Divider
        recyclerViewHistory.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )


        //get item position on recycler view
    val itemTouchHelper = ItemTouchHelper(simpleCallback)
    itemTouchHelper.attachToRecyclerView(recyclerViewHistory)

    }

    private fun setup() {

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    private fun EventChangeListener() {
        db.collection("createdJobs").orderBy("createdDate", Query.Direction.DESCENDING)
            .whereEqualTo("engineerEmail", engineerEmailQuery.toString())
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Firebase Error: ", error.message.toString())
                        return
                    }
                    for (dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            jobsHistoryList.add(dc.document.toObject(JobHistoryData::class.java))

                        }

                    }
                    myAdapterHistory.notifyDataSetChanged()
                }

            })

    }

//    Swipe listener event
//    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)){
private var simpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
           var position = viewHolder.adapterPosition
            var currentHistory = myAdapterHistory.getItemId(position)
            when(direction){
                ItemTouchHelper.RIGHT -> {
                    val args = Bundle()
                    args.putString("key", position.toString())
                    args.putString("action", "SwipeRight")
                    val fm: FragmentManager = activity!!.supportFragmentManager
                    val overlay = ViewJobsHistoryFragment()
                    overlay.setArguments(args)
                    overlay.show(fm, "FragmentDialog")
                }
//                ItemTouchHelper.LEFT -> {
////                    Toast.makeText(context, "Position Left:"+position, Toast.LENGTH_SHORT).show()
//
//                }

            }
            myAdapterHistory.notifyDataSetChanged()
        }

    }








    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_history, container, false)
    }

//
//    override fun onDestroy() {
//        super.onDestroy()
//    }

}
