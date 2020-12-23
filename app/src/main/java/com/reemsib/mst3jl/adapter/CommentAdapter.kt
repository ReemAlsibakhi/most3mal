package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Reviews
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.comment_item_right.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class CommentAdapter(var activity: Activity, var data: ArrayList<Reviews>) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    var mInterface: OnItemEditListener? = null
    private var manager: PreferencesManager = PreferencesManager(activity)

    interface OnItemEditListener {
        fun onClicked(clickedItemPosition: Int, activity: Activity, reviews: Reviews)
    }
    fun setOnEditClickListener(listener: OnItemEditListener) {
        mInterface = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val publisher = itemView.tv_publisher
        val comment = itemView.tv_comment
        val date = itemView.tv_date
        val edit = itemView.tv_edit_comment
        val rate = itemView.rate
        val delete = itemView.tv_delete_comment

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(
            R.layout.comment_item_right,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       if(data[position].user!=null){
           holder.publisher.text = data[position].user.name
       }
        holder.comment.text = data[position].content
        holder.date.text = getFormatDate(data[position].created_at)
        holder.rate.rating = data[position].rate.toFloat()

        if (manager.isLoggedIn && (manager.getUser().id == data[position].user.id)){
            holder.delete.visibility=View.VISIBLE
            holder.edit.visibility=View.VISIBLE
        }else{
            holder.delete.visibility=View.GONE
            holder.edit.visibility=View.GONE
        }
        holder.edit.setOnClickListener {
            if (mInterface != null) {
                mInterface!!.onClicked(position, activity, data[position])
//                mInterface!!.onClicked(position,activity, data[position].id, data[position].content,data[position].user.name)
            }}
        holder.delete.setOnClickListener {
                showAlertDialog(position, data[position].id)

            }
        }

    private fun showAlertDialog(position: Int, id: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.deleteComment)
        builder.setPositiveButton("نعم"){ dialogInterface, which ->
            delete(position, id)
        }
        builder.setNeutralButton("إلغاء"){ dialogInterface, which ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun delete(position: Int, id: Int) {
        val stringRequest = object : StringRequest(Method.POST, URLs.URL_DELETE_REVIEW,
            Response.Listener { response ->
                Log.e("delete_comment", response.toString())
                Log.e("access_token", PreferencesManager(activity).getAccessToken())
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_LONG).show()
                        Log.e("id_comment", "$id")
                        data.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, data.size)
                        notifyDataSetChanged()
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
                params2.put("id", id.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"]="Bearer " + PreferencesManager(activity).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(activity).addToRequestQueue(stringRequest)

    }
 }
 private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }



