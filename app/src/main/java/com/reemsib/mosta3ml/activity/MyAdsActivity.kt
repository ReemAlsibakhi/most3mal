package com.reemsib.mosta3ml.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.AdvertAdapter
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.activity_my_ads.*
import kotlinx.android.synthetic.main.activity_my_ads.avi
import org.json.JSONException
import org.json.JSONObject


class MyAdsActivity : AppCompatActivity() {
    val myAdsList = ArrayList<Advert>()
    var mMyAdsAdapter: AdvertAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)
        getMyAdvertisements()
    }

    private fun getMyAdvertisements() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_MY_ADVERT,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonArray = obj.getJSONArray("items")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val advert = Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                            myAdsList.add(advert)

                        }

                        //  Hawk.put(Constants.FREE_ADVERT,mAdvertList)
                        mMyAdsAdapter = AdvertAdapter(this, myAdsList)
                        buildAdvertGrid()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi.hide()
                Toast.makeText(this, error.message+"error listener", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)



    }

    private fun buildAdvertGrid() {
        grid_myAds.adapter=mMyAdsAdapter
   //     grid_myAds.isExpanded = true
        mMyAdsAdapter!!.setOnItemClickListener(object :AdvertAdapter.OnItemClickListener{
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID,id)
                startActivity(i)
            }

            override fun onLongClick(clickedItemPosition: Int, id: Int) {
                TODO("Not yet implemented")
            }

        })

    }
}