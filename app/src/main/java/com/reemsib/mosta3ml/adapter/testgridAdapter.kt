package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.Advert
import kotlinx.android.synthetic.main.grid_test_item.view.*

class testgridAdapter(var activity: Activity, var data: ArrayList<Advert>) :
    RecyclerView.Adapter<testgridAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, category: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_adName
        val save = itemView.img_save
        val price = itemView.tv_price
        val date = itemView.tv_date
        val views = itemView.tv_views
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.grid_test_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text = data[position].title
        holder.price.text = data[position].price.toString()
//        holder.date.text = data[position].date
//        holder.views.text = data[position].views.toString()


        holder.itemView.setOnClickListener {
            if (mListener != null) {
                //     mListener!!.onClicked(position, data[position])
            }
        }
    }
//    fun filterList(filteredList: ArrayList<Drug>) {
//        data = filteredList
//        notifyDataSetChanged()
//    }
}