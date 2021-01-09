package com.reemsib.mst3jl.activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.Login
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.activity_update_profile.et_email
import kotlinx.android.synthetic.main.activity_update_profile.et_mobile
import org.json.JSONException
import org.json.JSONObject

class UpdateProfileActivity : AppCompatActivity() ,View.OnClickListener{
    private lateinit var manager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        manager= PreferencesManager(this)
        et_email.setText(manager.getUser().email)
        et_mobile.setText(manager.getUser().mobile)
        btn_update.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_update->{updateProfile(et_email.text.toString(),et_mobile.text.toString())}

        }
    }

    private fun updateProfile(email: String, mobile: String) {
        if (!validForm(email,mobile)) {
            return
        }
        btn_update.showLoading()
        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_UPDATE_PROFILE, Response.Listener { response ->
            btn_update.hideLoading()
            Log.e("update_profile",response.toString())

            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {
                    Toast.makeText(applicationContext,obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    finish()

                } else {
                        Toast.makeText(applicationContext, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { error ->
                btn_update.hideLoading()
                Toast.makeText(applicationContext,getString(R.string.failed_internet), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["mobile"] = mobile
                params["email"] = email

                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + PreferencesManager(applicationContext).getAccessToken()
                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private  fun validForm(email: String, mobile: String): Boolean {
        var valid = true
        when {
            email.isEmpty() -> {
                et_email.error = getString(R.string.enter_email)
                et_email.requestFocus()
                valid = false

            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                et_email.error = getString(R.string.enter_valid_email)
                et_email.requestFocus()
                valid = false

            }
            mobile.isEmpty() -> {
            et_mobile.error=getString(R.string.enter_mobile)
            et_mobile.requestFocus()
            valid = false
            }
            mobile.length <9 ->{
                et_mobile.error=getString(R.string.enter_mobile_valid)
                et_mobile.requestFocus()
                valid = false
            }
        }
        return valid
    }

}