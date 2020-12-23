package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Report
import kotlinx.android.synthetic.main.report_item.view.*

class ReportAdapter(var activity: Activity, var data: ArrayList<Report>) :
    RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null
    private var selectedPos = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val report = itemView.tv_report
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.report_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       holder.report.text = data[position].report
        holder.itemView.setSelected(selectedPos == position);

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(position, data[position].id)
                if (selectedPos == holder.getAdapterPosition()) {
                    selectedPos = RecyclerView.NO_POSITION;
                    notifyDataSetChanged()
                }
                selectedPos =holder.getAdapterPosition();
                notifyDataSetChanged()

            }
        }
    }
}
//    fun filterList(filteredList: ArrayList<Drug>) {
//        data = filteredList
//        notifyDataSetChanged()
//    }


