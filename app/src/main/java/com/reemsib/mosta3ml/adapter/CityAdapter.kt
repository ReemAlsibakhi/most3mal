package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.City
import kotlinx.android.synthetic.main.city_item.view.*

class CityAdapter(var activity: Activity, var data: ArrayList<City>) :
    RecyclerView.Adapter<CityAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id:String, title: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val city = itemView.tv_city
        val desc = itemView.tv_description

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.city_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       holder.city.text = data[position].title
       holder.desc.text = data[position].description


        holder.itemView.setOnClickListener {
            if (mListener != null) {
             mListener!!.onClicked(position, data[position].id.toString(), data[position].title)
            }
        }
    }

}

