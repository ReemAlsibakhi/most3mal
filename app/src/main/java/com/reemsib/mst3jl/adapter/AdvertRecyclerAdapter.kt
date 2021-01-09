package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.advert_item.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdvertRecyclerAdapter(var activity: Activity, var data: ArrayList<Advert>,var page:String) :
    RecyclerView.Adapter<AdvertRecyclerAdapter.MyViewHolder>() {
    var mListener: OnItemClickListener?=null
    private lateinit var manager: PreferencesManager
    init {
        manager = PreferencesManager(activity)
    }
    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, id: Int)
        fun onLongClick(clickedItemPosition: Int, id: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_title
        val price = itemView.tv_price
        val date = itemView.tv_date
        val viewsNum = itemView.tv_views
        val btnSave = itemView.btn_save
        val paidImg = itemView.img_paid
        val advImg = itemView.img_advert
        val distance = itemView.tv_distance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.advert_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = data[position].title
    //    holder.price.text = data[position].price.toString()+"ريال"
        when(data[position].price_type){
            "fixed"->{  holder.price.text=data[position].price.toString()+activity.getString(R.string.rial)}
            "un_limited"->{ holder.price.text=activity.getString(R.string.not_detect)}
            "on_somme"->{holder.price.text=activity.getString(R.string.soom)}
        }

        holder.date.text = getFormatDate(data[position].created_at)
        holder.viewsNum.text = data[position].view_number.toString()
        holder.distance.text=data[position].distance.toString()+activity.getString(R.string.km)
        Picasso.get().load(data[position].images[0].image).into(holder.advImg);
        if (data[position].in_favorite){
           holder.btnSave.setImageResource(R.drawable.favorite)
        }else{
            holder.btnSave.setImageResource(R.drawable.save_outline)
        }
        holder.btnSave.setOnClickListener {
            if (manager.isLoggedIn){
                if (data[position].in_favorite){
                    holder.btnSave.setImageResource(R.drawable.save_outline)
                    deleteAdvert(data[position].id,position)
                }else{
                    holder.btnSave.setImageResource(R.drawable.favorite)
                    Log.e("save_Add", "${data[position].id},${manager.getUser().id}")
                    save(data[position].id, manager.getUser().id)
                }
            }else{
                Toast.makeText(activity,"من فضلك سجل دخول",Toast.LENGTH_LONG).show()
            }

        }
        val advType = data[position].adv_type
        if (advType == "paid") {
            holder.paidImg.visibility = View.VISIBLE
        } else {
            holder.paidImg.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(position, data[position].id)
            }
        }
        holder.itemView.setOnLongClickListener { view ->
            if (mListener != null) {
                mListener!!.onLongClick(position, data[position].id)
            }
            true
        }
    }
    fun filterList(filteredList: ArrayList<Advert>) {
        data = filteredList
        notifyDataSetChanged()
    }

    fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }
    private fun deleteAdvert(id: Int,position: Int) {
        val stringRequest = object : StringRequest(
            Method.POST, URLs.URL_DELETE_FAVORITE,
            Response.Listener { response ->
                Log.e("delete", response)
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_LONG).show()
                        if(page=="favorite"){
                        data.removeAt(position)
                        notifyDataSetChanged()
                        }

                        Log.e("delete_adv", obj.getString("message"))

                    } else {
                        Log.e("delete_failed", obj.getString("message"))
                        Toast.makeText(
                            activity,
                            obj.getString("message") + "error",
                            Toast.LENGTH_LONG
                        ).show()

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
                Toast.makeText(activity,activity.getString(R.string.failed_internet), Toast.LENGTH_SHORT).show()
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

}