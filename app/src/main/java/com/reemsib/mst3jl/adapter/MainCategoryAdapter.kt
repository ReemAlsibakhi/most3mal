package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.MainCategory
import com.squareup.picasso.Picasso

class MainCategoryAdapter(
    var activity: Activity,
    var data: ArrayList<MainCategory>,
    var pageType: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_IMAGE = 0
    private val VIEW_TYPE_ITEM = 1
    private var selectedPos = RecyclerView.NO_POSITION
    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int, name: String,has_models:Int)
     //   fun onClicked(clickedItemPosition: Int, id: Int, name: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.sub_category_item,
                parent,
                false
            )
           ItemViewHolder(view)
        }
    }

    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView

        init {
            tvTitle = itemView.findViewById(R.id.tv_category)
        }
    }
    private class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var catImg: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tv_title)
            catImg = itemView.findViewById(R.id.img_category)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (pageType == "home") VIEW_TYPE_IMAGE else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ItemViewHolder) {
            populateItemCat(viewHolder as ItemViewHolder, position)
        } else if (viewHolder is ImageViewHolder) {
            populateCatImage(viewHolder as ImageViewHolder, position)
        }

    }

    private fun populateCatImage(viewHolder: ImageViewHolder, position: Int) {
        viewHolder.tvTitle.text = data[position].name
        if(data[position].image=="all"){
        }else{
            Picasso.get().load(data[position].image).into(viewHolder.catImg);
        }
        viewHolder.itemView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(
                    position,
                    data[position].id,
                    data[position].name,
                    data[position].has_models
                )
            }
        }
    }
    private fun populateItemCat(viewHolder: ItemViewHolder, position: Int) {
        viewHolder.tvTitle.text = data[position].name
//        viewHolder.itemView.setSelected(if (viewHolder.itemView.isSelected()) true else false)
        viewHolder.itemView.setSelected(selectedPos == position);

        viewHolder.itemView.setOnClickListener {
            if (selectedPos == viewHolder.getAdapterPosition()) {
                selectedPos = RecyclerView.NO_POSITION;
                notifyDataSetChanged()
            //    return;
            }
            selectedPos =viewHolder.getAdapterPosition();
            notifyDataSetChanged();

            if (mListener != null) {
                mListener!!.onClicked(selectedPos, data[position].id, data[position].name,data[position].has_models)
            }

//            notifyItemChanged(selectedPos);
//            selectedPos = viewHolder.getLayoutPosition();
//            notifyItemChanged(selectedPos);
            Log.e("selectedPos","$selectedPos")
        }

    }

    fun filterList(filteredList: ArrayList<MainCategory>) {
        data = filteredList
        notifyDataSetChanged()
    }
}
