package com.reemsib.mosta3ml.activity.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.ThreadAdapter
import com.reemsib.mosta3ml.fcm.MyFirebaseMessagingService
import com.reemsib.mosta3ml.model.ChatMessage
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.activity_chat_log.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatRoomActivity : AppCompatActivity() {

    var adverterId:String ?=null
    var chatId :Int      ?= null
    var user1Id :Int      ?= null
    var adverterName:String ?=null
    val chatRoomList = ArrayList<ChatMessage>()
    var mChatRoomAdapter:ThreadAdapter ?=null
    var active = false
    var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Hawk.init(this).build()
       val i = intent
        if(i !=null){
            adverterId=i.getStringExtra(Constants.ADVERTER_ID)
            adverterName=i.getStringExtra(Constants.ADVERTER_NAME)
            chatId=i.getIntExtra(Constants.CHAT_ID, -1)
       }
        tv_userName.text=adverterName

        user1Id= MySession.getInstance(applicationContext).getUserId()

        rv_chatLog.setHasFixedSize(true)
        rv_chatLog.layoutManager = LinearLayoutManager(this)
        showChatInfo(chatId!!)

        btn_send.setOnClickListener {
          val message=et_message.text.toString()
            if (message.isNotEmpty()){
                Log.e("chat_log", "$user1Id,$chatId, $message,$adverterId")
                sendMessage(chatId!!, message)
            }
        }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    Log.d("message is here", "onReceive: ")
                    if(active){
                        showChatInfo(chatId!!)

                    }else{
                       MyFirebaseMessagingService().showNotificationMessage("123","33444")
                    }
                }
            }
        }

    }
    private fun showChatInfo(chatId: Int) {
        val stringRequest = object : StringRequest(Request.Method.GET, URLs.URL_CHAT_INFO + chatId,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val chatObj = obj.getJSONObject("chat")
                        val created_at = chatObj.getString("created_at")

                        chat_created_at.text =  getFormatDate(created_at)

                        val msgArray = chatObj.getJSONArray("messages")
                        for (i in 0 until msgArray.length()) {
                           val msgobj=msgArray.getJSONObject(i)
//                            val userId = msgobj.getInt("user_id")
//                            val message = msgobj.getString("message")
//                            val sentAt = msgobj.getString("created_at")
                        //    val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(msgobj.toString())
                            val msgs = Gson().fromJson<Any>(mJson, ChatMessage::class.java) as ChatMessage

                      //      val messagObject = Message(userId, message, sentAt)
                            chatRoomList.add(msgs)
                            Log.e("chat","$chatRoomList")
                        }
                        mChatRoomAdapter = ThreadAdapter(this, chatRoomList, user1Id!!)
                        rv_chatLog.adapter = mChatRoomAdapter
                        scrollToBottom()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //  Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

    private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }

    private fun sendMessage(chatId: Int, message: String) {
        val sentAt = getTimeStamp()

        val m = ChatMessage(user1Id!!, chatId,message, "",sentAt!!)
        chatRoomList.add(m)
        mChatRoomAdapter = ThreadAdapter(this, chatRoomList, user1Id!!)
        mChatRoomAdapter!!.notifyDataSetChanged()
        scrollToBottom()
        et_message.setText("")

        val stringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_SEND_MESSAGE,
            Response.Listener { response ->

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonObj = obj.getJSONObject("message")

                        val userId = jsonObj.getInt("user_id")
                        Log.e("user_id_my","$user1Id, $userId")

                        Toast.makeText(this, "send msg", Toast.LENGTH_LONG).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //  Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["message"] =message
                params["chat_id"] =chatId.toString()

                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

                return map
            }
        }
        //Disabling retry to prevent duplicate messages

        //Disabling retry to prevent duplicate messages
        val socketTimeout = 0
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        stringRequest.retryPolicy = policy
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

    private fun scrollToBottom() {
        mChatRoomAdapter!!.notifyDataSetChanged()
        if (mChatRoomAdapter!!.itemCount > 1)
            rv_chatLog.layoutManager!!.smoothScrollToPosition(
                rv_chatLog,
                null,
                mChatRoomAdapter!!.itemCount - 1
            ) }


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

    override fun onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
  //      LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    //This method will return current timestamp
    fun getTimeStamp(): String? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return format.format(Date())
    }

}