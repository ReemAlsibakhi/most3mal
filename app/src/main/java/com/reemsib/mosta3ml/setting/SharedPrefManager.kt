//package com.reemsib.mosta3ml.setting
//
//import android.content.Context
//import android.content.Intent
//import com.reemsib.mosta3ml.activity.TabActivity
//import com.reemsib.mosta3ml.model.User
//
//class SharedPrefManager
//private constructor(context: Context) {
//
//    init {
//        ctx = context
//    }
//
//    //this method will checker whether user is already logged in or not
//    val isLoggedIn: Boolean
//        get() {
//            val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//            return sharedPreferences?.getString(KEY_USERNAME, null) != null
//        }
//
//    //this method will give the logged in user
//    val user: User
//        get() {
//            val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//            return User(
//             //   sharedPreferences!!.getInt(KEY_ID, -1),
//                sharedPreferences!!.getString(KEY_USERNAME, null),
//                sharedPreferences.getString(KEY_EMAIL, null),
//                sharedPreferences.getString(KEY_MOBILE, null)
//
//            )
//        }
//
//
//    //this method will store the user data in shared preferences
//    fun userLogin(user: User) {
//        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//        val editor = sharedPreferences?.edit()
//        editor?.putString(KEY_USERNAME, user.username)
//        editor?.putString(KEY_EMAIL, user.email)
//        editor?.putString(KEY_MOBILE, user.mobile)
//        editor?.commit()
//    }
//
//    //this method will logout the user
//    fun logout() {
//        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//        val editor = sharedPreferences?.edit()
//        editor?.clear()
//        editor?.apply()
//        ctx?.startActivity(Intent(ctx, TabActivity::class.java))
//    }
//
//    companion object {
//
//        private val SHARED_PREF_NAME = "volleyregisterlogin"
//        private val KEY_USERNAME = "keyusername"
//        private val KEY_EMAIL = "keyemail"
//        private val KEY_MOBILE = "keymobile"
//        private val KEY_GENDER = "keygender"
//        private val KEY_CALLING_CODE = "keycode"
//        private val KEY_ID = "keyid"
//
//        private var mInstance: SharedPrefManager? = null
//        private var ctx: Context? = null
//        @Synchronized
//        fun getInstance(context: Context): SharedPrefManager {
//            if (mInstance == null) {
//                mInstance = SharedPrefManager(context)
//            }
//            return mInstance as SharedPrefManager
//        }
//    }
//}