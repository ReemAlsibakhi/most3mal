package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.advert_item.view.*
import kotlinx.android.synthetic.main.piad_advert_item.view.*
import kotlinx.android.synthetic.main.piad_advert_item.view.img_advert
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_date
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_price
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_title
import kotlinx.android.synthetic.main.piad_advert_item.view.tv_views
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SliderPaidAdvertAdapter(var activity: Context, var data: ArrayList<Advert>):
    SliderViewAdapter<SliderPaidAdvertAdapter.MyViewHolder>(){

    var mListener: OnItemClickListener? = null
    private lateinit var manager: PreferencesManager
    init {
        manager = PreferencesManager(activity)
    }
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
        val save = itemView.img_save

    }
    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.piad_advert_item, null)
            return MyViewHolder(inflate) }

    override fun getCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {

        Picasso.get().load(data[position].images[1].image).into(viewHolder!!.imgAdvert);
        viewHolder.title.text=data[position].title
        viewHolder.price.text=data[position].price.toString()
        viewHolder.view_num.text=data[position].view_number.toString()
        viewHolder.date.text=getFormatDate(data[position].created_at)

        if (data[position].in_favorite){
            viewHolder.save.setImageResource(R.drawable.favorite)

        }else{
            viewHolder.save.setImageResource(R.drawable.save_outline)
        }
        viewHolder.save.setOnClickListener {
            if (data[position].in_favorite){
                viewHolder.save.setImageResource(R.drawable.save_outline)
                deleteAdvert(data[position].id)
            }else{
                if (manager.isLoggedIn){
                    viewHolder.save.setImageResource(R.drawable.favorite)
                    save(data[position].id,PreferencesManager(activity).getUser().id)
                }else{
                    Toast.makeText(activity,R.string.please_login,Toast.LENGTH_LONG).show()
                }

            }
        }
        viewHolder.itemView.setOnClickListener {
            if (mListener != null) {
                 mListener!!.onClicked(position, data[position].id)
            }
    }
}
    private fun deleteAdvert(id: Int) {
        val stringRequest = object : StringRequest(
            Method.POST, URLs.URL_DELETE_FAVORITE,
            Response.Listener { response ->
                Log.e("delete",response)
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        Toast.makeText(activity,obj.getString("message"), Toast.LENGTH_LONG).show()

                        Log.e("delete_adv",obj.getString("message"))

                    }else{
                        Log.e("delete_failed",obj.getString("message"))
                        Toast.makeText(activity,obj.getString("message")+"error", Toast.LENGTH_LONG).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error.message + "", Toast.LENGTH_SHORT).show()
            }) {
            override fun getPostBodyContentType(): String {
                return "application/json; charset=utf-8";

            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = HashMap<String, String>()
                params2.put("advertisement_id", id.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"] = "Bearer " + PreferencesManager(activity).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(activity).addToRequestQueue(stringRequest)

    }
    private fun save(id: Int, userId: Int) {
        val stringRequest = object : StringRequest(
            Method.POST, URLs.URL_ADD_FAVORITE,
            Response.Listener { response ->
                Log.e("save_advert", response.toString())
                Log.e("access_token", PreferencesManager(activity).getAccessToken())
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_LONG).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error.message + "", Toast.LENGTH_SHORT).show()
                Log.e("error_listener", error.message.toString())

            }) {
            override fun getPostBodyContentType(): String {
                return "application/json; charset=utf-8";
            }
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = HashMap<String, String>()
                params2.put("user_id", userId.toString())
                params2.put("advertisement_id", id.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"] = "Bearer " + PreferencesManager(activity).getAccessToken()
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
}