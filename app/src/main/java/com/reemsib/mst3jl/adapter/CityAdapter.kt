package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.City
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.city_item.view.*

class CityAdapter(var activity: Activity, var data: ArrayList<City>) :
    RecyclerView.Adapter<CityAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

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
       // holder.itemView.setSelected(selectedPos == position);
        Log.e("test","$selectedPos")
        holder.itemView.setOnClickListener {
//           if(selectedPos!=0){
//               if (selectedPos == holder.getAdapterPosition()) {
//                   selectedPos = RecyclerView.NO_POSITION;
//                   notifyDataSetChanged()
//               }
//           }
            selectedPos =holder.getAdapterPosition();
            Log.e("posintion_city","$selectedPos")
            if (mListener != null) {
             mListener!!.onClicked(selectedPos, data[position].id.toString(), data[position].title)
            }
           notifyDataSetChanged()

        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}

