package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Country
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.country_item.view.*

class CountryAdapter(var activity: Activity, var data: ArrayList<Country>) :
    RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int , calling_code:String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val country = itemView.tv_country
        val abbrev = itemView.tv_abbrev
        val flag = itemView.img_flag
        val calling_code = itemView.tv_calling_code

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.country_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       holder.country.text = data[position].name
       holder.abbrev.text = data[position].iso_3166_1_alpha2
       Picasso.get().load(data[position].flag).into(holder.flag);
       holder.calling_code.text = data[position].calling_code

        holder.itemView.setOnClickListener {
            if (mListener != null) {
             mListener!!.onClicked(position, data[position].id,data[position].calling_code)
            }
        }
    }
    fun filterList(filteredList: ArrayList<Country>) {
        data = filteredList
        notifyDataSetChanged()
    }
}

