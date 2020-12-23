package com.reemsib.mst3jl.fragment
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.activity.AdvertDetailActivity
import com.reemsib.mst3jl.activity.LoginActivity
import com.reemsib.mst3jl.adapter.AdvertRecyclerAdapter
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.et_search
import org.json.JSONException
import org.json.JSONObject

class FavoriteFragment : Fragment(),View.OnClickListener {
    private var mFavoriteList = ArrayList<Advert>()
    var gridAdapter:AdvertRecyclerAdapter ?= null
    lateinit var myDialog: AlertDialog
    private lateinit var manager: PreferencesManager
    private lateinit var tvLogin: TextView
    private lateinit var linearLogin: LinearLayout
    private lateinit var layoutManager:GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)
        tvLogin=root.findViewById<TextView>(R.id.tv_login)
        linearLogin=root.findViewById<LinearLayout>(R.id.linear_login)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager= PreferencesManager(requireContext())
        Hawk.init(requireContext()).build()
        myDialog=BaseActivity.loading(requireContext())

        tvLogin.setOnClickListener(this)
        if(!manager.isLoggedIn){
            linearLogin.visibility=View.VISIBLE
            rv_favorite.visibility=View.GONE

        }else{
            linearLogin.visibility=View.GONE
            rv_favorite.visibility=View.VISIBLE
            getMyFavorite()
            if (Hawk.contains(Constants.FAVORITES)){
                gridAdapter= AdvertRecyclerAdapter(requireActivity(), Hawk.get(Constants.FAVORITES),"favorite")
            }
            listenerEdit()
        }
    }
    private fun getMyFavorite() {
       showDia()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_GET_MY_FAVORITE+"?lat="+manager.getLat()+"&lng="+manager.getlng(),
            Response.Listener { response ->
                Log.e("favourites",response.toString())
               finishDia()
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val advert: Advert =
                                Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                            mFavoriteList.add(advert)
                        }
                        if (isAdded && mFavoriteList.isNotEmpty()) {
                            gridAdapter = AdvertRecyclerAdapter(requireActivity(), mFavoriteList,"favorite")
                            buildRecyFavorite()
                            gridAdapter!!.notifyDataSetChanged()
                        }
                        if (mFavoriteList.isEmpty()){
                            Hawk.delete(Constants.FAVORITES)
                            tvNot_found.visibility=View.VISIBLE
                        }else{
                            Hawk.put(Constants.FAVORITES, mFavoriteList)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
               finishDia()
//                Toast.makeText(requireContext(), getString(R.string.failed_internet), Toast.LENGTH_SHORT).show()
//                Toast.makeText(requireContext(), error.message + "", Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + PreferencesManager(requireContext()).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

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
                Log.e("filter_edit", s.toString())
                filter(s.toString())
            }
        })
    }
    private fun buildRecyFavorite() {
        layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        rv_favorite.setHasFixedSize(true);
        rv_favorite.layoutManager = layoutManager
        rv_favorite.adapter=gridAdapter
        gridAdapter!!.setOnItemClickListener(object : AdvertRecyclerAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(requireContext(), AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID, id)
                startActivity(i)
            }
            override fun onLongClick(clickedItemPosition: Int, id: Int) {
                showAlertDialog(clickedItemPosition,id)
            }

        })
    }
    private fun showAlertDialog(position: Int, id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.dialogTitle)
        builder.setMessage(R.string.dialogMessage)
        builder.setPositiveButton(getString(R.string.yes)){ dialogInterface, which ->
            deleteAdvert(position,id)
        }
        builder.setNeutralButton(getString(R.string.canecl)){ dialogInterface, which ->
        }
        builder.setNegativeButton(getString(R.string.no)){ dialogInterface, which ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun deleteAdvert(position:Int,id: Int) {
        val stringRequest = object : StringRequest(Method.POST, URLs.URL_DELETE_FAVORITE,
            Response.Listener { response ->
              Log.e("delete",response)
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                      Toast.makeText(requireContext(),obj.getString("message"),Toast.LENGTH_LONG).show()
                        mFavoriteList.removeAt(position)
                        gridAdapter!!.notifyDataSetChanged()
                       // rv_favorite.invalidateViews()
                        Log.e("delete",obj.getString("message"))
                    }else{
                        Toast.makeText(requireContext(),obj.getString("message")+"error",Toast.LENGTH_LONG).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
              myDialog.dismiss()
                Toast.makeText(requireContext(), error.message + "", Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = java.util.HashMap<String, String>()
                params2.put("advertisement_id", id.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = java.util.HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"] = "Bearer " + PreferencesManager(requireContext()).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

    }
    private fun filter(text: String) {
        if (Hawk.contains(Constants.FAVORITES)) {
            val filteredList: ArrayList<Advert> = ArrayList()

            for (item: Advert in Hawk.get(Constants.FAVORITES) as ArrayList<Advert>) {
                if (item.title.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
                gridAdapter!!.filterList(filteredList)
            }
        } else {
            val filteredList1: ArrayList<Advert> = ArrayList()
            for (item: Advert in mFavoriteList) {
                if (item.title.toLowerCase().contains(text.toLowerCase())) {
                    filteredList1.add(item)
                }
                gridAdapter!!.filterList(filteredList1)
            }
        }
    }
    override fun onClick(p0: View?) {
       when(p0!!.id){
           R.id.tv_login ->{startActivity(Intent(requireContext(), LoginActivity::class.java))}
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