package com.reemsib.mosta3ml.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.AllCategoryAdapter
import com.reemsib.mosta3ml.model.AllCategory
import com.reemsib.mosta3ml.model.SubCategory
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.activity_category.*
import org.json.JSONException
import org.json.JSONObject

class CategoryActivity : AppCompatActivity() {

 val mAllCateList =ArrayList<AllCategory>()
 var mCateAdapter: AllCategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        getAllCate()

//     if (Hawk.contains(Constants.ALL_CATEGORIES)){
//         mCateAdapter = AllCategoryAdapter(this, Hawk.get(Constants.ALL_CATEGORIES))
//         buildGridView()
//     }
       getAllCate()
    }

    private fun getAllCate() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_MAIN_SUB_CAT,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonArray = obj.getJSONArray("categories")

                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
//                            val id = jsObj.getInt("id")
//                            val name = jsObj.getString("name")
//                            val image = jsObj.getString("image")
                            val mJson = JsonParser().parse(jsObj.toString())
                            val allCat = Gson().fromJson<Any>(mJson, SubCategory::class.java) as AllCategory
                            mAllCateList.add(allCat)
                        }
                        Hawk.put(Constants.ALL_CATEGORIES, mAllCateList)
                        Log.e("all_cat", (Hawk.get(Constants.ALL_CATEGORIES) as Any).toString())
                        mCateAdapter = AllCategoryAdapter(this, mAllCateList)

                        buildGridView()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi.hide()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
//                Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)


    }

    private fun buildGridView() {
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        rv_categ.layoutManager = llm
        rv_categ.adapter = mCateAdapter
        mCateAdapter!!.notifyDataSetChanged()

        mCateAdapter!!.setOnItemClickListener(object : AllCategoryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: String, title: String) {

            }

        })
    }

}