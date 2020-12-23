package com.reemsib.mst3jl.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_about_us.*
import org.json.JSONException
import org.json.JSONObject

class AboutUsActivity : AppCompatActivity() {
    lateinit var myDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        myDialog= BaseActivity.loading(AboutUsActivity@ this)
        Hawk.init(this).build();
        if(Hawk.contains(Constants.ABOUT)) {
            tv_about_us.text=Hawk.get(Constants.ABOUT)
        }
        getData()
        applicationContext.setTheme(R.style.AppTheme);

        btn_back.setOnClickListener{finish()}

    }

    private fun getData() {
     //   myDialog.show()
        showDia()
        val stringRequest = object : StringRequest(
            Request.Method.GET,
            URLs.URL_SETTING,
            Response.Listener { response ->
             //   myDialog.dismiss()
               finishDia()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        var jsonArray = obj.getJSONArray("settings")
                        var jsonObject = jsonArray.getJSONObject(0)
                        tv_about_us.text = jsonObject.getString("about_us")

                        Hawk.put(Constants.ABOUT, jsonObject.getString("about_us"))

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
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
}