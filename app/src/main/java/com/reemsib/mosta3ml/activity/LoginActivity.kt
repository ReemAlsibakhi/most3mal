package com.reemsib.mosta3ml.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.fcm.MyFirebaseMessagingService
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    var detail:Int ?=null
    var adv_id:Int ?=null
    var token:String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
             token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("TAG", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

       // token=MyFirebaseMessagingService().token

      //  Log.d("TAG_FCM_TOKEN", token!!)

        if (Hawk.contains(Constants.PAGE_DETAIL)){
            detail=Hawk.get(Constants.PAGE_DETAIL)
            adv_id=intent.getIntExtra("advId", -1)

        }
        tv_forgotPwd.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        btn_regiter.setOnClickListener(this)
        tv_skip.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_forgotPwd -> {
                startActivity(Intent(this, ResetPasswordActivity::class.java))
            }

            R.id.btn_login -> {
                login(et_email.text.toString(), et_pswd.text.toString())
            }
            R.id.btn_regiter -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            R.id.tv_skip -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun login(email: String, password: String) {
        if (!validForm(email, password)) {
            return
        }
        btn_login.showLoading()

        val stringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_LOGIN,
            Response.Listener { response ->
                btn_login.hideLoading()
                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        val token = obj.getString("token")
                        val user = obj.getJSONObject("user")
                        val userId=user.getInt("id")

                        MySession.getInstance(applicationContext).setLogin(true)
                        Hawk.put(Constants.TOKEN, token)
                        Hawk.put(Constants.USERID, userId)

                        Toast.makeText(applicationContext, getString(R.string.register_success), Toast.LENGTH_SHORT).show()

                        Log.e("token_login", "${Hawk.get(Constants.TOKEN, null)}")

                        Log.e("id_user", "${Hawk.get(Constants.USERID, null)}")

                        if (detail == 1) {
                            val intent = Intent(applicationContext, AdvertDetailActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra(Constants.ADVERT_ID, adv_id)
                            startActivity(intent)
                            Toast.makeText(applicationContext, "$detail, $adv_id", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }


                    } else {
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                btn_login.hideLoading()
            //    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                params["fcm_token"] = token!!
                params["device_type"] = "android"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"

                return map
            }

        }

        MySingleton.getInstance(this).addToRequestQueue(stringRequest)

    }

  private  fun validForm(email: String, password: String): Boolean {
        var valid = true
        when {
            email.isEmpty() -> {
                et_email.error = getString(R.string.enter_email)
                et_email.requestFocus()
                valid = false

            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                et_email.error = getString(R.string.enter_valid_email)
                et_email.requestFocus()
                valid = false

            }
            password.isEmpty() -> {
                et_pswd.error = getString(R.string.enter_password)
                et_pswd.requestFocus()
                valid = false

            }
            password.length < 6 -> {
                et_pswd.error = getString(R.string.enter_password_Length)
                et_pswd.requestFocus()
                valid = false
            }
        }
        return valid
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Toast.makeText(applicationContext, "$requestCode+Reeeeeeeeem", Toast.LENGTH_SHORT).show()
//        //   if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                detail= data!!.getIntExtra(Constants.PAGE_DETAIL,0)
//                adv_id=data.getIntExtra("advId",0)
//            }
//            if (resultCode == RESULT_CANCELED) {
//              //  tv_cityTit.text=getString(R.string.select_city)
//            }
//        }
    }

//}
