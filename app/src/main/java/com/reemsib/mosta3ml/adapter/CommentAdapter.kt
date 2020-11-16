package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.EditCommentActivity
import com.reemsib.mosta3ml.model.Reviews
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.comment_item_right.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(var activity: Activity, var data: ArrayList<Reviews>) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
 //   var mListener: OnItemDeleteListener? = null
    var mInterface: OnItemEditListener? = null

//    interface OnItemDeleteListener {
//        fun onClicked(clickedItemPosition: Int,activity: Activity,id:Int ,comment: String)
//    }
    interface OnItemEditListener {
        fun onClicked(clickedItemPosition: Int,activity: Activity,id:Int ,comment: String)
    }

//    fun setOnItemClickListener(listener: OnItemDeleteListener) {
//        mListener = listener
//    }
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

        holder.publisher.text = data[position].user.name
        holder.comment.text = data[position].content
        holder.date.text = getFormatDate(data[position].created_at)
        holder.rate.rating = data[position].rate.toFloat()
        holder.edit.setOnClickListener {
            if (mInterface != null) {
                mInterface!!.onClicked(position, activity, data[position].id, data[position].content)
            }}

//            holder.delete.setOnClickListener {
//                if (mListener != null) {
//                    mListener!!.onClicked(
//                        position,
//                        activity,
//                        data[position].id,
//                        data[position].content
//                    )
//                }
//            }

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
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                return params
            }


            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(activity).getToken()
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



