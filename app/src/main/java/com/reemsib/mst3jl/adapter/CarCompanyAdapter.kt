package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Company
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.company_item.view.*

class CarCompanyAdapter(var activity: Activity, var data: ArrayList<Company>) :
    RecyclerView.Adapter<CarCompanyAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onClicked(position: Int, id:Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val company = itemView.tv_company
        val imgCom = itemView.img_comp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.company_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.company.text = data[position].title
       Picasso.get().load(data[position].image).into(holder.imgCom)
        holder.itemView.setSelected(selectedPos == position);

        holder.itemView.setOnClickListener {
            if (selectedPos == holder.getAdapterPosition()) {
                selectedPos = RecyclerView.NO_POSITION;
                notifyDataSetChanged()
            }
            selectedPos =holder.getAdapterPosition();
            notifyDataSetChanged();

            if (mListener != null) {
                mListener!!.onClicked(selectedPos, data[position].id)
            }
        }
    }
}


