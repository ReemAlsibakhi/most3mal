package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.ChatRoom
import com.reemsib.mosta3ml.setting.MySession
import kotlinx.android.synthetic.main.message_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatsAdapter(var activity: Activity, var data: ArrayList<ChatRoom>) :
    RecyclerView.Adapter<ChatsAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, chat_id: Int,userId:Int,name: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.tv_name
        val date = itemView.tv_date
        val txt = itemView.tv_text
        val num_msg = itemView.num_messages

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.message_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = data[position].user.name
        holder.date.text =getFormatDate(data[position].created_at)
     //   holder.txt.text = data[position].message.message
        holder.num_msg.text = data[position].unread_messages_count.toString()


        holder.itemView.setOnClickListener {
            if (mListener != null) {
                 mListener!!.onClicked(position, data[position].id,MySession.getInstance(activity).getUserId(),data[position].user.name)
            }
        }
    }

    private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }
  //  data[position].message.user_id
//    fun filterList(filteredList: ArrayList<Drug>) {
//        data = filteredList
//        notifyDataSetChanged()
//    }
}

