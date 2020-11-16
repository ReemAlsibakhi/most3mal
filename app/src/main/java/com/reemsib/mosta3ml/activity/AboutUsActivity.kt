package com.reemsib.mosta3ml.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_about_us.avi
import kotlinx.android.synthetic.main.activity_about_us.btn_back
import org.json.JSONException
import org.json.JSONObject

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        Hawk.init(this).build();
        if(Hawk.contains(Constants.ABOUT)) {
            tv_about_us.text=Hawk.get(Constants.ABOUT)
        }
            getData()

        btn_back.setOnClickListener{finish()}


        }

    private fun getData() {
        avi.show()
        val stringRequest = object : StringRequest(Request.Method.GET, URLs.URL_SETTING, Response.Listener { response ->
            avi.hide()
            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {
                    var jsonArray=obj.getJSONArray("settings")
                    var jsonObject=jsonArray.getJSONObject(0)
                    tv_about_us.text=jsonObject.getString("about_us")

                    Hawk.put(Constants.ABOUT,jsonObject.getString("about_us"))

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
            ,
            Response.ErrorListener { error->
                avi.hide()
                Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
             //   map["token"]= Session.getToken()

                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


    }
}