package com.reemsib.mosta3ml.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.CountryAdapter
import com.reemsib.mosta3ml.model.Country
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.custom_dialog_country_fragment.*
import org.json.JSONException
import org.json.JSONObject


class CustomDialogCountryFragment : DialogFragment() {

    private var mCountryList = ArrayList<Country>()
    private var mCountryAdapter: CountryAdapter? = null
    var mCustomDIa: CountryCustomDialogInterface? = null
    var avi_dialog:AVLoadingIndicatorView ?=null

    interface CountryCustomDialogInterface {
        fun sendData(id: Int, calling_code: String)
    }

    fun setCustomDialogInterface(customDI: CountryCustomDialogInterface) {
        mCustomDIa = customDI
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.custom_dialog_country_fragment, container, false)
          avi_dialog=root.findViewById(R.id.avi) as (AVLoadingIndicatorView)
//        if(Hawk.contains(Constants.COUNTRIES)){
//            mCountryAdapter = CountryAdapter(requireActivity(), Hawk.get(Constants.COUNTRIES))
//            buildCountryRecy()
//        }
        getCountry()

        return root
    }


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.70).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    private fun getCountry() {
        avi_dialog!!.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_COUNTRIES,
            Response.Listener { response ->
                avi_dialog!!.hide()

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
                        Hawk.put(Constants.COUNTRIES, mCountryList)
                        mCountryAdapter = CountryAdapter(requireActivity(), mCountryList)
                        buildCountryRecy()

                        Toast.makeText(
                            requireContext(),
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi_dialog!!.hide()
                //   Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT)
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
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
        rv_countries.adapter = mCountryAdapter
        rv_countries.layoutManager = LinearLayoutManager(requireActivity())

        mCountryAdapter!!.setOnItemClickListener(object : CountryAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int, calling_code: String) {
                dialog!!.dismiss()
                if (mCustomDIa != null) {
                    mCustomDIa!!.sendData(id, calling_code)
                }
            }

        })
    }


}