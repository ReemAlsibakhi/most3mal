package com.reemsib.mst3jl.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.activity.chat.ChatRoomActivity
import com.reemsib.mst3jl.adapter.CommentAdapter
import com.reemsib.mst3jl.adapter.ReportAdapter
import com.reemsib.mst3jl.adapter.SliderAdvertDetailAdapter
import com.reemsib.mst3jl.fragment.ReportBottomSheetFragment
import com.reemsib.mst3jl.model.Advert
import com.reemsib.mst3jl.model.AdvertImage
import com.reemsib.mst3jl.model.Reviews
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.BaseActivity
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_advert_detail.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AdvertDetailActivity : AppCompatActivity(),View.OnClickListener {
    var commentList=ArrayList<Reviews>()
    var commentAdapter :CommentAdapter ?= null
    var reportList=ArrayList<String>()
    var reportAdapter :ReportAdapter ?= null
    var sliderAdapter:SliderAdvertDetailAdapter ?=null
    val sliderImg=ArrayList<AdvertImage>()
    var advertId :Int ?= null
    var adverterId:Int ? = null
    var adverterName:String ? = ""
    private lateinit var manager: PreferencesManager
    private lateinit var  advert:Advert
    lateinit var myDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_detail)
        Hawk.init(this).build()
        manager= PreferencesManager(applicationContext)
        myDialog= BaseActivity.loading(AdvertDetailActivity@ this)
        advertId=intent.getIntExtra(Constants.ADVERT_ID, -1)
        getAdvertInfo(advertId!!)
        Log.e("adv_id", "$advertId")
        expandable_detail.setOnClickListener(this)
        expandable_advertiserInfo.setOnClickListener(this)
        img_report.setOnClickListener(this)
        img_delete_ads.setOnClickListener(this)
        tv_newMessage.setOnClickListener(this)
        linear_comment.setOnClickListener(this)
        btn_sendComment.setOnClickListener(this)
        img_edit_ads.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        rv_Comments.layoutManager= LinearLayoutManager(applicationContext)



    }
    override fun onClick(p0: View?) {
      when(p0!!.id){
          R.id.expandable_detail -> {
              TransitionManager.beginDelayedTransition(relative_detail)
              if(relative_detail.visibility==View.GONE){
                  relative_detail.visibility=View.VISIBLE
              }else{
                  relative_detail.visibility=View.GONE
              }
          }
          R.id.expandable_advertiserInfo -> {
              expandableLinear.toggle()
          }
          R.id.img_report -> {
              val args = Bundle()
              args.putInt("advert_id", advertId!!)
              val bottomSheet = ReportBottomSheetFragment()
              bottomSheet.setArguments(args)
              bottomSheet.apply {
                  show(supportFragmentManager, ReportBottomSheetFragment.TAG)
              }
          }
          R.id.tv_newMessage -> {
              newChat(adverterId)
          }
          R.id.btn_sendComment -> {
              if (manager.isLoggedIn) {
                  sendComment()
              } else {
                  startActivity(Intent(this, LoginActivity::class.java))
              }
          }
          R.id.img_delete_ads -> {
              showAlertDialog(advertId)

              Log.e("id_ad", "$advertId")
          }
          R.id.img_edit_ads -> {
              val i = Intent(this, AddAdvertActivity::class.java)
              i.putExtra(Constants.UPDATE, "1")
              i.putExtra(Constants.ADVERT, advert)
              Log.e("advert_update", "$advert")
              startActivityForResult(i, 2)
          }
          R.id.linear_comment -> {
              if (!manager.isLoggedIn) {
                  startActivity(Intent(applicationContext, LoginActivity::class.java))
              }
          }
          R.id.btn_save -> {
              save()
          }
      }
    }

    private fun showAlertDialog(advertId: Int?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.deleteComment)
        builder.setPositiveButton("نعم"){ dialogInterface, which ->
            deleteAdvert(advertId)
        }
        builder.setNeutralButton("إلغاء"){ dialogInterface, which ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun save() {
        if (manager.isLoggedIn){
            if (advert.in_favorite){
                Toast.makeText(this, "هذا الاعلان محفوظ من قبل", Toast.LENGTH_LONG).show()

            }else{
                btn_save.setImageResource(R.drawable.favorite)
                saveAdvert(advertId, manager.getUser().id)
            }
        }else{
            Toast.makeText(this, "من فضلك سجل دخول", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveAdvert(advertId: Int?, id: Int) {
        val stringRequest = object : StringRequest(
            Method.POST, URLs.URL_ADD_FAVORITE,
            Response.Listener { response ->
                Log.e("detail_save_advert", response.toString())
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, getString(R.string.failed_internet), Toast.LENGTH_SHORT).show()
                Log.e("error_listener", error.message.toString())

            }) {
            override fun getPostBodyContentType(): String {
                return "application/json; charset=utf-8";
            }
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = java.util.HashMap<String, String>()
                params2.put("user_id", id.toString())
                params2.put("advertisement_id", advertId.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = java.util.HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"] = "Bearer " + PreferencesManager(applicationContext).getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(AdvertDetailActivity@ this).addToRequestQueue(stringRequest)

    }

    private fun deleteAdvert(advertId: Int?){
        val stringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_DELETE_MY_ADVERT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("delete_success", obj.getString("message"))
                        val resultIntent = Intent()
                        resultIntent.putExtra("delete_advert", "1")
                        setResult(RESULT_OK, resultIntent)
                        Log.e("delete_advert", "1111")
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("delete_failed", obj.getString("message"))
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                btn_login.hideLoading()
                //    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getPostBodyContentType(): String {
                return "application/json; charset=utf-8";
            }
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = java.util.HashMap<String, String>()
                params2.put("id", advertId.toString())
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = java.util.HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                map["Authorization"] = "Bearer " + PreferencesManager(applicationContext).getAccessToken()
                return map
            }
        }
            MySingleton.getInstance(this).addToRequestQueue(stringRequest)

        }
    private fun sendComment() {
        val comment=et_comment.text.toString()
        val rate=rb_review.rating.toInt()
        Log.e("review_star", rate.toString())

        if(!validateComment(comment, rate)){
            return
        }
        et_comment.setText("")
        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_ADD_COMMENT,
            Response.Listener { response ->
                Log.e("comment", response)
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsoObj = obj.getJSONObject("data")
                        val mJson = JsonParser().parse(jsoObj.toString())
                        val rev = Gson().fromJson<Any>(mJson, Reviews::class.java) as Reviews
                        commentList.add(rev)
                        buildRecComment()
                        rv_Comments.requestFocus()
                        scrollToBottom()
                        Toast.makeText(this, getString(R.string.comment_added), Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //  Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params["advertisement_id"]=advertId.toString()
                params["content"] = comment
                params["rate"] = rate.toString()

                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + manager.getAccessToken()

                return map
            }
        }
        //Disabling retry to prevent duplicate messages

        //Disabling retry to prevent duplicate messages
        val socketTimeout = 0
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        stringRequest.retryPolicy = policy
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }
    fun getTimeStamp(): String? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return format.format(Date())
    }
    private fun validateComment(comment: String, review: Int):Boolean{
        var valid = true
        when{
            comment.isEmpty()->{
                et_comment.error=getString(R.string.enter_comment)
                et_comment.requestFocus()
                valid=false
            }
//            review==0->{
//                Toast.makeText(this, getString(R.string.enter_star), Toast.LENGTH_LONG).show()
//                valid=false
//            }
        }
    return valid
    }
    private fun newChat(user2Id: Int?) {
        if (!manager.isLoggedIn){
            val i = Intent(this, LoginActivity::class.java)
            Hawk.put(Constants.PAGE_DETAIL, 1)
            i.putExtra("advId", advertId)
            Log.e("detail_page", " ${Hawk.get(Constants.PAGE_DETAIL, 0) as Any}")
            startActivity(i)
            return
        }
        val stringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_NEW_CHAT,
            Response.Listener { response ->

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonObj = obj.getJSONObject("chat")
                        val chatId = jsonObj.getInt("id")
                        val i = Intent(this, ChatRoomActivity::class.java)
                        i.putExtra(Constants.CHAT_LOG, "detail")
                        i.putExtra(Constants.CHAT_ID, chatId)
                        i.putExtra(Constants.ADVERTER_NAME, adverterName)
                        Log.e("new_chat", "$user2Id, $chatId,$adverterName")
                        startActivity(i)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): MutableMap<String, String> {
            val params = HashMap<String, String>()
            params["user_id"] = user2Id.toString()
            return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + manager.getAccessToken()
                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }
    private fun getAdvertInfo(advertId: Int) {
        showDia()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_ADVERT_INFO + advertId,
            Response.Listener { response ->
               finishDia()
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        val itemsObj = obj.getJSONObject("item")
                        val mJson = JsonParser().parse(itemsObj.toString())
                        advert = Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                        Log.e("advertDetailActivity", "$advert")
                        adverterId = advert.user.id
                        adverterName = advert.user.name
                        Hawk.put(Constants.ADVERTER_ID, advert.user.id)
                        fillData(advert)
                        showIcon(adverterId!!)
                        for (i in 0 until advert.images.size) {
                            val jsObj = advert.images[i]
                            val sliderObj = AdvertImage(
                                jsObj.id, advert.id, jsObj.image,
                                advert.adv_type, advert.view_number
                            )
                            sliderImg.add(sliderObj)
                        }
                        buildSlider()
                    } else {
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                finishDia()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"

                return map
            }

        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }
    private fun showIcon(id: Int) {
        if(manager.isLoggedIn){
            if (manager.getUser().id==id){
                img_report.visibility=View.GONE
                adverterRating.visibility=View.GONE
                img_edit_ads.visibility=View.VISIBLE
                img_delete_ads.visibility=View.VISIBLE
            }else{
                img_report.visibility = View.VISIBLE
                adverterRating.visibility = View.VISIBLE
                img_edit_ads.visibility = View.GONE
                img_delete_ads.visibility = View.GONE
//                et_comment.isEnabled=true
            }
        }else{
            img_edit_ads.visibility=View.GONE
            img_delete_ads.visibility=View.GONE
            img_report.visibility=View.VISIBLE
//            et_comment.isEnabled=false

        }
    }
    private fun scrollToBottom() {
        commentAdapter!!.notifyDataSetChanged()
        rv_Comments.scrollToPosition(commentList.size-1)
    }
    private fun fillData(advert: Advert) {
        tv_advertTitle.text=advert.title
        when(advert.price_type){
            "fixed" -> {
                tv_price.text = advert.price.toString() + getString(R.string.rial)
            }
            "un_limited" -> {
                tv_price.text = getString(R.string.not_detect)
            }
            "on_somme" -> {
                tv_price.text = getString(R.string.soom)
            }
            }
        tv_date.text= getFormatDate(advert.created_at)
        tv_description.setText(advert.description)
        tv_advertiser.text=advert.user.name
        tv_city_title.text=advert.city.title
        tv_mobile.text=advert.user.mobile
        tv_whatsapp.text = advert.user.full_number
        val fstr2 =advert.rate
        val float2: Float? = fstr2.toFloat()
        rb_rating_review.rating = float2!!

        if(advert.accept_negotiation==1){
           tv_priceType.text=getString(R.string.allow_negotiation)
        }
        if (advert.allow_to_show_mobile==1){
            linear_mobile.visibility=View.VISIBLE
        } else{
            linear_mobile.visibility=View.GONE
        }

        if (manager.isLoggedIn){
            if(advert.allow_reply==0 &&  !(manager.getUser().id==adverterId ) ){
                    linear_comment.visibility=View.GONE
                    tv_allowReply.visibility=View.VISIBLE
                }else{
                linear_comment.visibility=View.VISIBLE
                tv_allowReply.visibility=View.GONE
            }
        }

        commentList.clear()
        commentList=advert.reviews
        buildRecComment()

        tv_email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto: ${advert.user.email}")
            intent.putExtra(Intent.EXTRA_EMAIL, "test")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hello World")
            startActivity(intent)
            }
        tv_mobile.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + advert.user.mobile)
            startActivity(dialIntent)
        }
      tv_whatsapp.setOnClickListener {
          val url = "https://api.whatsapp.com/send?phone="+advert.user.mobile
          val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse(url)
          startActivity(i)
    }

        if (advert.in_favorite){
            btn_save.setImageResource(R.drawable.favorite)
        }else{
            btn_save.setImageResource(R.drawable.save_outline)
        }
    }
    private fun buildRecComment() {
        if(commentList.size==0){
            rv_Comments.visibility=View.GONE
            there_comment.visibility=View.VISIBLE
        }else {
           rv_Comments.visibility = View.VISIBLE
          there_comment.visibility = View.GONE

            commentAdapter = CommentAdapter(this, commentList, adverterId!!)
            rv_Comments.adapter = commentAdapter
            commentAdapter!!.notifyDataSetChanged()

            commentAdapter!!.setOnEditClickListener(object : CommentAdapter.OnItemEditListener {
                override fun onClicked(
                    clickedItemPosition: Int,
                    activity: Activity,
                    reviews: Reviews
                ) {
                    val i = Intent(activity, EditCommentActivity::class.java)
                    i.putExtra(Constants.POSITION, clickedItemPosition)
                    i.putExtra(Constants.REVIEWS, reviews)
                    activity.startActivityForResult(i, 1)
                }

            })
        }
    }
    private fun buildSlider() {
        sliderAdapter=SliderAdvertDetailAdapter(this, sliderImg)
        sliderView.setSliderAdapter(sliderAdapter!!)
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.indicatorUnselectedColor = Color.GRAY
    }
    private fun getFormatDate(createdAt: String):String {
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        return s
    }
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val revObj=data!!.getParcelableExtra<Reviews>(Constants.REVIEW)
                val updateIndex = data.getIntExtra(Constants.POSITION, -1)
                commentList[updateIndex] = revObj!!
                commentAdapter!!.notifyItemChanged(updateIndex)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }else if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                 advertId=data!!.getIntExtra(Constants.ADVERT_ID, -1)
                sliderImg.clear()
                 getAdvertInfo(advertId!!)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

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



