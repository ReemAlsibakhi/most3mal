package com.reemsib.mst3jl.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ReportAdapter
import com.reemsib.mst3jl.model.Report
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.URLs
import kotlinx.android.synthetic.main.layout_bottom_sheet_report.*
import org.json.JSONException
import org.json.JSONObject

class ReportBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var dialog:AlertDialog;
    var advertId: Int? = null
    var reportList = ArrayList<Report>()
    var reportAdapter: ReportAdapter? = null
    private lateinit var manager: PreferencesManager
    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_report, container, false) }
        override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog=BaseActivity.loading(requireContext())
        manager= PreferencesManager(requireContext())
        var mReportList = resources.getStringArray(R.array.reports)
//        for (i in 0 until  mReportList.size){
//            reportList.add(mReportList[i]) }
        val mArgs = arguments
        advertId = mArgs!!.getInt("advert_id")
            rv_reports.layoutManager = LinearLayoutManager(requireContext())
            reportAdapter = ReportAdapter(requireActivity(), reportList)
            rv_reports.adapter = reportAdapter
        reportList.add(Report(0, "اعلان غير اخلاقي"))
        reportList.add(Report(1, "اعلان عنصري"))
        reportList.add(Report(2, "اعلان يمس الدين"))
        reportList.add(Report(3, "غير ذلك"))
        reportAdapter!!.notifyDataSetChanged()

        reportAdapter!!.setOnItemClickListener(object : ReportAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int) {
             if (!manager.isLoggedIn){
                  Toast.makeText(requireContext(),getString(R.string.please_login),Toast.LENGTH_LONG).show()
              }else{
                if (id == 3) {
                    showDialog(id)
                } else {
                    reportAdvert(id, advertId!!,"")
                    Log.e("report", "$id,$advertId!!")
                }
             }
            }
        })
    }

    private fun showDialog(id: Int) {
        val mDialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom_note, null)
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(mDialogView)
        val btnSend = mDialogView.findViewById<Button>(R.id.btn_send)
        val mNote = mDialogView.findViewById<EditText>(R.id.et_addNote)
        btnSend.setOnClickListener {
            var note=mNote.text.toString()
            if (note.isNotEmpty()){
                dialog.dismiss()
                reportAdvert(id,advertId!!,note)
            }else{ mNote.error=getString(R.string.write_note) } }
//        mDialogView.findViewById<View>(R.id.btn_ok).setOnClickListener { //your business logic
//            dialog.dismiss()
//        }
        dialog.show()
    }
    private fun reportAdvert(id: Int, advertId: Int,note:String) {
        dialog.show()
       // GlobalSetting(requireContext()).loading(requireContext(),true)
        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_REPORTING_ADVERT,
            Response.Listener { response ->
                dialog.dismiss()
                //          GlobalSetting(requireContext()).loading(requireContext(),false)
                Log.e("hide_load", "1")
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        Toast.makeText(
                            requireContext(), getString(R.string.sent_report), Toast.LENGTH_LONG
                        ).show()
                       ReportBottomSheetFragment.apply { dismiss() }
                        Log.e("report_msg", obj.getString("message"))
                      } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_sent_report),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("report_msg", obj.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                dialog.dismiss()
                //    GlobalSetting(requireContext()).loading(requireContext(),false)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.failed_internet),
                    Toast.LENGTH_LONG
                ).show()

                Log.e("hide_load", "1")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["type"] = id.toString()
                params["note"] = note
                params["advertisement_id"] = advertId.toString()
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Authorization"] = "Bearer " + PreferencesManager(requireContext()).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

}