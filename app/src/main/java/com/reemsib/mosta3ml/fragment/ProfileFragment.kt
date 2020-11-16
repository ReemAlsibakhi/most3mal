package com.reemsib.mosta3ml.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.LoginActivity
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.json.JSONException
import org.json.JSONObject

class ProfileFragment : Fragment() ,View.OnClickListener{
  var mUsername:TextView?=null
  var mMobile:TextView?=null
  var mEmail:TextView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        mUsername = root.findViewById(R.id.tv_username) as TextView
        mMobile = root.findViewById(R.id.tv_mobile) as TextView
        mEmail = root.findViewById(R.id.tv_email) as TextView

        if(!MySession.getInstance(requireContext()).isLoggedIn()){
            root.linear_login.visibility=View.VISIBLE
        }
        else{
            root.linear_profile.visibility=View.VISIBLE

            if(Hawk.contains(Constants.USERNAME)){
                root.tv_username.text=Hawk.get(Constants.USERNAME)
                root.tv_email.text=Hawk.get(Constants.EMAIL)
                root.tv_mobile.text=Hawk.get(Constants.MOBILE)
            }

            getUserInfo()
        }

        root.tv_login.setOnClickListener(this)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if(!MySession.getInstance(requireContext()).isLoggedIn()){
//            linear_login.visibility=View.VISIBLE
//        }
//        else{
//            linear_profile.visibility=View.VISIBLE
//
//            if(Hawk.contains(Constants.USERNAME)){
//               tv_username.text=Hawk.get(Constants.USERNAME)
//                tv_email.text=Hawk.get(Constants.EMAIL)
//                tv_mobile.text=Hawk.get(Constants.MOBILE)
//            }
//
//            getUserInfo()
//        }
//        tv_login.setOnClickListener(this)
    }
    private fun getUserInfo() {
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_USER_INFO, Response.Listener { response ->
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val user = obj.getJSONObject("user")
                        val country = user.getJSONObject("country")

                        val username = user.getString("name")
                        val email = user.getString("email")
                        val mobile = user.getString("mobile")
                        val callingCode = country.getString("calling_code")


                        Hawk.put(Constants.USERNAME, username)
                        Hawk.put(Constants.MOBILE, callingCode + mobile)
                        Hawk.put(Constants.EMAIL, email)

                        if(username.isNotEmpty()|| email.isNotEmpty() ){
                            Log.e("name,email","$username,$email")
                            mUsername!!.text = username
                            mEmail!!.text = email
                          //  mMobile!!.text = callingCode + mobile
                        }



                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(requireActivity()).getToken()

                return map
            }


        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

    }

    override fun onClick(v: View?) {
       when(v!!.id){
          R.id.tv_login ->{startActivity(Intent(requireContext(),LoginActivity::class.java))}
       }
    }


}