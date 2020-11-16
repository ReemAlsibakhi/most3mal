package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.AdvertImage
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.view_image_modify_item.view.*


class SliderModifyAdapter(var activity: Activity, var sliderItems: ArrayList<AdvertImage>):
    SliderViewAdapter<SliderModifyAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvimg = itemView.image


    }


    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {

        val inflate: View = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.view_image_modify_item, null)
        return MyViewHolder(inflate)
    }

    override fun getCount(): Int {
        return sliderItems.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
     //   viewHolder!!.tvimg.setImageResource(sliderItems[position].image)

    }
}