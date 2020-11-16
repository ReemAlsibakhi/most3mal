package com.reemsib.mosta3ml.setting

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.activity.LoginActivity
import com.reemsib.mosta3ml.utils.Constants

class MySession private constructor(context: Context) {

    init {
        ctx = context
        Hawk.init(ctx).build()
    }

    companion object {
     //   private var mLogin: Boolean = Hawk.get(Constants.ISLoggedIn,false)
        private var mInstance: MySession? = null
        private var ctx: Context? = null
        var token:String ?=null
        var userId:Int ?=null

        @Synchronized
        fun getInstance(context: Context): MySession {
            if (mInstance == null) {
                mInstance = MySession(context)
            }
            return mInstance as MySession
        }
    }

        fun setLogin(login: Boolean) {
           Hawk.put(Constants.ISLoggedIn,login)
        }

        fun isLoggedIn(): Boolean {
            return Hawk.get(Constants.ISLoggedIn,false)
        }

        fun Logout() {
            setLogin(false)
            val intent = Intent(ctx, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx!!.startActivity(intent)
        }

        fun getToken(): String {
//            if(Hawk.contains(Constants.TOKEN))
             token = Hawk.get(Constants.TOKEN, null) as String
             return token!!
        }
       fun getUserId(): Int {
             userId = Hawk.get(Constants.USERID, null) as Int
             return userId!!
        }
//       fun getFcmToken():String{
//           FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//               if (!task.isSuccessful) {
//                   Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                   return@OnCompleteListener
//               }
//
//               // Get new FCM registration token
//               val fcmToken = task.result
//
//               // Log and toast
//               val msg =  token
//               Log.d("TAG", msg!!)
//               Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//           })
//       return fcmToken!!
//       }
}