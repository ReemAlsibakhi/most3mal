package com.reemsib.mst3jl.activity.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ThreadAdapter
import com.reemsib.mst3jl.model.ChatInfo
import com.reemsib.mst3jl.model.Message
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_chat_log.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatRoomActivity : AppCompatActivity(), View.OnClickListener {

//    var adverterId:String ?=null
    var chatId :Int ?= null
    var user1Id :Int  ?= null
    var adverterName:String ?=null
    var chatRoomList = ArrayList<Message>()
    var mChatRoomAdapter:ThreadAdapter ?=null
    var active = false
    var broadcastReceiver: BroadcastReceiver? = null
    var isSendMsg:Boolean=false
    private lateinit var manager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        manager= PreferencesManager(applicationContext)
        Hawk.init(this).build()
        val i = intent
        if(i !=null){
            adverterName=i.getStringExtra(Constants.ADVERTER_NAME)
            chatId=i.getIntExtra(Constants.CHAT_ID, -1)
       }
        tv_userName.text=adverterName
        user1Id= manager.getUser().id

        if (Hawk.contains(Constants.CHATROOM)){
            mChatRoomAdapter = ThreadAdapter(this, Hawk.get(Constants.CHATROOM),user1Id!!)
            scrollToBottom()
        }
        showChatInfo(chatId!!)
        btn_back.setOnClickListener(this)
        btn_send.setOnClickListener(this)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    if(active){
                        if (chatId==intent.getIntExtra("chat id",-1)){
                            showChatInfo(chatId!!)
                        }
                    }
                }
            }
        }

    }
    override fun onClick(p0: View?) {
       when(p0!!.id){
           R.id.btn_back->{
               finish()
               isUpdateMsgs()
           }
           R.id.btn_send ->{
               val message=et_message.text.toString()
               if (message.isNotEmpty()){
                   Log.e("chat_log", "$user1Id,$chatId, $message")
                   sendMessage(chatId!!, message)
               }else{
                   et_message.error=getString(R.string.write_msg)
               }
           }
       }
    }

    private fun isUpdateMsgs() {
        if (isSendMsg){
            val resultIntent = Intent()
            resultIntent.putExtra("back_chat", "1")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
}
    override fun onBackPressed() {
        isUpdateMsgs()
        super.onBackPressed()
    }

    private fun showChatInfo(chatId: Int) {
        val stringRequest = object : StringRequest(Request.Method.GET, URLs.URL_CHAT_INFO + chatId,
            Response.Listener { response ->
             Log.e("chat_info",response)
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val chatObj = obj.getJSONObject("chat")
                        val mJson = JsonParser().parse(chatObj.toString())
                        val chatInfo = Gson().fromJson<Any>(mJson, ChatInfo::class.java) as ChatInfo
                        chat_created_at.text=  getFormatDate(chatInfo.created_at)
                        tv_userName.text=chatInfo.user2.name
                        chatRoomList=chatInfo.messages
                        Hawk.put(Constants.CHATROOM, chatRoomList)
                        mChatRoomAdapter = ThreadAdapter(this, chatRoomList, user1Id!!)
                        scrollToBottom()
                        mChatRoomAdapter!!.notifyDataSetChanged()

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
                map["Authorization"]="Bearer " + manager.getAccessToken()

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

        val m = Message(0,user1Id!!,chatId,message,sentAt!!)
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
                        isSendMsg=true
                    //    Toast.makeText(this, "send msg", Toast.LENGTH_LONG).show()
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
                map["Authorization"]="Bearer " + manager.getAccessToken()

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
        rv_chatLog.setHasFixedSize(true)
        rv_chatLog.layoutManager = LinearLayoutManager(this)
        rv_chatLog.adapter = mChatRoomAdapter
       rv_chatLog.scrollToPosition(chatRoomList.size-1)
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