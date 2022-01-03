package com.bjtmtech.servicejobtracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bjtmtech.servicejobtracker.data.JobTypes
import com.bjtmtech.servicejobtracker.R

data class MyAdapterJobHistory(private val jobHistoryList : ArrayList<JobTypes>) : RecyclerView.Adapter<MyAdapterJobHistory.MyJobsViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }



    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onBindViewHolder(holder: MyJobsViewHolder, position: Int) {
        val jobs : JobTypes = jobHistoryList[position]

        holder.uid.text = jobs.UID.toString()
        holder.jobTypeName.text = jobs.name.toString()
        holder.myButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.jobtype_item,
            parent, false)

        return MyJobsViewHolder(itemView, mListener)
    }


    override fun getItemCount(): Int {
        return jobHistoryList.size
    }

    public class MyJobsViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val uid : TextView = itemView.findViewById(R.id.jtvId)
        val jobTypeName : TextView = itemView.findViewById(R.id.jtvJobTypeName)
        val myButton = itemView.findViewById<Button>(R.id.myButton)

        init {
            myButton.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

}