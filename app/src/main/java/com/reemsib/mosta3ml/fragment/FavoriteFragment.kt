package com.reemsib.mosta3ml.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.AdvertDetailActivity
import com.reemsib.mosta3ml.adapter.AdvertAdapter
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.json.JSONException
import org.json.JSONObject


class FavoriteFragment : Fragment() {

    private var mFavoriteList = ArrayList<Advert>()
    var adapter:AdvertAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       Hawk.init(requireContext()).build()

//        if (Hawk.contains(Constants.FAVORITES)){
//           adapter= AdvertAdapter(requireActivity(), Hawk.get(Constants.FAVORITES))
//           buildRecyFavorite()
//        }
        getMyFavorite()
        listenerEdit()
    }



    private fun getMyFavorite() {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_GET_MY_FAVORITE,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonObj = obj.getJSONObject("data")
                        val jsonArray = jsonObj.getJSONArray("data")

                        for (i in 0 until jsonArray.length()) {

                            val jsObj = jsonArray.getJSONObject(i)
                            val advObj = jsObj.getJSONObject("advertisement")
                            val mJson = JsonParser().parse(advObj.toString())
                            val advert: Advert =
                                Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                            mFavoriteList.add(advert)
                        }
                        Hawk.put(Constants.FAVORITES, mFavoriteList)
                        adapter = AdvertAdapter(requireActivity(), mFavoriteList)
                        buildRecyFavorite()
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
                avi.hide()
                Toast.makeText(requireContext(), error.message + "", Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(requireContext()).getToken()
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
                //   AdvertAdapter(requireActivity(),mFavoriteList).filter.filter(s)
                filter(s.toString())
            }
        })
    }
    private fun buildRecyFavorite() {
        grid_favorite.adapter=adapter

        adapter!!.setOnItemClickListener(object : AdvertAdapter.OnItemClickListener {
            override fun onClicked(clickedItemPosition: Int, id: Int) {
                val i = Intent(requireContext(), AdvertDetailActivity::class.java)
                i.putExtra(Constants.ADVERT_ID, id)
                startActivity(i)
            }

            override fun onLongClick(clickedItemPosition: Int, id: Int) {
                showAlertDialog(id)
           //     Toast.makeText(requireContext(), "favorite", Toast.LENGTH_LONG).show()
            }

        })


    }

    private fun showAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
       builder.setTitle(R.string.dialogTitle)
        //set message for alert dialog
        builder.setMessage(R.string.dialogMessage)
       // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(getString(R.string.yes)){ dialogInterface, which ->
            deleteAdvert(id)
      //      Toast.makeText(requireContext(), "clicked yes", Toast.LENGTH_LONG).show()

        }
        //performing cancel action
        builder.setNeutralButton(getString(R.string.canecl)){ dialogInterface, which ->
            //Toast.makeText(requireContext(), "clicked cancel\n operation cancel", Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder.setNegativeButton(getString(R.string.no)){ dialogInterface, which ->
         //   Toast.makeText(requireContext(), "clicked No", Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteAdvert(id: Int) {
        val stringRequest = object : StringRequest(Method.POST, URLs.URL_DELETE_FAVORITE,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {
                      Toast.makeText(requireContext(),obj.getString("message"),Toast.LENGTH_LONG).show()
                      adapter!!.notifyDataSetChanged()
                      grid_favorite.invalidateViews()

                    }else{
                        Toast.makeText(requireContext(),obj.getString("message")+"error",Toast.LENGTH_LONG).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                avi.hide()
                Toast.makeText(requireContext(), error.message + "", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["advertisement_id"] = id.toString()
                return params
            }


            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(requireContext()).getToken()
                return map
            }


        }
        MySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)

    }


    private fun filter(text: String) {

       if (Hawk.contains(Constants.FAVORITES)){
           val filteredList: ArrayList<Advert> = ArrayList()

           for ( item :Advert in Hawk.get(Constants.FAVORITES) as ArrayList<Advert> ) {
               if (item.title.toLowerCase().contains(text.toLowerCase())) {
                   filteredList.add(item)

               }
               adapter!!.filterList(filteredList)
           }
       }else{
           val filteredList1: ArrayList<Advert> = ArrayList()

           for ( item :Advert in mFavoriteList ) {
               if (item.title.toLowerCase().contains(text.toLowerCase())) {
                   filteredList1.add(item)

               }
               adapter!!.filterList(filteredList1)
           }
       }


}

}