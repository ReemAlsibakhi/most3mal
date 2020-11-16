package com.reemsib.mosta3ml.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.et_email
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
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
         CustomProgressDialog.getInstance(this).showDialog()

        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_RESET_PASS, Response.Listener { response ->

            CustomProgressDialog.getInstance(this).hideDialog()

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            ,
            Response.ErrorListener {error->
                CustomProgressDialog.dialog!!.dismiss()
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