package com.reemsib.mosta3ml.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.ModelAdapter
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.layout_bottom_sheet_model.*
import org.json.JSONException
import org.json.JSONObject

class ModelBottomSheetFragment : BottomSheetDialogFragment() {

    var mModelList=ArrayList<String>()
    var mModelAdapter :ModelAdapter ?= null

    companion object {

        const val TAG = "ModelBottomSheetFragment"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_model, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Hawk.contains(Constants.MODELS)){
            mModelAdapter = ModelAdapter(requireActivity(), Hawk.get(Constants.MODELS))
            buildModelRecy()
        }

        getModels()
    }

    private fun buildModelRecy() {
        rv_model.adapter = mModelAdapter
        rv_model.layoutManager = LinearLayoutManager(requireContext())
        mModelAdapter!!.notifyDataSetChanged()
    }

    private fun getModels() {

        avi_model.show()

        val stringRequest = object : StringRequest(Method.GET, URLs.URL_YEARS, Response.Listener { response ->
            avi_model.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonArray = obj.getJSONArray("years")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)
                            val year = jsObj.getString("year")
                            mModelList.add(year)
                        }

                        Hawk.put(Constants.MODELS, mModelList)

                        mModelAdapter = ModelAdapter(requireActivity(), mModelList)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi_model.hide()
                Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT)
                    .show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
               // map["token"]= Session.getToken()

                return map
            }


        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)


    }

}