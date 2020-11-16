package com.reemsib.mosta3ml.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.AdvertsActivity
import com.reemsib.mosta3ml.adapter.MainCategoryAdapter
import com.reemsib.mosta3ml.model.MainCategory
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import com.wang.avi.AVLoadingIndicatorView
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    var avi:AVLoadingIndicatorView ?=null
    var grid_category:GridView ?=null

    private var mCategoryAdapter: MainCategoryAdapter? = null
    private var mCategoryList = ArrayList<MainCategory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        avi=root.findViewById(R.id.avi) as (AVLoadingIndicatorView)
        grid_category=root.findViewById(R.id.grid_category) as (GridView)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Hawk.init(requireContext()).build();

        if(Hawk.contains(Constants.MAIN_CATEGORIES)){
            mCategoryAdapter = MainCategoryAdapter(requireActivity(), Hawk.get(Constants.MAIN_CATEGORIES))
            buildGridView()
        }
        getMainCategories()

    }

    private fun getMainCategories() {
        avi!!.show()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_MAIN_CATEGORIES, Response.Listener { response ->
            avi!!.hide()
            try {
                val obj = JSONObject(response)

                if (obj.getBoolean("status")) {

                    val jsonArray = obj.getJSONArray("categories")

                    for (i in 0 until jsonArray.length()) {

                        val jsObj = jsonArray.getJSONObject(i)

                        val mJson = JsonParser().parse(jsObj.toString())
                        val category = Gson().fromJson<Any>(mJson, MainCategory::class.java) as MainCategory

                        mCategoryList.add(category)
                    }
                    Hawk.put(Constants.MAIN_CATEGORIES, mCategoryList)
                    mCategoryAdapter = MainCategoryAdapter(requireActivity(), mCategoryList)
                    buildGridView()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { error ->
                avi!!.hide()
                Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_SHORT).show()
            }) {

//            override fun getHeaders(): MutableMap<String, String> {
//                val map = HashMap<String, String>()
//
//                return map
//            }

        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)


    }

    private fun buildGridView() {
        grid_category!!.adapter = mCategoryAdapter
        mCategoryAdapter!!.setOnItemClickListener(object : MainCategoryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int, name: String, img: String) {
                val i = Intent(requireActivity(), AdvertsActivity::class.java)
                i.putExtra(Constants.MAIN_CATEGORIES_ID,id)
                startActivity(i)
            }
        })

    }

}