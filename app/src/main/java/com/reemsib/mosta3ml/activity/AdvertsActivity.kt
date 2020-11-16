package com.reemsib.mosta3ml.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.AdvertAdapter
import com.reemsib.mosta3ml.adapter.SliderPaidAdvertAdapter
import com.reemsib.mosta3ml.adapter.SubCategoryAdapter
import com.reemsib.mosta3ml.fragment.ModelBottomSheetFragment
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.model.SubCategory
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import kotlinx.android.synthetic.main.activity_adverts.*
import kotlinx.android.synthetic.main.activity_adverts.sliderView
import org.json.JSONException
import org.json.JSONObject


class AdvertsActivity : AppCompatActivity(), View.OnClickListener {

    var mAdvertList = ArrayList<Advert>()
    var mAdvertAdapter: AdvertAdapter? = null

    val mPaidAdvertList= ArrayList<Advert>()
    var mPaidAdvAdapter: SliderPaidAdvertAdapter? = null

    var mSubCatList = ArrayList<SubCategory>()
    var mSubcategAdapter: SubCategoryAdapter? = null

    var catId :Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_adverts)

        catId=intent.getIntExtra(Constants.MAIN_CATEGORIES_ID,-1)


        tv_city.setOnClickListener(this)
        tv_model.setOnClickListener(this)

        Hawk.init(this).build()

        if(Hawk.contains(Constants.SUB_CATEGORIES)){
            mSubcategAdapter = SubCategoryAdapter(this, Hawk.get(Constants.SUB_CATEGORIES))
            buildSubCategRecy()
        }

        getSubCategories()
        getPaidAdverts()
        getFreeAdverts()
    }



    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.tv_city ->{startActivity(Intent(this,CitiesActivity::class.java))}
            R.id.tv_model ->{ModelBottomSheetFragment().apply {
                show(supportFragmentManager, ModelBottomSheetFragment.TAG)}
        }
    }
  }

    private fun getSubCategories() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_SUB_CATEGORIES_TO_CATEGORY+catId, Response.Listener { response ->
            avi.hide()
            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {

                    val jsonArray = obj.getJSONArray("categories")


                    for (i in 0 until jsonArray.length()) {

                        val jsObj = jsonArray.getJSONObject(i)
                        val mJson = JsonParser().parse(jsObj.toString())
                        val subCat = Gson().fromJson<Any>(mJson, SubCategory::class.java) as SubCategory

                        mSubCatList.add(subCat)
                    }
                    Hawk.put(Constants.SUB_CATEGORIES,mSubCatList)
                    mSubcategAdapter = SubCategoryAdapter(this, mSubCatList)
                    buildSubCategRecy()

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { error ->
                 avi.hide()
                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT)
                    .show()
            }) {


        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


    }

    private fun getPaidAdverts() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_ADVERT_IN_CATEGORY + catId,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        val itemsObj = obj.getJSONObject("items")
                 //       val current_page = itemsObj.getInt("current_page")
                        val jsonArray = itemsObj.getJSONArray("data")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val paidAdv = Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                            if (paidAdv.adv_type == "paid")
                                mPaidAdvertList.add(paidAdv)

                        }
                        // Hawk.put(Constants.PAID_ADVERTS,mPaidAdvertList)
                        mPaidAdvAdapter = SliderPaidAdvertAdapter(Activity(), mPaidAdvertList)
                        buildPiadSlider()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi.hide()
//                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT)
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {


        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


    }

    private fun getFreeAdverts() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_ADVERT_IN_CATEGORY+catId, Response.Listener { response ->
            avi.hide()
            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {
                    val itemsObj=obj.getJSONObject("items")
                  //  val current_page= itemsObj.getInt("current_page")
                    val jsonArray = itemsObj.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {

                        val jsObj = jsonArray.getJSONObject(i)
                        val mJson = JsonParser().parse(jsObj.toString())
                        val advert = Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                        if(advert.adv_type=="free")
                            mAdvertList.add(advert)

                    }

                  //  Hawk.put(Constants.FREE_ADVERT,mAdvertList)
                    mAdvertAdapter = AdvertAdapter(this, mAdvertList)
                    buildAdvertGrid()

                //    Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { error ->
                avi.hide()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
//                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {


        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


    }

    private fun buildSubCategRecy() {
        rv_sub_category.adapter = mSubcategAdapter
        rv_sub_category.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
    }

    private fun buildPiadSlider() {
        sliderView.setSliderAdapter(mPaidAdvAdapter!!)
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)

        mPaidAdvAdapter!!.setOnItemClickListener(object :SliderPaidAdvertAdapter.OnItemClickListener{
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID,id)
                startActivity(i)
            }

        })

    }

    private fun buildAdvertGrid() {
        grid_adverts.adapter = mAdvertAdapter
        grid_adverts.isExpanded = true
        mAdvertAdapter!!.setOnItemClickListener(object :AdvertAdapter.OnItemClickListener{
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
