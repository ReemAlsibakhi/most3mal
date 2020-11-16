package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import kotlinx.android.synthetic.main.image_advert_item.view.*

class ImageAdapter  (var activity: Activity, var data: ArrayList<Uri>) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, img: Uri)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAdvert = itemView.img_advert
        val btnDelete = itemView.btn_delete

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.image_advert_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.imgAdvert.setImageURI(data[position])

        holder.btnDelete.setOnClickListener {
            if (mListener != null) {
               mListener!!.onClicked(position, data[position])
            }
        }
    }
}