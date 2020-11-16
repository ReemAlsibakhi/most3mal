package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.AdvertDetailActivity
import com.reemsib.mosta3ml.activity.MainActivity
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.advert_item.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class AdvertAdapter(var activity: Activity, var data: ArrayList<Advert>) : BaseAdapter() {

    var mListener: OnItemClickListener? = null
    var listFilter=ArrayList<Advert>()

    init {
        listFilter=data
    }
    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int)
        fun onLongClick(clickedItemPosition: Int, id: Int)
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
        val view = inflator.inflate(R.layout.advert_item, null)

        view.tv_title.text = data[position].title
        view.tv_price.text = data[position].price.toString()
        view.tv_views.text = data[position].view_number.toString()
        view.tv_date.text =getFormatDate(data[position].created_at)
        Picasso.get().load(data[position].images[1].image).into(view.img_advert);

        view.btn_save.setOnClickListener {
            Log.e("save_Add","${data[position].id},${MySession.getInstance(activity).getUserId()}")
            save(data[position].id,MySession.getInstance(activity).getUserId())
        }
        val advType=data[position].adv_type
        if(advType=="paid"){
            view.img_paid.visibility=View.VISIBLE
        }else{
            view.img_paid.visibility=View.GONE
        }

        view.setOnClickListener {
            if (mListener != null) {
              mListener!!.onClicked(position, data[position].id)

            }
        }
       view.setOnLongClickListener {view ->
           if (mListener != null) {
               mListener!!.onLongClick(position, data[position].id)

           }
           true
        }
        return view
    }

    private fun save(id: Int, userId: Int) {
        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_ADD_FAVORITE, Response.Listener { response ->
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                  //      Toast.makeText(activity, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show()
                        Log.e("save","$id,$userId")

                        Toast.makeText(activity, obj.getString("message")+"true", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, obj.getString("message")+"false", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error.message+"", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                params["advertisement_id"] = id.toString()

                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(activity).getToken()
   //             map["Authorization"]="Bearer " + "4|cUPgLCE1r9x4L6xXXO6Hl1Tx0BO5FqouZClI2xGI"

                return map
            }

        }

        MySingleton.getInstance(activity).addToRequestQueue(stringRequest)

    }

    private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }

    fun filterList(filteredList: ArrayList<Advert>) {
        data = filteredList
        notifyDataSetChanged()
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val filterResults = FilterResults()
//                val charSearch = constraint.toString().toLowerCase()
//                if (charSearch.isEmpty()) {
//                   filterResults.count=listFilter.size
//                   filterResults.values=listFilter
//               //     listFilter = data
//                }
//                else {
//                    val resultList = ArrayList<Advert>()
//                    for (row in listFilter) {
//                        if (row.title.contains(charSearch.toLowerCase(Locale.ROOT))) {
//                            resultList.add(row)
//                        }
//                    }
//                    filterResults.values = resultList
//                    filterResults.count=resultList.size
//              //      listFilter = resultList
//                }
//             //   val filterResults = FilterResults()
//          //      filterResults.values = listFilter.size
//            //    filterResults.count=
//                return filterResults
//            }
//
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                data = results?.values as ArrayList<Advert>
//                notifyDataSetChanged()
//            }
//
//        }
    //}
}

