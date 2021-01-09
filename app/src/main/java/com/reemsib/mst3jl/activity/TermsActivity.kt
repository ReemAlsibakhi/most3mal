package com.reemsib.mst3jl.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.activity_politics.*
import kotlinx.android.synthetic.main.activity_politics.btn_back
import org.json.JSONException
import org.json.JSONObject

class TermsActivity : AppCompatActivity() {
    lateinit var myDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politics)

        Hawk.init(this).build();
        myDialog=BaseActivity.loading(TermsActivity@this)
        if(Hawk.contains(Constants.TERMS)) {
            tv_condition_term.text= Hawk.get(Constants.TERMS)
        }
        getTerms()
        btn_back.setOnClickListener{finish()}
    }
    private fun getTerms() {
       showDia()
        val stringRequest = object : StringRequest(Request.Method.GET, URLs.URL_SETTING, Response.Listener { response ->
           finishDia()
            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {

                    val jsonArray=obj.getJSONArray("settings")
                    val jsonObject=jsonArray.getJSONObject(1)
                    tv_condition_term.text=jsonObject.getString("condition_and_term")
                    Hawk.put(Constants.TERMS,jsonObject.getString("condition_and_term"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
            ,
            Response.ErrorListener { error->
                finishDia()
                Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
            //    map["token"]= Session.getToken()

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