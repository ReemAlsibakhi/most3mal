package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.MainCategory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_item.view.*

open class MainCategoryAdapter(var activity: Activity, var data: ArrayList<MainCategory>) :BaseAdapter() {

    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id:Int ,name: String,img:String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): Any {
      return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val inflator = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.category_item, null)

        view.tv_title.text = data[position].name
        Picasso.get().load(data[position].image).into(view.img_category);

        view.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(position,data[position].id ,data[position].name,data[position].image)

            }
        }
        return view
    }
}

//    fun filterList(filteredList: ArrayList<Drug>) {
//        data = filteredList
//        notifyDataSetChanged()
////    }