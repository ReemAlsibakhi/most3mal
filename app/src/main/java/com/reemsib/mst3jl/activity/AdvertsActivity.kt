package com.reemsib.mst3jl.activity

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.*
import com.reemsib.mst3jl.fragment.ModelBottomSheetFragment
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.model.MainCategory
import com.reemsib.mst3jl.model.Company
import com.reemsib.mst3jl.model.SubCategory
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import kotlinx.android.synthetic.main.activity_adverts.*
import kotlinx.android.synthetic.main.activity_adverts.et_search
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class AdvertsActivity : AppCompatActivity(), View.OnClickListener{
    var mAdvertList = ArrayList<Advert>()
    var mAdvertAdapter: AdvertRecyclerAdapter? = null
    val mPaidAdvertList= ArrayList<Advert>()
    var mPaidAdvAdapter: SliderPaidAdvertAdapter? = null
    var mMainCatList = ArrayList<MainCategory>()
    var mSubCatList = ArrayList<SubCategory>()
    var mCompanyList = ArrayList<Company>()
    var mMainCategoryAdapter: MainCategoryAdapter? = null
    var mCarCompanyAdapter: CarCompanyAdapter? = null
    var mSubcategAdapter: SubCategoryAdapter? = null
    var mainCatId :String ?= ""
    private  var cityId :String=""
    private  var yearId :String=""
    private  var subCat :String=""
    private  var companyId :String=""
    private  var searchText :String=""
    private lateinit var manager: PreferencesManager
    private lateinit var myDialog: AlertDialog
    private lateinit var layoutManager:GridLayoutManager
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var visibleItemCount = 0
    private var previousTotal = 0
    private var isLoading:Boolean = true
    private var view_threshold=30
    private var page: Int =1
    private var allCat: String ="0"
    private var customDialogModel = ModelBottomSheetFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adverts)
        Hawk.init(this).build()
        manager=PreferencesManager(applicationContext)
        myDialog= BaseActivity.loading(AdvertsActivity@ this)
        manager.setFirstRun(false)

        Log.e("your_loc Adverts","${manager.getLat()},${manager.getlng()},${manager.isFirstRun()}")

        mainCatId=intent.getStringExtra(Constants.MAIN_CATEGORIES_ID)
        Log.e("main_cat_id", "$mainCatId,$allCat")
        if (mainCatId=="0"){
            allCat="1"
        }
        tv_city.setOnClickListener(this)
        tv_model.setOnClickListener(this)
        buildRecyMainCat()
        buildSubCategRecy()
        buildCarCompanyRecy()
        buildPiadSlider()
        buildAdvertGrid()

        buildRequest()
        listenerEdit()
        initPagination()

    }
    private fun buildRequest() {
        if (allCat=="1"){
            Log.e("all_Category", "$allCat")
            getMainCategories(URLs.URL_MAIN_CATEGORIES)
            getPaidAdverts(URLs.URL_ADVERTS+"?lat="+manager.getLat()+"&lng="+manager.getlng())
            getFreeAdverts(URLs.URL_ADVERTS+"?lat="+manager.getLat()+"&lng="+manager.getlng())
        }else{
            getSubCategories(URLs.URL_SUB_CATEGORIES_TO_CATEGORY + mainCatId)
            getPaidAdverts(URLs.URL_ADVERT_IN_CATEGORY + mainCatId+"?lat="+manager.getLat()+"&lng="+manager.getlng())
            getFreeAdverts(URLs.URL_ADVERT_IN_CATEGORY + mainCatId+"?page="+"?lat="+manager.getLat()+"&lng="+manager.getlng())

        }
    }
    private fun filterAdverts() {
        if (allCat=="1"){
            Log.e("all_Category_filter", "$searchText,$cityId, $yearId,$mainCatId,$subCat,$companyId")
            getPaidAdverts(URLs.URL_ADVERTS + "?search=" + searchText + "&city_id=" + cityId + "&year_id=" + yearId + "&main_category_id=" + mainCatId + "&category_id=" + subCat + "&company_id=" + companyId)
            getFreeAdverts(URLs.URL_ADVERTS + "?search=" + searchText + "&city_id=" + cityId + "&year_id=" + yearId + "&main_category_id=" + mainCatId + "&category_id=" + subCat + "&company_id=" + companyId)

        }else{
            Log.e("_filter", "$cityId, $yearId,$mainCatId,$subCat")

            getPaidAdverts(URLs.URL_ADVERT_IN_CATEGORY + mainCatId + "?search=" + searchText + "&city_id=" + cityId + "&year_id=" + yearId + "&main_category_id=" + mainCatId + "&category_id=" + subCat + "&company_id=" + companyId)
            getFreeAdverts(URLs.URL_ADVERT_IN_CATEGORY + mainCatId + "?search=" + searchText + "&city_id=" + cityId + "&year_id=" + yearId + "&main_category_id=" + mainCatId + "&category_id=" + subCat + "&company_id=" + companyId)

        }
    }
    private fun getMainCategories(url: String) {
     //   mCategoryList.clear()
        val stringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            try {
                val obj = JSONObject(response)
                if (obj.getBoolean("status")) {
                    val jsonArray = obj.getJSONArray("categories")
                    for (i in 0 until jsonArray.length()) {
                        val jsObj = jsonArray.getJSONObject(i)
                        val mJson = JsonParser().parse(jsObj.toString())
                        val category = Gson().fromJson<Any>(mJson, MainCategory::class.java) as MainCategory
                        mMainCatList.add(category)
                        mMainCategoryAdapter!!.notifyDataSetChanged()
                    }
//                        mCategoryList.add(0, MainCategory(0,getString(R.string.all_categ),"all"))
//                            Hawk.put(Constants.MAIN_CATEGORIES, mCategoryList)
//                    buildRecyMainCat()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { error ->
                //  avi!!.hide()
                myDialog.dismiss()
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }) {
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }
    private fun buildRecyMainCat() {
        rv_main_category.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
        mMainCategoryAdapter = MainCategoryAdapter(this, mMainCatList, "advert")
        rv_main_category.adapter = mMainCategoryAdapter
        mMainCategoryAdapter!!.setOnItemClickListener(object :
            MainCategoryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int, name: String,has_models:Int) {
                Log.e("main_category_id", "$clickedItemPosition,$id")
                subCat = ""
                if (clickedItemPosition==-1){
                   mainCatId=""
               }else{
                   mainCatId = id.toString()
               }
                if (has_models==1){
                    tv_model.visibility=View.VISIBLE
                }else{
                    tv_model.visibility=View.GONE
                }
                getSubCategories(URLs.URL_SUB_CATEGORIES_TO_CATEGORY + id.toString())
                filterAdverts()
            }


        })
    }
    private fun listenerEdit() {
        et_search.setOnKeyListener(OnKeyListener { view, keyCode, keyevent ->
            //If the keyevent is a key-down event on the "enter" button
            if (keyevent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchText=et_search.text.toString()
                filterAdverts()
                true
            } else
            {
                false
            }

        })
    }
    private fun getSubCategoriesCars(id: Int) {
        mCompanyList.clear()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_SUB_CATEGORIES_COMPANY + id,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("companies")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val comp = Gson().fromJson<Any>(mJson, Company::class.java) as Company
                            mCompanyList.add(comp)
                            mCarCompanyAdapter!!.notifyDataSetChanged()
                        }
//                        Hawk.put(Constants.SUB_CATEGORIES, mSubCatList)
//                        buildCarCompanyRecy()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //    myDialog.dismiss()
                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT)
                    .show()
            }) {


        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun buildCarCompanyRecy() {
        rv_sub_category2_car.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
        mCarCompanyAdapter = CarCompanyAdapter(this, mCompanyList)
        rv_sub_category2_car.adapter=mCarCompanyAdapter
        mCarCompanyAdapter!!.setOnItemClickListener(object : CarCompanyAdapter.OnItemClickListener {
            override fun onClicked(position: Int, id: Int) {
                 if(position==-1){
                     companyId=""
                 }else{
                     companyId = id.toString()
                 }
                filterAdverts()
            }

        })
    }
    private fun performPagination() {
       var url:String ?=""
        if (allCat=="1"){
            url=URLs.URL_ADVERTS+"?page="+page
            Log.e("url_",url)
        }else{
            url=URLs.URL_ADVERT_IN_CATEGORY+mainCatId+"?page="+page
            Log.e("url_2",url)
        }
           progress_bar.visibility=View.VISIBLE
            val stringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
                    try {
                        val obj = JSONObject(response)
                        if (obj.getBoolean("status")) {
                            val itemsObj = obj.getJSONObject("items")
                            //      currentPage= itemsObj.getInt("current_page")
                            val jsonArray = itemsObj.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val jsObj = jsonArray.getJSONObject(i)
                                val mJson = JsonParser().parse(jsObj.toString())
                                val advert =
                                    Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                                if (advert.adv_type == "free")
                                    mAdvertList.add(advert)
                            }
                            mAdvertAdapter = AdvertRecyclerAdapter(this, mAdvertList, "")
                            buildAdvertGrid()
                            progress_bar.visibility = View.GONE

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "No more Data available....",
                                Toast.LENGTH_LONG
                            ).show()

                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    progress_bar.visibility = View.GONE
                    Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT).show()
                }) {

                override fun getParams(): MutableMap<String, String> {
                    val params=HashMap<String, String>()
//                    params["page"]=page.toString()
                    return params
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["Accept"] = "application/json"
                    map["Content-Type"] = "application/json"
                    if (manager.isLoggedIn){
                        map["Authorization"]="Bearer " + manager.getAccessToken()
                    }
                    return map
                }

            }
            MySingleton.getInstance(this).addToRequestQueue(stringRequest)


        }
    private fun initPagination() {
        nested_scroll.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (v!!.getChildAt(v!!.childCount - 1) != null) {
                    if (attr.scrollY >= v!!.getChildAt(v!!.childCount - 1).measuredHeight - v!!.measuredHeight &&
                        attr.scrollY > oldScrollY
                    ) {
                        val visibleItemCount: Int = layoutManager.getChildCount()
                        val totalItemCount: Int = layoutManager.getItemCount()
                        val pastVisibleItem: Int = layoutManager.findFirstVisibleItemPosition()
                        if (isLoading) {
                            if (totalItemCount > previousTotal) {
                                isLoading = false
                                previousTotal = totalItemCount
                                Log.e("previousTotal", "$previousTotal")
                            }
                        }
                        if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItem + view_threshold)) {
                            Log.e("scroll_view", "22222222222222")
                            page++
                            performPagination()
                            isLoading = true
                        }
                    }
                }
            }
        });
    }
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.tv_city -> {
                startActivityForResult(Intent(this, CitiesActivity::class.java), 20);
            }
            R.id.tv_model -> {
                showDialogSheet()

            }
        }
    }

    private fun showDialogSheet() {
        customDialogModel.setModelInterface(object :
            ModelBottomSheetFragment.ModelBottomSheetInterface {
            override fun sendData(position:Int,id: Int, model: String) {
                if (position==0){
                    yearId=""
                    tv_model.text = getString(R.string.model)

                }else{
                    yearId = id.toString()
                    tv_model.text = model
                }

                customDialogModel.dismiss()
                filterAdverts()
            }

        })
        customDialogModel.apply { show(supportFragmentManager, ModelBottomSheetFragment.TAG)
        }
    }

    private fun getSubCategories(url: String) {
        mSubCatList.clear()
        val stringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                var subCat:SubCategory ?=null

                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("categories")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                             subCat = Gson().fromJson<Any>(mJson, SubCategory::class.java) as SubCategory
                            mSubCatList.add(subCat)

                        }
                        if (subCat!!.has_models==1){
                            tv_model.visibility=View.VISIBLE
                        }else{
                            tv_model.visibility=View.GONE
                        }
                        mSubcategAdapter!!.notifyDataSetChanged()

                       // Hawk.put(Constants.SUB_CATEGORIES, mSubCatList)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //    myDialog.dismiss()
                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT)
                    .show()
            }) {

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun getPaidAdverts(url: String) {
        mPaidAdvertList.clear()
        val stringRequest = object : StringRequest(Method.GET,
            url,
            Response.Listener { response ->
                //     myDialog.dismiss()
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
                         mPaidAdvAdapter!!.notifyDataSetChanged()
                        // Hawk.put(Constants.PAID_ADVERTS,mPaidAdvertList)
                        if (mPaidAdvertList.isEmpty()) {
                            sliderView.visibility = View.GONE
                        } else {
                            sliderView.visibility = View.VISIBLE
                        }
                  //  buildPiadSlider()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //    myDialog.dismiss()
//                Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT)
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
               if (manager.isLoggedIn){
                   map["Authorization"]="Bearer " + manager.getAccessToken()
               }
                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun getFreeAdverts(url: String) {
         mAdvertList.clear()
          showDia()
      //   progress_bar.visibility=View.VISIBLE
           val stringRequest = object : StringRequest(Method.GET, url,
               Response.Listener { response ->
                   finishDia()
                   try {
                       val obj = JSONObject(response)

                       if (obj.getBoolean("status")) {
                           val itemsObj = obj.getJSONObject("items")
                           //      currentPage= itemsObj.getInt("current_page")
                           val jsonArray = itemsObj.getJSONArray("data")
                           for (i in 0 until jsonArray.length()) {
                               val jsObj = jsonArray.getJSONObject(i)
                               val mJson = JsonParser().parse(jsObj.toString())
                               val advert =
                                   Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                               if (advert.adv_type == "free")
                                   mAdvertList.add(advert)
                           }
                           mAdvertAdapter!!.notifyDataSetChanged()
                           //  Hawk.put(Constants.FREE_ADVERT,mAdvertList)

                           if (mAdvertList.isEmpty()) {
                               not_result.visibility = View.VISIBLE
                           } else {
                               not_result.visibility = View.GONE
                           }

                       }
                   } catch (e: JSONException) {
                       e.printStackTrace()
                   }
               },
               Response.ErrorListener { error ->
                   finishDia()
                   //Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                   Toast.makeText(this, R.string.failed_internet, Toast.LENGTH_SHORT).show()
               }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                if (manager.isLoggedIn){
                    map["Authorization"]="Bearer " + manager.getAccessToken()
                }
                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun buildSubCategRecy() {
        rv_sub_category.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
        mSubcategAdapter = SubCategoryAdapter(this, mSubCatList)
        rv_sub_category.adapter = mSubcategAdapter
        mSubcategAdapter!!.setOnItemClickListener(object : SubCategoryAdapter.OnItemClickListener {
            override fun onClicked(position: Int, id: Int, category: String) {
                Log.e("sub_cat_id", "$position,$id")
                if (position==-1){
                    subCat=""
                }else{
                    subCat = id.toString()
                    getSubCategoriesCars(id)
                }
                filterAdverts()

            }

        })
    }
    private fun buildPiadSlider() {
        mPaidAdvAdapter = SliderPaidAdvertAdapter(this, mPaidAdvertList)
        sliderView.setSliderAdapter(mPaidAdvAdapter!!)
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
    //    sliderView.scrollTimeInSec = 10; //set scroll delay in seconds :
        mPaidAdvAdapter!!.setOnItemClickListener(object :
            SliderPaidAdvertAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID, id)
                startActivityForResult(i, 1)
            }

        })

    }
    private fun buildAdvertGrid() {
        layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rv_adverts.setHasFixedSize(true);
        rv_adverts.layoutManager = layoutManager
        mAdvertAdapter = AdvertRecyclerAdapter(this, mAdvertList, "")
        rv_adverts.adapter = mAdvertAdapter
        mAdvertAdapter!!.notifyDataSetChanged()
        mAdvertAdapter!!.setOnItemClickListener(object : AdvertRecyclerAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(applicationContext, AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID, id)
                startActivityForResult(i, 1)
            }

            override fun onLongClick(clickedItemPosition: Int, id: Int) {
                TODO("Not yet implemented")
            }

        }) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val delete = data!!.getStringExtra("delete_advert")
                if (delete=="1"){
                    Log.e("update_delete", delete)
                    buildRequest()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }else if(requestCode==20){
            if (resultCode == RESULT_OK) {
                val position = data!!.getIntExtra("position",-1)!!
                if (position==0){
                    cityId=""
                    tv_city.text=getString(R.string.select_city)

                }else{
                    cityId = data!!.getStringExtra("cityId")!!
                    val city = data.getStringExtra("city")!!
                    tv_city.text=city
                }
                filterAdverts()
            }
            if (resultCode == RESULT_CANCELED) {
            //    tv_city.text=getString(R.string.select_city)
              //  cityId=""
             //   Log.e("city_id2", "$cityId")
               // buildRequest()
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
