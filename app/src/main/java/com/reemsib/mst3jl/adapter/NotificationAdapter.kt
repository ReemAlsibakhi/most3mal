package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Notification
import kotlinx.android.synthetic.main.notification_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(var activity: Activity, var data: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onClicked(id:String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_name
        val content = itemView.tv_content
        val date = itemView.tv_date

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.notification_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.title.text = data[position].user.name
       holder.content.text = data[position].message+" "+data[position].title
       holder.date.setText(getFormatDate(data[position].created_at))

        holder.itemView.setOnClickListener {

            if (mListener != null) {
             mListener!!.onClicked( data[position].advertisement_id)
            }
        }
    }
    fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }

}

