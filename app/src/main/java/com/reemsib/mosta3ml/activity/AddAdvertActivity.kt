package com.reemsib.mosta3ml.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.adapter.ImageAdapter
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.URLs
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.android.synthetic.main.activity_add_advert.*
import org.json.JSONException
import org.json.JSONObject


class AddAdvertActivity : AppCompatActivity(), IPickResult, View.OnClickListener{

    private var imgUri: Uri? = null
    private var imgList = ArrayList<Uri>()
    private var imgAdapter: ImageAdapter? = null
    private var priceType: String = ""
    private var categoryId: String ="1"
    private var cityId: String =""
    private var acceptNego: String = ""
    private var allowRep: String = ""
    private var offerLoc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_advert)

        linear_upload.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        select_category.setOnClickListener(this)
        select_city.setOnClickListener(this)

        imgAdapter = ImageAdapter(this, imgList)
        rv_imgsAdvert.adapter = imgAdapter
        rv_imgsAdvert.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL,
            true
        )
    }



    override fun onPickResult(r: PickResult?) {
        if (r!!.getError() == null) {
            imgUri=r.uri
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);
            imgList.add(r.uri)
            imgAdapter!!.notifyDataSetChanged();

        } else {
            //Handle possible errors
            Toast.makeText(this, r.getError().message, Toast.LENGTH_LONG).show();
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.linear_upload -> {
                PickImageDialog.build(PickSetup()).show(this)
            }
            R.id.btn_save -> {
                addAdvert()
            }
            R.id.select_category -> {
                startActivity(Intent(this, CategoryActivity::class.java))
            }
            R.id.select_city -> {
                startActivityForResult(Intent(this, CitiesActivity::class.java), 1);
            }
        }
    }

    private fun addAdvert() {
        val title = et_title.text.toString()
        val description=et_detail.text.toString()
        val price = et_price.text.toString()
        val negotiation = cb_negotiation.isChecked
        val allowReply = allow_reply.isChecked
        val allowLocation = allow_location.isChecked
        val lat:Long ?=123456
        val lng:Long ?=123456

        when(rg_price.checkedRadioButtonId){
            R.id.rb_fixed -> {
                priceType = "fixed" }
            R.id.rb_soom -> {
                priceType = "on_somme" }
            R.id.rb_undefined -> {
                priceType = "un_limited" } }

        acceptNego = if(negotiation){ "1" }else{ "0" }
        allowRep = if(allowReply){ "1" }else{ "0" }
        offerLoc = if(allowLocation){ "1" }else{ "0" }


        if (!validForm(title, description, categoryId, cityId, price, priceType, acceptNego, lat, lng)) {
            return
        }
        btn_save.showLoading()
        Log.e("add_adv","$title, $description, $categoryId, $cityId, $priceType, $acceptNego, $allowRep, $offerLoc")

        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_CREATE_ADVERT, Response.Listener { response ->
                btn_save.hideLoading()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()

                    } else {
                        Log.e("add_advert_1", obj.getString("message"))
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                btn_save.hideLoading()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["title"] =title
                params["description"] =description
                params["price type"] = priceType
                params["price"] = price
                params["accept_negotiation"] = acceptNego
                params["allow_reply"] = allowRep
                params["allow_offer_location"] =offerLoc
                params["lat"] = "123456"
                params["lng"] = "123456"
                params["category_id"] = categoryId
                params["city_id"] = cityId
                params["images"] = "1"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

                return map
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

    private fun validForm(title: String, description: String, categoryId: String, cityId: String, price: String, priceType: String, negotiation: String,  lat: Long?, lng: Long?): Boolean {
        var valid = true

        when {
            title.isEmpty() -> {
                et_title.error = getString(R.string.enter_title)
                et_title.requestFocus()
                valid = false

            }
            description.isEmpty() -> {
                et_detail.error = getString(R.string.enter_detail)
                et_detail.requestFocus()
                valid = false

            }
            categoryId=="" -> {
             Toast.makeText(this,getString(R.string.enter_category),Toast.LENGTH_LONG).show()
                valid = false

            }
            cityId=="" -> {
             Toast.makeText(this,getString(R.string.enter_city),Toast.LENGTH_LONG).show()
                valid = false

            }
            priceType.isEmpty()->{
             Toast.makeText(this,getString(R.string.choose_price),Toast.LENGTH_LONG).show()
                valid = false
            }
            priceType=="fixed" ->{
                 if(price.isEmpty()){
                     et_price.error=getString(R.string.enter_price)
                     valid = false
                 }
            }
        }
        return valid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
               cityId = data!!.getStringExtra("cityId")!!
              var city = data.getStringExtra("city")!!
                tv_cityTit.text=city
            }
            if (resultCode == RESULT_CANCELED) {
                tv_cityTit.text=getString(R.string.select_city)
            }
        }
    }
}