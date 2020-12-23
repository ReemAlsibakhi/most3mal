package com.reemsib.mst3jl.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.activity.AboutUsActivity
import com.reemsib.mst3jl.activity.LoginActivity
import com.reemsib.mst3jl.activity.MyAdsActivity
import com.reemsib.mst3jl.activity.chat.MessagesActivity
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.bell_white.*
import kotlinx.android.synthetic.main.bell_white.badge_notification_sec
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import org.json.JSONException
import org.json.JSONObject

class MoreFragment : Fragment() ,View.OnClickListener{

    private lateinit var manager: PreferencesManager
    var broadcastReceiver: BroadcastReceiver? = null
    var active = false
   lateinit  var notiNum:TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        manager= PreferencesManager(requireContext())

        val v= inflater.inflate(R.layout.fragment_more, container, false)
            v.relative_about.setOnClickListener(this)
            v.relative_logout.setOnClickListener(this)
            v.relative_msgs.setOnClickListener(this)
            v.relative_ads.setOnClickListener(this)
            notiNum=v.findViewById(R.id.num_notification) as TextView

          if(manager.isLoggedIn){
              v.relative_logout.visibility=View.VISIBLE
              counter()
          }else{
              v.relative_logout.visibility=View.GONE
          }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    if(active){
                       counter()
                    }
                }
            }
        }

        return v
    }
    fun counter(){
        if (manager.getChatsCount()!=0){
            notiNum.visibility= View.VISIBLE
            notiNum.text=manager.getChatsCount().toString()
        }else{
            notiNum.visibility= View.GONE
        }
    }
    override fun onClick(v: View?) {
       when(v!!.id){
           R.id.relative_about -> {
               val i = Intent(requireContext(), AboutUsActivity::class.java)
               startActivity(i)
           }
           R.id.relative_logout -> {
               logout()
           }
           R.id.relative_msgs -> {
             if(manager.isLoggedIn){
                 startActivity(Intent(requireContext(),MessagesActivity::class.java))
             }else{
                 startActivity(Intent(requireContext(),LoginActivity::class.java))
             }
           }
           R.id.relative_ads ->{
               if(manager.isLoggedIn){
                   startActivity(Intent(requireContext(),MyAdsActivity::class.java))
               }else{
                   startActivity(Intent(requireContext(),LoginActivity::class.java))
               }
           }

       }
    }
    private fun logout() {

     //CustomProgressDialog.getInstance(requireContext()).showDialog()

        val stringRequest = object : StringRequest(Method.POST, URLs.URL_LOGOUT, Response.Listener { response ->

    //   CustomProgressDialog.getInstance(requireContext()).hideDialog()

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        manager.Logout()

                        Toast.makeText(requireContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

                       // val intent = Intent(requireContext(), LoginActivity::class.java)
                       // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                       // startActivity(intent)

                    } else {
                        Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->

          //      CustomProgressDialog.getInstance(requireContext()).hideDialog()

                Toast.makeText(requireContext(),error.message.toString(), Toast.LENGTH_SHORT).show()

            }) {


            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + manager.getAccessToken()
                return map
            }

        }

        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver!!,
            IntentFilter("notify")
        )

    }

}