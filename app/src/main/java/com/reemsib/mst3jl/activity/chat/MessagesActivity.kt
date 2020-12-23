package com.reemsib.mst3jl.activity.chat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ChatsAdapter
import com.reemsib.mst3jl.model.ChatRoom
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_messages.et_search
import kotlinx.android.synthetic.main.bell_white.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.json.JSONException
import org.json.JSONObject


class MessagesActivity : AppCompatActivity() {
    var messagesList=ArrayList<ChatRoom>()
    var messagesAdapter:ChatsAdapter ?=null
    lateinit var myDialog: AlertDialog
    private lateinit var manager:PreferencesManager
    var broadcastReceiver: BroadcastReceiver? = null
    var active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        Hawk.init(this).build()
        myDialog=BaseActivity.loading(MessagesActivity@ this)
        manager=PreferencesManager(applicationContext)
        manager.setChatsCount(0)
        getMessages()
        listenerEdit()
        if(Hawk.contains(Constants.CHATS)){
            messagesAdapter = ChatsAdapter(this, Hawk.get(Constants.CHATS))
            buildChatsRecy()
        }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    Log.e("type_notify_",intent.getIntExtra("type",-1).toString())
                    if(active){
                        Log.e("type_notify",intent.getIntExtra("type",-1).toString())
                        if(intent.getIntExtra("type",-1)==3){
                           messagesList.clear()
                           getMessages()
                        }
                    }
                }
            }
        }

      }
    private fun listenerEdit() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                Log.e("filter_edit", s.toString())
                filter(s.toString())
            }
        })
    }
    private fun getMessages() {
        showDia()
        messagesList.clear()
     val stringRequest = object : StringRequest(Method.GET,
         URLs.URL_USER_CHAT,
         Response.Listener { response ->
             Log.e("all_msgs", response)
             finishDia()
             try {
                 val obj = JSONObject(response)

                 if (obj.getBoolean("status")) {
                     val jsonArray = obj.getJSONArray("items")
                     for (i in 0 until jsonArray.length()) {
                         val jsObj = jsonArray.getJSONObject(i)
                         val mJson = JsonParser().parse(jsObj.toString())
                         val chats: ChatRoom =
                             Gson().fromJson<Any>(mJson, ChatRoom::class.java) as ChatRoom
                         messagesList.add(chats)
                     }
                     messagesAdapter = ChatsAdapter(this, messagesList)
                     buildChatsRecy()
                     messagesAdapter!!.notifyDataSetChanged()
                     Hawk.put(Constants.CHATS, messagesList)

                 }
             } catch (e: JSONException) {
                 e.printStackTrace()
             }
         },
         Response.ErrorListener { error ->
            finishDia()
             // Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
             Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
         }) {

      override fun getHeaders(): MutableMap<String, String> {
        val map = HashMap<String, String>()
        map["Accept"] = "application/json"
        map["Authorization"]="Bearer " + PreferencesManager(applicationContext).getAccessToken()
        return map
      }
    }
    MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
  }
    private fun buildChatsRecy() {
      rv_messages.layoutManager=LinearLayoutManager(this)
      rv_messages.adapter=messagesAdapter
       messagesAdapter!!.setOnItemClickListener(object : ChatsAdapter.OnItemClickListener {
           override fun onClicked(
               clickedItemPosition: Int,
               chat_id: Int,
               userId: Int,
               name: String
           ) {
               val i = Intent(applicationContext, ChatRoomActivity::class.java)
               i.putExtra(Constants.CHAT_ID, chat_id)
               i.putExtra(Constants.ADVERTER_NAME, name)
               startActivityForResult(i, 1)
           }

       })
  }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        val back_chat = data!!.getStringExtra("back_chat")
         if (back_chat=="1"){
           Log.e("back", back_chat)
       messagesList.clear()
       getMessages()
         }
      }
      if (resultCode == Activity.RESULT_CANCELED) {
        //Write your code if there's no result
      }
    }
  }
    private fun filter(text: String) {
        if (Hawk.contains(Constants.CHATS)){
            val filteredList: ArrayList<ChatRoom> = ArrayList()
            for ( item : ChatRoom in Hawk.get(Constants.CHATS) as ArrayList<ChatRoom> ) {
                if (item.user.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
                messagesAdapter!!.filterList(filteredList)
            }
        }else{
            val filteredList1: ArrayList<ChatRoom> = ArrayList()
            for ( item : ChatRoom in messagesList ) {
                if (item.user.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList1.add(item) }
                messagesAdapter!!.filterList(filteredList1)
            }
        }
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
