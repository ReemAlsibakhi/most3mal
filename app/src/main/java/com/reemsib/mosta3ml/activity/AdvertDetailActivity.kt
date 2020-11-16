package com.reemsib.mosta3ml.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.chat.ChatRoomActivity
import com.reemsib.mosta3ml.adapter.CommentAdapter
import com.reemsib.mosta3ml.adapter.ReportAdapter
import com.reemsib.mosta3ml.adapter.SliderAdvertDetailAdapter
import com.reemsib.mosta3ml.fragment.ReportBottomSheetFragment
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.model.AdvertImage
import com.reemsib.mosta3ml.model.Reviews
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_advert_detail.*
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
    var AdverterId:Int ? = 0
    var name:String ? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_detail)
        Hawk.init(this).build()

        advertId=intent.getIntExtra(Constants.ADVERT_ID, -1)

        getAdvertInfo(advertId!!)

        expandable_detail.setOnClickListener(this)
        expandable_advertiserInfo.setOnClickListener(this)
        img_report.setOnClickListener(this)
        tv_newMessage.setOnClickListener(this)
        btn_sendComment.setOnClickListener(this)

        img_edit_ads.visibility=View.GONE
        img_delete_ads.visibility=View.GONE

        rv_Comments.layoutManager= LinearLayoutManager(applicationContext)

//        Log.e("id_user","${MySession.getInstance(this).getUserId()},${AdverterId}")

//        if(MySession.getInstance(this).getUserId() == AdverterId){
//            img_edit_ads.visibility=View.VISIBLE
//            img_delete_ads.visibility=View.VISIBLE
//            Log.e("id_user","${MySession.getInstance(this).getUserId()},${AdverterId}")
//        }

    }
    override fun onClick(p0: View?) {
      when(p0!!.id){
          R.id.expandable_detail -> {
              expandableLayout.toggle()
          }
          R.id.expandable_advertiserInfo -> {
              expandableLinear.toggle()
          }
          R.id.img_report -> {
              ReportBottomSheetFragment().apply {
                  show(supportFragmentManager, ReportBottomSheetFragment.TAG)
              }
          }
          R.id.tv_newMessage -> {
              newChat(AdverterId)
          }
          R.id.btn_sendComment -> {
              if (MySession.getInstance(applicationContext).isLoggedIn()) {
                  sendComment()
              } else {
                  startActivity(Intent(this, LoginActivity::class.java))
              }
          }
      }

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
                        //     Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show()
                        Toast.makeText(this, getString(R.string.comment_added), Toast.LENGTH_LONG).show()
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
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

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
            review==0->{
                Toast.makeText(this, getString(R.string.enter_star), Toast.LENGTH_LONG).show()
                valid=false
            }
        }
    return valid
    }

    private fun newChat(userId: Int?) {

        if (!MySession.getInstance(applicationContext).isLoggedIn()){
            val i = Intent(this, LoginActivity::class.java)
            Hawk.put(Constants.PAGE_DETAIL, 1)
            i.putExtra("advId", advertId)
            Log.e("detail_page", " ${Hawk.get(Constants.PAGE_DETAIL, 0) as Any}")
            startActivity(i)

            return
        }
       // Toast.makeText(applicationContext, userId + name, Toast.LENGTH_SHORT).show()
        val stringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_NEW_CHAT,
            Response.Listener { response ->

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val jsonObj = obj.getJSONObject("chat")
                        val chat_id = jsonObj.getInt("id")
                        val user1_id = jsonObj.getInt("user1_id")
                        val user2_id = jsonObj.getInt("user2_id")

                        val i = Intent(this, ChatRoomActivity::class.java)
                        i.putExtra(Constants.CHAT_LOG, "detail")

                        i.putExtra(Constants.ADVERTER_ID, userId!!)
                        i.putExtra(Constants.CHAT_ID, chat_id)
                        i.putExtra(Constants.ADVERTER_NAME, name)
                        i.putExtra(Constants.USER2_ID, user2_id)

                        Log.e("detail_log", "$userId, $chat_id,$name")

                        startActivity(i)
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
            params["user_id"] = userId.toString()

            return params
        }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()

                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

    private fun getAdvertInfo(advertId: Int) {
        avi.show()
        val stringRequest = object : StringRequest(Method.GET,
            URLs.URL_ADVERT_INFO + advertId,
            Response.Listener { response ->
                avi.hide()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val itemsObj = obj.getJSONObject("item")
                        val mJson = JsonParser().parse(itemsObj.toString())
                        val advert = Gson().fromJson<Any>(mJson, Advert::class.java) as Advert
                        fillData(advert)
                        for (i in 0 until advert.images.size) {
                            val jsObj = advert.images[i]
                            val sliderObj = AdvertImage(
                                jsObj.id, jsObj.advertisement_id, jsObj.image,
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
                avi.hide()
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

    private fun scrollToBottom() {
        commentAdapter!!.notifyDataSetChanged()
        if (commentAdapter!!.itemCount > 1)
            rv_Comments.layoutManager!!.smoothScrollToPosition(
                rv_Comments,
                null,
                commentAdapter!!.itemCount - 1
            ) }

    private fun fillData(advert: Advert) {
        tv_advertTitle.text=advert.title
        tv_price.text=advert.price.toString()
        tv_date.text= getFormatDate(advert.created_at)
        tv_description.setText(advert.description)
        tv_advertiser.text=advert.user.name
        tv_city_title.text=advert.city.title
        tv_mobile.text=advert.user.mobile
        tv_whatsapp.text = advert.user.country.calling_code+ advert.user.mobile
        val fstr2 =advert.rate
        val float2: Float? = fstr2.toFloat()
        rb_rating_review.rating = float2!!

        if(advert.accept_negotiation==1){
           tv_priceType.text=getString(R.string.allow_negotiation)
        }
        commentList.clear()
        commentList=advert.reviews
        buildRecComment()

//        tv_email.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("mailto:") // only email apps should handle this
//            intent.putExtra(Intent.EXTRA_EMAIL, email)
//         //   intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback")
//            if (intent.resolveActivity(applicationContext.packageManager) != null) {
//                startActivity(intent)
//            }  }
//        tv_mobile.setOnClickListener {
//            val callIntent = Intent(Intent.ACTION_CALL)
//            callIntent.data = Uri.parse("tel:$mobile") //change the number
//            startActivity(callIntent)
//        }
//        tv_whatsapp.setOnClickListener {  }
    }
    private fun buildRecComment() {
        if(commentList.size==0){
            rv_Comments.visibility=View.GONE
            there_comment.visibility=View.VISIBLE
        }else {
           rv_Comments.visibility = View.VISIBLE
          there_comment.visibility = View.GONE

            commentAdapter = CommentAdapter(this, commentList)
            rv_Comments.adapter = commentAdapter
            commentAdapter!!.notifyDataSetChanged()

            commentAdapter!!.setOnEditClickListener(object : CommentAdapter.OnItemEditListener {
                override fun onClicked(clickedItemPosition: Int, activity: Activity, id: Int, comment: String) {
                    val i = Intent(activity, EditCommentActivity::class.java)
                    i.putExtra(Constants.POSITION, clickedItemPosition)
                    i.putExtra(Constants.REVIEW_Id, id)
                    i.putExtra(Constants.REVIEW_CONTENT, comment)
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
        }
    }
}



