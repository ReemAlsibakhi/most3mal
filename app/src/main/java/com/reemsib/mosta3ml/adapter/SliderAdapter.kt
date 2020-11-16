package com.maha.motaml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reemsib.mosta3ml.R
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.detail_slider_item.view.*

//
//class SliderAdapter(var activity: Activity, var sliderItems: ArrayList<AdvertDetailSlider>):
//    SliderViewAdapter<SliderAdapter.MyViewHolder>(){
//    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
//        val tvimg = itemView.image
//
//
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
//
//        val inflate: View = LayoutInflater.from(parent!!.context)
//            .inflate(R.layout.detail_slider_item, null)
//        return MyViewHolder(inflate)
//    }
//
//    override fun getCount(): Int {
//        return sliderItems.size
//    }
//
//    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
//        viewHolder!!.tvimg.setImageResource(sliderItems[position].imag)
//
//    }
//}