package com.reemsib.mosta3ml.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.CityAdapter
import com.reemsib.mosta3ml.model.City
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.activity_cities.*
import org.json.JSONException
import org.json.JSONObject


class CitiesActivity : AppCompatActivity() {

 var mCityList=ArrayList<City>()
 var mCityAdapter: CityAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        Hawk.init(this).build()

        if(Hawk.contains(Constants.CITIES)){
            mCityAdapter = CityAdapter(this, Hawk.get(Constants.CITIES))
            buildCityRecy()
        }
        getCities()
    }




    private fun getCities() {
          avi.show()

        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_CITIES,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonArray = obj.getJSONArray("cities")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val city: City = Gson().fromJson<Any>(mJson, City::class.java) as City

                            mCityList.add(city)
                        }

                        Hawk.put(Constants.CITIES, mCityList)

                        mCityAdapter = CityAdapter(this, mCityList)
                        buildCityRecy()
                        //   mCityAdapter!!.notifyDataSetChanged()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi.hide()
                Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT)
                    .show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                return map
            }


        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }
   fun buildCityRecy(){
       rv_city.adapter = mCityAdapter
       rv_city.layoutManager = LinearLayoutManager(this)

       mCityAdapter!!.setOnItemClickListener(object : CityAdapter.OnItemClickListener {
           override fun onClicked(clickedItemPosition: Int, id: String, title: String) {
               val resultIntent = Intent()
               resultIntent.putExtra("cityId", id)
               resultIntent.putExtra("city", title)
               setResult(RESULT_OK, resultIntent)
               finish()
           }
       })
   }
}