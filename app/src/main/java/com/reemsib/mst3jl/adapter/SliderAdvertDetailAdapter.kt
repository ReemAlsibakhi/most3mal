package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.AdvertImage
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_slider_item.view.*


class SliderAdvertDetailAdapter(var activity: Activity, var data: ArrayList<AdvertImage>):
    SliderViewAdapter<SliderAdvertDetailAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val slider = itemView.img_slider
        val premiumImg=itemView.img_premium
        val viewsNum=itemView.tv_views
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {

        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.detail_slider_item, null)
            return MyViewHolder(inflate)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {

        Picasso.get().load(data[position].image).into(viewHolder!!.slider)
        viewHolder.viewsNum.text=data[position].view_number.toString()

         if(data[position].adv_type=="paid"){
             viewHolder.premiumImg.visibility=View.VISIBLE
         }else{
             viewHolder.premiumImg.visibility=View.GONE

         }


    }
}
