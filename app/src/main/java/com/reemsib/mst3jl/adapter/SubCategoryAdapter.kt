package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.SubCategory
import kotlinx.android.synthetic.main.sub_category_item.view.*

class SubCategoryAdapter(var activity: Activity, var data: ArrayList<SubCategory>) :
    RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onClicked(position: Int,id:Int ,category: String)
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
       holder.itemView.setSelected(selectedPos == position);

        holder.itemView.setOnClickListener {
            if (selectedPos == holder.getAdapterPosition()) {
                selectedPos = RecyclerView.NO_POSITION;
                notifyDataSetChanged()
            }
            selectedPos =holder.getAdapterPosition();

            if (mListener != null) {
                mListener!!.onClicked(selectedPos,data[position].id, data[position].name)
            }
            notifyDataSetChanged();

            Log.e("pos_sub_categor","$selectedPos")

        }
    }
}



