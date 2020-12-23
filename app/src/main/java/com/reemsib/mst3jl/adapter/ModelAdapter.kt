package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.ModelYear
import kotlinx.android.synthetic.main.model_item.view.*

class ModelAdapter(var activity: Activity, var data: ArrayList<ModelYear>) :
    RecyclerView.Adapter<ModelAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onClicked(position: Int, id:Int,year: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val model = itemView.tv_model
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.model_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

      Log.e("model","${data[position]}")
//       if (data[position].year!=null){
           holder.model.text = data[position].year
//       }
        holder.itemView.setSelected(selectedPos == position);

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                if (selectedPos == holder.getAdapterPosition()) {
                    selectedPos = RecyclerView.NO_POSITION;
                    notifyDataSetChanged()
                }
                selectedPos =holder.getAdapterPosition();
                mListener!!.onClicked(position, data[position].id,data[position].year)
                notifyDataSetChanged();

            }
        }
    }
}



