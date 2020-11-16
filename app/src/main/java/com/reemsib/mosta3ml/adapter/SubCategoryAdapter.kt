package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.SubCategory
import kotlinx.android.synthetic.main.sub_category_item.view.*

class SubCategoryAdapter(var activity: Activity, var data: ArrayList<SubCategory>) :
    RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, category: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category = itemView.tv_category
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.sub_category_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       holder.category.text = data[position].name

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(position, data[position].name)

            }
        }
    }
}



