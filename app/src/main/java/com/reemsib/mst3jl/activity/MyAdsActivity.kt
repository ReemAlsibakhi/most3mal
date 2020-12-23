package com.reemsib.mst3jl.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.AdvertRecyclerAdapter
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.activity_my_ads.*
import kotlinx.android.synthetic.main.activity_my_ads.et_search
import org.json.JSONException
import org.json.JSONObject

class MyAdsActivity : AppCompatActivity() {
    val myAdsList = ArrayList<Advert>()
    var mMyAdsGridAdapter: AdvertRecyclerAdapter?=null
    lateinit var myDialog: AlertDialog
    lateinit var layoutManager: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)
        myDialog=BaseActivity.loading(MyAdsActivity@this)
        getMyAdvertisements()
        listenerEdit()
        if (Hawk.contains(Constants.MY_ADS)){
            mMyAdsGridAdapter= AdvertRecyclerAdapter(this, Hawk.get(Constants.MY_ADS),"")
            buildAdvertGrid()
        }
    }
    private fun listenerEdit() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                Log.e("filter_edit", s.toString())
                filter(s.toString())
            }
        })
    }

    private fun getMyAdvertisements() {
        showDia()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_MY_ADVERT,
            Response.Listener { response ->
                finishDia()
                Log.e("my_ads",response.toString())
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
                        mMyAdsGridAdapter = AdvertRecyclerAdapter(this, myAdsList,"")
                        Hawk.put(Constants.MY_ADS,myAdsList)
                        buildAdvertGrid()
                        mMyAdsGridAdapter!!.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
                Toast.makeText(this, error.message+"error listener", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + PreferencesManager(applicationContext).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun buildAdvertGrid() {
        layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
        rv_myAds.setHasFixedSize(true);
        rv_myAds.layoutManager = layoutManager
        rv_myAds.adapter=mMyAdsGridAdapter
        mMyAdsGridAdapter!!.setOnItemClickListener(object :AdvertRecyclerAdapter.OnItemClickListener{
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID,id)
                startActivityForResult(i,1)
            }

            override fun onLongClick(clickedItemPosition: Int, id: Int) {
            }
        }) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val delete = data!!.getStringExtra("delete_advert")
                if (delete=="1"){
                    Log.e("update_delete",delete)
                    myAdsList.clear()
                    getMyAdvertisements()
                } }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private fun filter(text: String) {
        if (Hawk.contains(Constants.MY_ADS)){
            val filteredList: ArrayList<Advert> = ArrayList()
            for ( item : Advert in Hawk.get(Constants.MY_ADS) as ArrayList<Advert> ) {
                if (item.title.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
                mMyAdsGridAdapter!!.filterList(filteredList)
            }
        }else{
            val filteredList1: ArrayList<Advert> = ArrayList()
            for ( item : Advert in myAdsList ) {
                if (item.title.toLowerCase().contains(text.toLowerCase())) {
                    filteredList1.add(item) }
                mMyAdsGridAdapter!!.filterList(filteredList1)
            }
        }
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