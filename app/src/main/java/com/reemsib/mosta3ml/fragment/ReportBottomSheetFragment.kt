package com.reemsib.mosta3ml.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.ReportAdapter
import kotlinx.android.synthetic.main.layout_bottom_sheet_report.*

class ReportBottomSheetFragment : BottomSheetDialogFragment() {
    var reportList=ArrayList<String>()
    var reportAdapter :ReportAdapter ?= null

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var mReportList=resources.getStringArray(R.array.reports)
        for (i in 0 until  mReportList.size){
            reportList.add(mReportList[i])
        }
        reportAdapter= ReportAdapter(requireActivity(),reportList)
        rv_reports.adapter=reportAdapter
        rv_reports.layoutManager= LinearLayoutManager(requireContext())

    }
}