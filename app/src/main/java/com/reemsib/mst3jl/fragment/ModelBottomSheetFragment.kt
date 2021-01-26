package com.reemsib.mst3jl.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ModelAdapter
import com.reemsib.mst3jl.model.ModelYear
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.layout_bottom_sheet_model.*
import org.json.JSONException
import org.json.JSONObject

class ModelBottomSheetFragment : BottomSheetDialogFragment() {

    var mModelList=ArrayList<ModelYear>()
    var mModelAdapter :ModelAdapter ?= null
    lateinit var myDialog: AlertDialog
    var mCustomDIa: ModelBottomSheetInterface? = null

    companion object {
        const val TAG = "ModelBottomSheetFragment"
    }
    interface ModelBottomSheetInterface {
        fun sendData(position:Int,id: Int, model: String)
    }
    fun setModelInterface(customDI: ModelBottomSheetInterface) {
        mCustomDIa = customDI
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_model, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDialog=BaseActivity.loading(requireContext())

        buildModelRecy()
        getModels()

    }
    private fun getModels() {
       showDia()
        mModelList.clear()
        val stringRequest = object : StringRequest(Method.GET, URLs.URL_YEARS, Response.Listener { response ->
          finishDia()
            Log.e("models",response.toString())

            try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                        val jsonArray = obj.getJSONArray("years")
                        for (i in 0 until jsonArray.length()) {
                            val jsObj = jsonArray.getJSONObject(i)
                            val mJson = JsonParser().parse(jsObj.toString())
                            val modelYear = Gson().fromJson<Any>(mJson, ModelYear::class.java) as ModelYear
                            mModelList.add(modelYear)
                        }
                        if (isAdded && mModelList.isNotEmpty()) {
                            mModelList.add(0,ModelYear(0,"جميع الموديلات"))
                            mModelAdapter!!.notifyDataSetChanged()
                            Hawk.put(Constants.MODELS, mModelList)

                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
              finishDia()
               // Toast.makeText(requireContext(), R.string.failed_internet, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
               // map["token"]= Session.getToken()

                return map
            }
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }
    private fun buildModelRecy() {
        rv_model.layoutManager = LinearLayoutManager(requireContext())
        mModelAdapter = ModelAdapter(requireActivity(), mModelList)
        rv_model.adapter = mModelAdapter
        mModelAdapter!!.setOnItemClickListener(object :ModelAdapter.OnItemClickListener{
            override fun onClicked(position: Int, id: Int, year: String) {
                mCustomDIa!!.sendData(position,id,year)

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