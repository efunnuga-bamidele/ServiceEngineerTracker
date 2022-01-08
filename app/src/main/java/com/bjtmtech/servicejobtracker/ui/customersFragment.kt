package com.bjtmtech.servicejobtracker.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bjtmtech.servicejobtracker.R
import com.bjtmtech.servicejobtracker.adapter.MyAdapterCustomerName
import com.bjtmtech.servicejobtracker.adapter.MyAdapterJobType
import com.bjtmtech.servicejobtracker.data.CustomerName
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.fragment_customers.*


class customersFragment : Fragment() {

    private var EditUID : Int? = null

    val db = Firebase.firestore
    private var dataSize: Int = 0
    private lateinit var recyclerView : RecyclerView
    private lateinit var  customersArrayList : ArrayList<CustomerName>
    private lateinit var  myAdapter : MyAdapterCustomerName
    private lateinit var  database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseSettings()

//        var navView: NavigationView = (activity as MainActivity).findViewById(R.id.nav_view)

        recyclerView = cvRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        customersArrayList = arrayListOf()

        myAdapter = MyAdapterCustomerName(customersArrayList)
        recyclerView.adapter  = myAdapter

        myAdapter.setOnItemClickListener(object : MyAdapterCustomerName.onItemClickListener{
            override fun onItemClick(position: Int) {
                FancyToast.makeText(context,customersArrayList[position].name.toString(), FancyToast.INFO, FancyToast.LENGTH_SHORT, true).show()
//                EditUID = customersArrayList[position].UID
                cvCustomerNameEditText.setText(customersArrayList[position].name.toString())
            }
        })

        //Add Divider
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        EventChangeListener()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customers, container, false)
    }

    private fun databaseSettings() {
        //offline persistence enabled
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
//          .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = settings
    }


    private fun EventChangeListener() {
        db.collection("customerNames")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen error", e)
                    return@addSnapshotListener
                }

                for (change in querySnapshot!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "Customers Name: ${change.document.data}")
                        customersArrayList.add(change.document.toObject(CustomerName::class.java))
                    }


                    val source = if (querySnapshot.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
                    Log.d(TAG, "Data fetched from $source")
                }
                myAdapter.notifyDataSetChanged()
            }
    }




}