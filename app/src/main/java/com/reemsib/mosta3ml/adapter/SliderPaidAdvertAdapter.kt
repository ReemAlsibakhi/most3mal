package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.Advert
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.piad_advert_item.view.img_advert
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_date
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_price
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_title
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_views
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SliderPaidAdvertAdapter(var activity: Activity, var data: ArrayList<Advert>):
    SliderViewAdapter<SliderPaidAdvertAdapter.MyViewHolder>(){

    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val imgAdvert = itemView.img_advert
        val title = itemView.tv_title
        val price = itemView.tv_price
        val date = itemView.tv_date
        val view_num = itemView.tv_views

    }


    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {

        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.piad_advert_item, null)
            return MyViewHolder(inflate)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {

        Picasso.get().load(data[position].images[1].image).into(viewHolder!!.imgAdvert);
        viewHolder.title.text=data[position].title
        viewHolder.price.text=data[position].price.toString()
        viewHolder.view_num.text=data[position].view_number.toString()
        viewHolder.date.text=getFormatDate(data[position].created_at)

        viewHolder.itemView.setOnClickListener {
            if (mListener != null) {
                 mListener!!.onClicked(position, data[position].id)

            }

    }
}
    private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }
}