package com.reemsib.mst3jl.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.AllCategoryAdapter
import com.reemsib.mst3jl.model.AllCategory
import com.reemsib.mst3jl.model.SubCategory
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.activity_category.*
import org.json.JSONException
import org.json.JSONObject
import java.text.FieldPosition

class CategoryActivity : AppCompatActivity() {

 val mAllCateList =ArrayList<AllCategory>()
 var mCateAdapter: AllCategoryAdapter? = null
 lateinit var myDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        myDialog=BaseActivity.loading(CategoryActivity@this)
        getAllCate()
        if (Hawk.contains(Constants.ALL_CATEGORIES)){
            mCateAdapter = AllCategoryAdapter(this, Hawk.get(Constants.ALL_CATEGORIES))
            buildGridView()

        }
        btn_back.setOnClickListener{finish()}
    }

    private fun getAllCate() {
        showDia()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_MAIN_SUB_CAT, Response.Listener { response ->
               finishDia()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonArray = obj.getJSONArray("categories")

                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)

                            val mJson = JsonParser().parse(jsObj.toString())
                            val allCat = Gson().fromJson<Any>(mJson, AllCategory::class.java) as AllCategory
                            mAllCateList.add(allCat)
                        }
                        mCateAdapter = AllCategoryAdapter(this, mAllCateList)
                        buildGridView()
                        mCateAdapter!!.notifyDataSetChanged()
                        Hawk.put(Constants.ALL_CATEGORIES, mAllCateList)
//                        Log.e("all_cat", (Hawk.get(Constants.ALL_CATEGORIES) as Any).toString())

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
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
        mCateAdapter!!.setOnItemClickListener(object : AllCategoryAdapter.OnItemClickListener {
            override fun onClicked(position: Int, subCategory: SubCategory) {
                val resultIntent = Intent()
                resultIntent.putExtra("category", subCategory)
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
}