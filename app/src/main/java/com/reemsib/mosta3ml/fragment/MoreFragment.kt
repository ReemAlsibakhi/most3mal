package com.reemsib.mosta3ml.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.AboutUsActivity
import com.reemsib.mosta3ml.activity.LoginActivity
import com.reemsib.mosta3ml.activity.MyAdsActivity
import com.reemsib.mosta3ml.activity.chat.MessagesActivity
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.fragment_more.view.*
import org.json.JSONException
import org.json.JSONObject

class MoreFragment : Fragment() ,View.OnClickListener{


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v= inflater.inflate(R.layout.fragment_more, container, false)
            v.relative_about.setOnClickListener(this)
            v.relative_logout.setOnClickListener(this)
            v.relative_msgs.setOnClickListener(this)
            v.relative_ads.setOnClickListener(this)

          if(MySession.getInstance(requireContext()).isLoggedIn()){
              v.relative_logout.visibility=View.VISIBLE
          }else{
              v.relative_logout.visibility=View.GONE
          }

        return v

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
             if(MySession.getInstance(requireContext()).isLoggedIn()){
                 startActivity(Intent(requireContext(),MessagesActivity::class.java))
             }else{
                 startActivity(Intent(requireContext(),LoginActivity::class.java))
             }
           }
           R.id.relative_ads ->{
               if(MySession.getInstance(requireContext()).isLoggedIn()){
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

                        MySession.getInstance(requireContext()).Logout()

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

                Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT).show()

            }) {


            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(requireContext()).getToken()
                return map
            }

        }

        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

    }

}