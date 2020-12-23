package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.AllCategory
import com.reemsib.mst3jl.model.SubCategory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.all_category_item.view.*

class AllCategoryAdapter (var activity: Activity, var data: ArrayList<AllCategory>):
    RecyclerView.Adapter<AllCategoryAdapter.MyViewHolder>() {

    var mSubCatAdapter: SubCategAdapter? = null
    var mSubCatList:ArrayList<SubCategory> ?=null
    var mListener: OnItemClickListener? = null

    init {
        mSubCatList=ArrayList()
    }
    interface OnItemClickListener {
        fun onClicked(position: Int, id: Int, title: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.img_cat!!
        val mainCat = itemView.tv_mainCat!!
        val rvSubCat = itemView.rv_subCat!!
        val linearCate = itemView.linear_categ!!
//        val expanSubcateg= itemView.expan_subCateg!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.all_category_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Picasso.get().load(data[position].image).into(holder.image)
        holder.mainCat.text = data[position].name

        mSubCatList=data[position].sub_categories

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        holder.rvSubCat.layoutManager = llm
        mSubCatAdapter = SubCategAdapter(activity, mSubCatList!!)
        holder.rvSubCat.adapter = mSubCatAdapter
        holder.itemView.setOnClickListener{
            if (holder.rvSubCat.visibility==View.GONE){
                holder.rvSubCat.visibility=View.VISIBLE
            }else{
                holder.rvSubCat.visibility=View.GONE

            }
        }
//        holder.linearCate.setOnClickListener {
//            holder.expanSubcateg.toggle()
//        }
        mSubCatAdapter!!.setOnItemClickListener(object :SubCategAdapter.OnItemClickListener{
            override fun onClicked(clickedItemPosition: Int, id: Int, category: String) {
                if (mListener != null) {
                mListener!!.onClicked(position , id ,category)
                }
            }
        })
//        holder.itemView.setOnClickListener {
//            if (mListener != null) {
//                mListener!!.onClicked(position, data[position].id.toString(), data[position].name,data[position].sub_categories)
//            }
//        }
    }
}
