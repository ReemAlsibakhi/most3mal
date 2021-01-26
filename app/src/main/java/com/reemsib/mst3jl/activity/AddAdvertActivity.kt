package com.reemsib.mst3jl.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ImageTypeAdapter
import com.reemsib.mst3jl.fragment.ModelBottomSheetFragment
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.model.Image
import com.reemsib.mst3jl.model.SubCategory
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.Constants
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_add_advert.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException


class AddAdvertActivity : AppCompatActivity(), IPickResult, View.OnClickListener{
    private var imgsArray = ArrayList<Image>()
    private var imageTypeAdapter: ImageTypeAdapter? = null
    private var categoryId:String =""
    private var cityId: String =""
    private  var yearId :String=""
    private var array:ArrayList<File> ?=null
    private var fileImg:File?=null
    private lateinit var advert:Advert
    private lateinit var manager: PreferencesManager
    var priceType: String =""
    var  title :String?=null
    var description:String?=null
    var price:String ?=null
    lateinit var  acceptNego:String
    lateinit var  allowRep :String
    lateinit var  allowShowMobile :String
    var offerLoc :String="0"
    lateinit var  fromUpdate :String
    private var customDialogModel = ModelBottomSheetFragment()
    private var subCat:SubCategory?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_advert)
        linear_upload.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        select_category.setOnClickListener(this)
        select_city.setOnClickListener(this)
        select_model_Car.setOnClickListener(this)
        btn_back_add.setOnClickListener(this)
        manager= PreferencesManager(applicationContext)
        initRecyImagesUpdate()
        val i=intent
        array= ArrayList<File>()
        if (i !=null){
            fromUpdate=i.getStringExtra(Constants.UPDATE)!!
            if (fromUpdate=="1"){
                Log.e("update_003", "1")
                advert=i.getParcelableExtra<Advert>(Constants.ADVERT)!!
                btn_save.setText(getString(R.string.update))
                fillFields(advert)
            }else{
                btn_save.setText(getString(R.string.save))
            } }
        allow_location.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                startActivity(Intent(this, MapsActivity::class.java))
            }
        })
    }


    private fun fillFields(advert: Advert) {
        et_title.setText(advert.title)
        et_detail.setText(advert.description)
        tv_category.setText(advert.category.name)
        tv_cityTit.setText(advert.city.title)
        categoryId=advert.category.id.toString()
        cityId=advert.city.id.toString()
        if (advert.category.has_models==1){
            select_model_Car.visibility=View.VISIBLE
            if (advert.year!=null){
                yearId=advert.year!!.id.toString()
                tv_model.setText(advert.year!!.year)
            }
        }else{
            select_model_Car.visibility=View.GONE
        }
        Log.e("advert_id", advert.id.toString())
        when(advert.price_type){
            "fixed" -> {
                rb_fixed.isChecked = true
                et_price.setText(advert.price.toString())
                Log.e("adv_type", advert.adv_type)
            }
//            "un_limited" -> {
//                rb_undefined.isChecked = true
//            }
            "on_somme" -> {
                rb_soom.isChecked = true
            }
        }

        if (advert.accept_negotiation==1){
            cb_negotiation.isChecked=true
        }
        if (advert.allow_reply==1){
            allow_reply.isChecked=true
        }
        if (advert.allow_offer_location==1){
            allow_location.isChecked=true
        }
        if (advert.allow_to_show_mobile==1){
            allow_mobile.isChecked=true
        }
        imgsArray=advert.images
        initRecyImagesUpdate()
        imageTypeAdapter!!.notifyDataSetChanged()

        for (i in 0 until imgsArray.size ){
            array!!.add(File(imgsArray[i].image))
        }
        Log.e("array: ", "$array")
    }
    override fun onPickResult(uri: PickResult?) {
        if (uri!!.error == null) {
                if(imgsArray.size < 10){
                    Log.e("size_list_update", "${imgsArray.size}")
                    imgsArray.add(Image(0, uri.uri.toString()))
                    fileImg =  File(uri.getPath());
                    array!!.add(fileImg!!)
                    imageTypeAdapter!!.notifyDataSetChanged()
//                    initRecyImagesUpdate()
            }else{
                Toast.makeText(applicationContext, "الحد المسموح عشر صور فقط ", Toast.LENGTH_LONG).show()
            }

        } else {
            //Handle possible errors
            Toast.makeText(this, uri.getError().message, Toast.LENGTH_LONG).show();
        }
    }
    private fun initRecyImagesUpdate() {
        rv_imgsAdvert.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
        imageTypeAdapter= ImageTypeAdapter(this, imgsArray)
        rv_imgsAdvert.adapter=imageTypeAdapter
        imageTypeAdapter!!.setOnItemClickListener(object : ImageTypeAdapter.OnItemClickListener {
            override fun onClicked(position: Int) {
                    imgsArray.removeAt(position)
                    rv_imgsAdvert.removeViewAt(position);
                    imageTypeAdapter!!.notifyItemRemoved(position)
                    imageTypeAdapter!!.notifyItemRangeChanged(position, imgsArray.size)
                    imageTypeAdapter!!.notifyDataSetChanged();
                    array!!.removeAt(position)

            }

        })
    }
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_back_add->{
                finish()
            }
            R.id.linear_upload -> {
                PickImageDialog.build(PickSetup()).show(this)
            }
            R.id.btn_save -> {
                if (intent.getStringExtra(Constants.UPDATE) == "1") {
                    Log.e("btn_update", "1")
                    updateAdvert()
                } else {
                    addAdvert()
                }

            }
            R.id.select_category -> {
                startActivityForResult(Intent(this, CategoryActivity::class.java), 2)
            }
            R.id.select_city -> {
                val i = Intent(this, CitiesActivity::class.java)
                startActivityForResult(i, 1)
//                startActivityForResult(Intent(this, CitiesActivity::class.java), 1);
            }
            R.id.select_model_Car->{
              showDialogSheet()
           }

        }
    }
    private fun showDialogSheet() {
        customDialogModel.setModelInterface(object :
            ModelBottomSheetFragment.ModelBottomSheetInterface {
            override fun sendData(position:Int,id: Int, model: String) {
                if (position==0){
                    yearId=""
                    tv_model.text = getString(R.string.select_model)

                }else{
                    yearId = id.toString()
                    tv_model.text = model
                }
                customDialogModel.dismiss()
//                filterAdverts()
            }

        })
        customDialogModel.apply { show(supportFragmentManager, ModelBottomSheetFragment.TAG)
        }
    }
    private fun updateAdvert() {
        if(!validForm()){
            return }

        btn_save.showLoading()
        val client = AsyncHttpClient()
        val BASE_URL = "http://mst3jl.com/api/updateAdvertisement/"
        client.addHeader("Accept", "application/json")
        client.addHeader(
            "Authorization",
            "Bearer " + PreferencesManager(applicationContext).getAccessToken()
        )
        client.connectTimeout = 10 * 1000 * 60
        client.responseTimeout = 10 * 1000 * 60

        val params = RequestParams()
        params.put("title", title)
        params.put("description", description)
        params.put("price type", priceType)
        params.put("price", price)
        params.put("accept_negotiation", acceptNego)
        params.put("allow_reply", allowRep)
        params.put("allow_offer_location", offerLoc)
        params.put("lat", "123456")
        params.put("lng", "123456")
        params.put("category_id", categoryId)
        params.put("city_id", cityId)
        params.put("year_id", yearId)
        params.put("allow_to_show_mobile", allowShowMobile)

        for (i in 0 until array!!.size) {
            try {
                params.put("images[$i]", array!!.get(i))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        Log.e("array_hh", "array: UPDATE $array")

        client.post(BASE_URL + advert.id, params, object : JsonHttpResponseHandler() {
            override fun onProgress(bytesWritten: Long, totalSize: Long) {
                super.onProgress(bytesWritten, totalSize)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.e("update_advert", response.toString())
                btn_save.hideLoading()
                btn_save.setText(getString(R.string.update))
                try {
                    val status = response.getString("status")
                    if (status == "true") {
                        val message = response.getString("message")
                        Toast.makeText(
                            applicationContext,
                            "تمت تحديث الاعلان بنجاح ",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("success_update", message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(Constants.ADVERT_ID, advert.id)
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    } else {
                        val message = response.getString("message")
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                        Log.e("failed_update", message)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                super.onFinish()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONObject
            ) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                try {
                    btn_save.hideLoading()
                    btn_save.setText(getString(R.string.update))
                    Log.e("errorResponse", errorResponse.toString() + "")
                    //   GlobalSettings.makeToast(getActivity(), errorResponse.getString("message"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONArray
            ) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                btn_save.hideLoading()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                super.onFailure(statusCode, headers, responseString, throwable)
                btn_save.hideLoading()
            }

            override fun onUserException(error: Throwable) {
                //  super.onUserException(error);
            }
        }) }
    private fun addAdvert() {
         if(!validForm()){
             return
         }
        btn_save.showLoading()
        val client = AsyncHttpClient()
        val BASE_URL = "http://mst3jl.com/api/createAdvertisement"
        client.addHeader("Accept", "application/json")
        client.addHeader(
            "Authorization",
            "Bearer " + PreferencesManager(applicationContext).getAccessToken()
        )
        client.connectTimeout = 10 * 1000 * 60
        client.responseTimeout = 10 * 1000 * 60

        val params = RequestParams()
        params.put("title", title)
        params.put("description", description)
        params.put("price type", priceType)
        params.put("price", price)
        params.put("accept_negotiation", acceptNego)
        params.put("allow_reply", allowRep)
        params.put("allow_offer_location", offerLoc)
        params.put("lat", manager.getLat())
        params.put("lng",manager.getlng())
        params.put("category_id", categoryId)
        params.put("city_id", cityId)
        params.put("year_id", yearId)
        params.put("allow_to_show_mobile", allowShowMobile)

        for (i in 0 until array!!.size) {
            try {
                params.put("images[$i]", array!!.get(i))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        client.post(BASE_URL, params, object : JsonHttpResponseHandler() {
            override fun onProgress(bytesWritten: Long, totalSize: Long) {
                super.onProgress(bytesWritten, totalSize)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.e("add_advert", response.toString())
                btn_save.hideLoading()
                btn_save.setText(getString(R.string.save))
                try {
                    val status = response.getString("status")
                    if (status == "true") {
                        val message = response.getString("message")
                        Toast.makeText(
                            applicationContext,
                            "تمت اضافة الاعلان بنجاح ",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("success_add", message)
                        finish()

                    } else {
                        val message = response.getString("message")
                        Log.e("failed_add", message)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                super.onFinish()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONObject
            ) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                try {
                    btn_save.hideLoading()
                    btn_save.setText(getString(R.string.save))
                    Log.e("errorResponse", errorResponse.toString() + "")
                    //   GlobalSettings.makeToast(getActivity(), errorResponse.getString("message"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONArray
            ) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                btn_save.hideLoading()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                super.onFailure(statusCode, headers, responseString, throwable)
                btn_save.hideLoading()
            }

            override fun onUserException(error: Throwable) {
                //  super.onUserException(error);
            }
        })
    }
    private fun validForm():Boolean{
        var valid = true

         title = et_title.text.toString()
         description=et_detail.text.toString()
         price = et_price.text.toString()
         val category=tv_category.text.toString()
         val city=tv_cityTit.text.toString()
         val model=tv_model.text.toString()

        val negotiation = cb_negotiation.isChecked
        val allowReply = allow_reply.isChecked
        val allowLocation = allow_location.isChecked
        val allowMobile = allow_mobile.isChecked
        when(rg_price.checkedRadioButtonId){
            R.id.rb_fixed -> {
                priceType = "fixed"

            }
            R.id.rb_soom -> {
                price = "0"
                priceType = "on_somme"

            }
//            R.id.rb_undefined -> {
//                price = "0"
//                priceType = "un_limited"
//
//            }
        }
          acceptNego = if(negotiation){ "1" }else{ "0" }
          allowRep = if(allowReply){ "1" }else{ "0" }
          offerLoc = if(allowLocation){ "1" }else{ "0" }
          allowShowMobile = if(allowMobile){ "1" }else{ "0" }

        when {
            array!!.isEmpty()-> {
                Toast.makeText(this, getString(R.string.add_imgs_add), Toast.LENGTH_LONG).show()
                Log.e("add_advert_not_update", "rrr")
                valid = false
            }
            title!!.isEmpty() -> {
                et_title.error = getString(R.string.enter_title)
                et_title.requestFocus()
                valid = false
            }
            description!!.isEmpty()-> {
                et_detail.error = getString(R.string.enter_detail)
                et_detail.requestFocus()
                valid = false

            }
            category.equals(getString(R.string.select_category)) -> {
             Toast.makeText(this, getString(R.string.enter_category), Toast.LENGTH_LONG).show()
                valid = false

            }
           city.equals(getString(R.string.select_city)) -> {
             Toast.makeText(this, getString(R.string.enter_city), Toast.LENGTH_LONG).show()
                valid = false

            }

            priceType.isEmpty()->{
                 Toast.makeText(this, getString(R.string.choose_price), Toast.LENGTH_LONG).show()
                valid = false
            }
            priceType=="fixed" && price!!.isEmpty()-> {
//                if (price!!.isEmpty()) {
                et_price.error = getString(R.string.enter_price)
                valid = false
//                }
            }
            offerLoc=="0"->{
                Toast.makeText(this, getString(R.string.allow_location), Toast.LENGTH_LONG).show()
                valid = false
            }
            subCat!!.has_models==1 && yearId=="" -> {
                    Toast.makeText(this, getString(R.string.select_model), Toast.LENGTH_LONG).show()
                    valid = false

            }


        }
        return valid
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                 cityId = data!!.getStringExtra("cityId")!!
                 val city = data.getStringExtra("city")!!
                 val position = data.getIntExtra("position", -1)
                 if (position==0){
                     cityId=""
                     tv_cityTit.text=getString(R.string.select_city)
                 }else{
                     tv_cityTit.text=city
                 }

            }
            if (resultCode == RESULT_CANCELED) {
             //   tv_cityTit.text=getString(R.string.select_city)
            }
        }else if(requestCode==2){
            if (resultCode == RESULT_OK) {
                subCat = data!!.getParcelableExtra<SubCategory>("category")!!
                categoryId= subCat!!.id.toString()
                tv_category.text= subCat!!.name
                if (subCat!!.has_models==1){
                    select_model_Car.visibility=View.VISIBLE
                }else{
                    select_model_Car.visibility=View.GONE

                }
            }
            if (resultCode == RESULT_CANCELED) {
               // tv_category.text=getString(R.string.select_category)
            }
        }
    }


}