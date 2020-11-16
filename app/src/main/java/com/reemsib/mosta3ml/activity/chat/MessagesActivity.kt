package com.reemsib.mosta3ml.activity.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.ChatsAdapter
import com.reemsib.mosta3ml.model.ChatRoom
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.activity_messages.*
import org.json.JSONException
import org.json.JSONObject


class MessagesActivity : AppCompatActivity() {
  var messagesList=ArrayList<ChatRoom>()
  var messagesAdapter:ChatsAdapter ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
      Hawk.init(this).build()
      if(Hawk.contains(Constants.CHATS)){
        messagesAdapter = ChatsAdapter(this, Hawk.get(Constants.CHATS))
        buildChatsRecy()

      }

      getMessages()

      }

  private fun getMessages() {
    avi.show()
    val stringRequest = object : StringRequest(Method.GET, URLs.URL_USER_CHAT, Response.Listener { response ->
        avi.hide()
        try {
          val obj = JSONObject(response)

          if (obj.getBoolean("status")) {

            val jsonArray = obj.getJSONArray("items")

            for (i in 0 until jsonArray.length()) {

              val jsObj = jsonArray.getJSONObject(i)
              val mJson = JsonParser().parse(jsObj.toString())
              val chats: ChatRoom = Gson().fromJson<Any>(mJson, ChatRoom::class.java) as ChatRoom
              messagesList.add(chats)
            }
            Hawk.put(Constants.CHATS, messagesList)
            messagesAdapter = ChatsAdapter(this, messagesList)
            buildChatsRecy()
          }
        } catch (e: JSONException) {
          e.printStackTrace()
        }
      },
      Response.ErrorListener { error ->
        avi.hide()
       // Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
        Toast.makeText(applicationContext,error.message, Toast.LENGTH_SHORT).show()
      }) {

      override fun getHeaders(): MutableMap<String, String> {
        val map = HashMap<String, String>()
        map["Accept"] = "application/json"
        map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()
        return map
      }


    }
    MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

  }

  private fun buildChatsRecy() {
    rv_messages.adapter=messagesAdapter
    rv_messages.layoutManager=LinearLayoutManager(this)
    rv_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    messagesAdapter!!.notifyDataSetChanged()

    messagesAdapter!!.setOnItemClickListener(object :ChatsAdapter.OnItemClickListener{
      override fun onClicked(clickedItemPosition: Int, chat_id: Int,userId:Int,name:String) {
        val i =Intent(applicationContext,ChatRoomActivity::class.java)
        i.putExtra(Constants.CHAT_ID,chat_id)
        i.putExtra(Constants.ADVERTER_NAME,name)
     //   i.putExtra(Constants.USER1_ID,userId)
        startActivity(i)
      }

    })
  }
}
