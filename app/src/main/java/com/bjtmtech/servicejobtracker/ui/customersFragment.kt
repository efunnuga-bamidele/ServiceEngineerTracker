package com.bjtmtech.servicejobtracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bjtmtech.servicejobtracker.R
import com.bjtmtech.servicejobtracker.adapter.MyAdapterJobType
import com.bjtmtech.servicejobtracker.data.JobTypes
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class customersFragment : Fragment() {

    private var EditUID : Int? = null

    val db = Firebase.firestore
    private var dataSize: Int = 0
    private lateinit var recyclerView : RecyclerView
    private lateinit var  jobsArrayList : ArrayList<JobTypes>
    private lateinit var  myAdapter : MyAdapterJobType
    private lateinit var  database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customers, container, false)
    }

}