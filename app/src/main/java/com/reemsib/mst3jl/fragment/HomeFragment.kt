package com.reemsib.mst3jl.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.activity.AddAdvertActivity
import com.reemsib.mst3jl.activity.AdvertsActivity
import com.reemsib.mst3jl.activity.LoginActivity
import com.reemsib.mst3jl.adapter.MainCategoryAdapter
import com.reemsib.mst3jl.model.MainCategory
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.et_search
import org.json.JSONException
import org.json.JSONObject


class HomeFragment : Fragment(),View.OnClickListener {
    var grid_category:RecyclerView ?=null
    private lateinit var manager: PreferencesManager
    private var mCategoryAdapter: MainCategoryAdapter? = null
    private var mCategoryList = ArrayList<MainCategory>()
    lateinit var myDialog: AlertDialog
    private lateinit var layoutManager:GridLayoutManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        grid_category=root.findViewById(R.id.grid_category) as (RecyclerView)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Hawk.init(requireContext()).build();
        myDialog=BaseActivity.loading(requireContext())
        manager=PreferencesManager(requireContext())
        btn_addAdvert.setOnClickListener(this)


        getMainCategories()
        if(Hawk.contains(Constants.MAIN_CATEGORIES)){
            mCategoryAdapter = MainCategoryAdapter(requireActivity(), Hawk.get(Constants.MAIN_CATEGORIES),"home")
            buildGridView()

        }
        listenerEdit()

    }
    private fun listenerEdit() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                Log.e("filter_home", s.toString())
                filter(s.toString())
            }
        })
    }
    private fun filter(text: String) {
        if (Hawk.contains(Constants.MAIN_CATEGORIES)){
            val filteredList: ArrayList<MainCategory> = ArrayList()

            for ( item : MainCategory in Hawk.get(Constants.MAIN_CATEGORIES) as ArrayList<MainCategory> ) {
                if (item.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
                mCategoryAdapter!!.filterList(filteredList)
            }
        }else{
            val filteredList1: ArrayList<MainCategory> = ArrayList()
            for ( item : MainCategory in mCategoryList ) {
                if (item.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList1.add(item)
                }
                mCategoryAdapter!!.filterList(filteredList1)
            }
        }
    }
    private fun getMainCategories() {
        mCategoryList.clear()
        showDia()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_MAIN_CATEGORIES,
            Response.Listener { response ->
                finishDia()
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("categories")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val category = Gson().fromJson<Any>(
                                mJson,
                                MainCategory::class.java
                            ) as MainCategory
                            mCategoryList.add(category)
                        }

                        if (isAdded && mCategoryList.isNotEmpty()) {
                            mCategoryList.add(0, MainCategory(0,getString(R.string.all_categ),"all"))
                            mCategoryAdapter = MainCategoryAdapter(requireActivity(), mCategoryList,"home")
                            buildGridView()
                            mCategoryAdapter!!.notifyDataSetChanged()
                            Hawk.put(Constants.MAIN_CATEGORIES, mCategoryList)

                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
               finishDia()
                Toast.makeText(requireContext(), getString(R.string.failed_internet), Toast.LENGTH_SHORT).show()
            }) {
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

    private fun buildGridView() {
        layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        grid_category!!.setHasFixedSize(true);
        grid_category!!.layoutManager = layoutManager
        grid_category!!.adapter = mCategoryAdapter
        mCategoryAdapter!!.setOnItemClickListener(object : MainCategoryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int, name: String) {
                val i = Intent(requireActivity(), AdvertsActivity::class.java)
                i.putExtra(Constants.MAIN_CATEGORIES_ID, id.toString())
                startActivity(i)

//                if (id==0){

                  //  i.putExtra(Constants.ALL_CATEGORIES, "ALL_CAT")
//                }else{
//                }
            }
        })
    }
    override fun onClick(p0: View?) {
       when(p0!!.id){
           R.id.btn_addAdvert -> {
               addAdvert()
           }
       }
    }
    private fun addAdvert() {
            if(manager.isLoggedIn){
                val i = Intent(requireContext(), AddAdvertActivity::class.java)
                i.putExtra(Constants.UPDATE, "0")
                startActivity(i)
            }else{
                startActivity(Intent(requireContext(), LoginActivity::class.java))
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

