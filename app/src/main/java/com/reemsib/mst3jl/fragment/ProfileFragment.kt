package com.reemsib.mst3jl.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.activity.LoginActivity
import com.reemsib.mst3jl.activity.UpdateProfileActivity
import com.reemsib.mst3jl.model.Login
import com.reemsib.mst3jl.model.User
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.json.JSONException
import org.json.JSONObject

class ProfileFragment : Fragment() ,View.OnClickListener{
  var mUsername:TextView?=null
  var mMobile:TextView?=null
  var mEmail:TextView?=null
  var mLogin:TextView?=null
  var btnUpdate: Button?=null
    private lateinit var manager: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        manager= PreferencesManager(requireContext())
        mUsername = root.findViewById<TextView>(R.id.tv_username) as TextView
        mMobile = root.findViewById(R.id.tv_mobile) as TextView
        mEmail = root.findViewById(R.id.tv_email) as TextView
        mLogin=root.findViewById(R.id.tv_login) as TextView
        btnUpdate=root.findViewById(R.id.btn_updateData) as Button
        if(!manager.isLoggedIn){
            root.linear_login.visibility=View.VISIBLE
        } else{
            root.linear_profile.visibility=View.VISIBLE
            root.tv_username.text=manager.getUser().name
            root.tv_email.text=manager.getUser().email
            root.tv_mobile.text=manager.getUser().mobile
            getUserInfo()
        }
        mLogin!!.setOnClickListener(this)
        btnUpdate!!.setOnClickListener(this)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun getUserInfo() {
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_USER_INFO, Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Log.e("profile_data",response.toString())
                    if (obj.getBoolean("status")) {
                        val userObj=obj.getJSONObject("user")
                        val mJson = JsonParser().parse(userObj.toString())
                        val user = Gson().fromJson<Any>(mJson, User::class.java) as User
                        Log.e("profile", "$user")
                        manager.setUser(user)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
       //        Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["accept"] = "application/json"
                map["Authorization"]="Bearer " + manager.getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

    }

    override fun onClick(v: View?) {
       when(v!!.id){
          R.id.tv_login ->{startActivity(Intent(requireContext(),LoginActivity::class.java))}
          R.id.btn_updateData ->{startActivity(Intent(requireContext(),UpdateProfileActivity::class.java))}
       }
    }


}