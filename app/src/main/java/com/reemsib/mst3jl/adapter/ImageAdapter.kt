package com.reemsib.mst3jl.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Image
import kotlinx.android.synthetic.main.image_advert_item.view.*

class ImageAdapter(var activity: Context, var data: Any) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {
    var mListener: OnItemClickListener? = null

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

        val itemView =
            LayoutInflater.from(activity).inflate(R.layout.image_advert_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //  doListCompare(data)
        //  holder.imgAdvert.setImageURI(data!![position])
      //  demo(data)
        holder.btnDelete.setOnClickListener {
            if (mListener != null) {
                //       mListener!!.onClicked(position, data!![position])
            }
//        data.removeAt(position)
//        notifyItemRemoved(position);


        }
    }


}