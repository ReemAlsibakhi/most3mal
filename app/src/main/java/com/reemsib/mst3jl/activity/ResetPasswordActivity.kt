package com.reemsib.mst3jl.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.et_email
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var myDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        myDialog=BaseActivity.loading(ResetPasswordActivity@this)

        btn_reset.setOnClickListener {
          val email=et_email.text.toString()
          when{
              email.isEmpty() -> {
                  et_email.error = getString(R.string.enter_email)
                  et_email.requestFocus()
              }
              !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                  et_email.error = getString(R.string.enter_valid_email)
                  et_email.requestFocus()
              }
             else ->{
                 Reset(email)
             } 
          }
            
        }


    }

    private fun Reset(email: String) {
        btn_reset.showLoading()
        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_RESET_PASSWORD, Response.Listener { response ->
         btn_reset.hideLoading()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        Toast.makeText(applicationContext, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            ,
            Response.ErrorListener {error->
                btn_reset.hideLoading()
                Toast.makeText(applicationContext,error.message, Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
               // map["token"]= Session.getToken()

                return map
            }


        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

}