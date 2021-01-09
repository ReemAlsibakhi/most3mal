package com.reemsib.mst3jl.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.NotificationAdapter
import com.reemsib.mst3jl.model.Notification
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_notification_activtiy.*
import org.json.JSONException
import org.json.JSONObject

class NotificationActivtiy : AppCompatActivity() {
    var mNotiflList=ArrayList<Notification>()
    var mNotifAdapter: NotificationAdapter ?= null
    lateinit var myDialog: AlertDialog
    private lateinit var manager:PreferencesManager
    var broadcastReceiver: BroadcastReceiver? = null
    var active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_activtiy)
        Hawk.init(this).build();
        myDialog= BaseActivity.loading(AdvertDetailActivity@ this)
        manager=PreferencesManager(applicationContext)
        manager.setRevsCount(0)
        getNotification()
        if(Hawk.contains(Constants.NOTIFICATION_LIST)) {
            mNotifAdapter = NotificationAdapter(this,Hawk.get(Constants.NOTIFICATION_LIST))
            buildNotRecy()

        }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    Log.e("type_notify_",intent.getIntExtra("type",-1).toString())
                    if(active){
                        Log.e("type_notify",intent.getIntExtra("type",-1).toString())

                        if(intent.getIntExtra("type",-1)==2){
                            getNotification()
                        }
                    }
                }
            }
        }
    }
    private fun getNotification() {
        showDia()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_GET_NOTIFICATION,
            Response.Listener { response ->
                finishDia()
                Log.e("notification",response.toString())
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("notifications")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val notif: Notification = Gson().fromJson<Any>(mJson, Notification::class.java) as Notification
                            mNotiflList.add(notif)
                        }
                        mNotifAdapter = NotificationAdapter(this,mNotiflList)
                        buildNotRecy()
                        mNotifAdapter!!.notifyDataSetChanged()
                        Hawk.put(Constants.NOTIFICATION_LIST, mNotiflList)
                    }else{

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["accept"] = "application/json"
                map["Authorization"]="Bearer " + PreferencesManager(applicationContext).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }
    fun buildNotRecy(){
        rv_notifications.layoutManager = LinearLayoutManager(this)
        rv_notifications.adapter = mNotifAdapter
        mNotifAdapter!!.setOnItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun onClicked( id: String) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID, id.toInt())

                startActivity(i)
            }
        })
    }
    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("back_notify", 0)
        setResult(RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }
    fun finishDia() {
        if (myDialog.isShowing) {
            myDialog.dismiss()
        }
    }
    fun showDia() {
        if (!myDialog.isShowing) {
            myDialog.show()
        }
    }
    override fun onStart() {
        super.onStart()
        active = true
    }
    override fun onStop() {
        super.onStop()
        active = false
    }
    override fun onResume() {
        super.onResume()
        active = true

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver!!,
            IntentFilter("notify")
        )

    }
}