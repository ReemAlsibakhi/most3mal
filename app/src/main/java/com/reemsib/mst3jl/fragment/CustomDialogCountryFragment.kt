package com.reemsib.mst3jl.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.CountryAdapter
import com.reemsib.mst3jl.model.Country
import com.reemsib.mst3jl.model.MainCategory
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.custom_dialog_country_fragment.*
import kotlinx.android.synthetic.main.custom_dialog_country_fragment.et_search
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject


class CustomDialogCountryFragment : DialogFragment() {

    private var mCountryList = ArrayList<Country>()
    private var mCountryAdapter: CountryAdapter? = null
    var mCustomDIa: CountryCustomDialogInterface? = null
    lateinit var myDialog: AlertDialog
    lateinit var rvCountries:RecyclerView
    lateinit var etSearch:EditText
    interface CountryCustomDialogInterface {
        fun sendData(id: Int, calling_code: String)
    }

    fun setCustomDialogInterface(customDI: CountryCustomDialogInterface) {
        mCustomDIa = customDI
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.custom_dialog_country_fragment, container, false)
        rvCountries=root.findViewById<RecyclerView>(R.id.rv_countries)
        etSearch=root.findViewById<EditText>(R.id.et_search)
        Hawk.init(requireContext()).build()
        myDialog=BaseActivity.loading(requireContext())

        getCountry()
        listenerEdit()
        if(Hawk.contains(Constants.COUNTRIES)){
            mCountryAdapter = CountryAdapter(requireActivity(), Hawk.get(Constants.COUNTRIES))
            buildCountryRecy()
        }
        return root
    }
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.70).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun getCountry() {
        showDia()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_COUNTRIES,
            Response.Listener { response ->
              finishDia()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        var jsonArray = obj.getJSONArray("countries")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)

                            val mJson = JsonParser().parse(jsObj.toString())
                            val country =
                                Gson().fromJson<Any>(mJson, Country::class.java) as Country
                            mCountryList.add(country)
                        }
                        mCountryAdapter = CountryAdapter(requireActivity(), mCountryList)
                        buildCountryRecy()
                        mCountryAdapter!!.notifyDataSetChanged()
                        if (isAdded && mCountryList.isNotEmpty()) {
                            Hawk.put(Constants.COUNTRIES, mCountryList)
                        }
                     //   Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
          Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
          //      map["token"]= Session.getToken()

                return map
            }


        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }
    private fun buildCountryRecy() {
        rvCountries.layoutManager = LinearLayoutManager(requireActivity())
        rvCountries.adapter = mCountryAdapter
        mCountryAdapter!!.setOnItemClickListener(object : CountryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int, calling_code: String) {
                dialog!!.dismiss()
                if (mCustomDIa != null) {
                    mCustomDIa!!.sendData(id, calling_code)
                }
            }

        })
    }

    private fun listenerEdit() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                Log.e("filter_country", s.toString())
                filter(s.toString())
            }
        })
    }
    private fun filter(text: String) {
        if (Hawk.contains(Constants.COUNTRIES)){
            val filteredList: ArrayList<Country> = ArrayList()

            for ( item : Country in Hawk.get(Constants.COUNTRIES) as ArrayList<Country> ) {
                if (item.name.toLowerCase().contains(text.toLowerCase()) || item.calling_code.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
                mCountryAdapter!!.filterList(filteredList)
            }
        }else{
            val filteredList1: ArrayList<Country> = ArrayList()
            for ( item : Country in mCountryList ) {
                if (item.name.toLowerCase().contains(text.toLowerCase()) || item.calling_code.toLowerCase().contains(text.toLowerCase())) {
                    filteredList1.add(item)
                }
                mCountryAdapter!!.filterList(filteredList1)
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