package com.reemsib.mst3jl.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.CityAdapter
import com.reemsib.mst3jl.model.City
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.activity_cities.*
import org.json.JSONException
import org.json.JSONObject
import java.text.FieldPosition


class CitiesActivity : AppCompatActivity() {
 var mCityList=ArrayList<City>()
 var mCityAdapter: CityAdapter ?= null
 lateinit var myDialog: AlertDialog
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)
        Hawk.init(this).build()
        myDialog=BaseActivity.loading(CitiesActivity@this)
        rv_city.layoutManager = LinearLayoutManager(this)
        getCities()
       if(Hawk.contains(Constants.CITIES)){
         mCityAdapter = CityAdapter(this, Hawk.get(Constants.CITIES))
         buildCityRecy()
        mCityAdapter!!.notifyDataSetChanged()

       }
    }
    private fun getCities() {
       showDia()
        mCityList.clear()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_CITIES,
            Response.Listener { response ->
                Log.e("cities",response.toString())
               finishDia()
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
                        mCityList.add(0,City(0,getString(R.string.all_cities),""))
                        mCityAdapter = CityAdapter(this, mCityList)
                        buildCityRecy()
                        mCityAdapter!!.notifyDataSetChanged()
                        Hawk.put(Constants.CITIES, mCityList)

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
                return map
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }
    fun buildCityRecy(){
       rv_city.adapter = mCityAdapter
       mCityAdapter!!.setOnItemClickListener(object : CityAdapter.OnItemClickListener {
           override fun onClicked(clickedItemPosition: Int, id: String, title: String) {
               val resultIntent = Intent()
               resultIntent.putExtra("position", clickedItemPosition)
               resultIntent.putExtra("cityId", id)
               resultIntent.putExtra("city", title)
               setResult(RESULT_OK, resultIntent)
               finish()
           }
       })
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

//    @Override fun getItemPosition(o: City): Int {
//        return PagerAdapter.POSITION_NONE
//    }
}