package com.reemsib.mst3jl.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.reemsib.mst3jl.activity.LoginActivity
import com.reemsib.mst3jl.model.Login
import com.reemsib.mst3jl.model.User
import com.reemsib.mst3jl.utils.Constants


class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var ctx: Context? = null

    init {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
        ctx=context
    }
    //this method will checker whether user is already logged in or not
    val isLoggedIn = preferences.getBoolean(Constants.ISLoggedIn, false)

    fun setLogin(login: Boolean) {
        editor.putBoolean(Constants.ISLoggedIn, login).commit()
      //  editor.commit()
    }
    fun setFcmToken(fcm: String){
        editor.putString(Constants.FCM_TOKEN, fcm).commit()
        editor.commit()
        Log.e("fcm token:",fcm)
    }
    fun gettFcmToken():String{
       return preferences.getString(Constants.FCM_TOKEN, "null")!!
        Log.e("fcm token:",preferences.getString(Constants.FCM_TOKEN, "null")!!)

    }
    fun setLocation(latLng: LatLng){
        editor.putDouble("lat",latLng.latitude )
        editor.putDouble("lng",latLng.longitude)
        editor.commit()
        Log.e("lat lng:","$latLng")
    }
    fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))
    fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))


    fun getLat():Double{
        return preferences.getDouble("lat",0.0)

    }
    fun getlng():Double{
        return preferences.getDouble("lng",0.0)
    }
    fun Logout() {
        setLogin(false)
        val intent = Intent(ctx, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx!!.startActivity(intent)
    }
    //this method will store the user data in shared preferences
    fun setAccessToken(token:String){
        editor.putString(Constants.ACCESS_TOKEN, token).commit()
        editor.commit()
        Log.e("access token:",token)
    }
    fun getAccessToken():String{
        return preferences.getString(Constants.ACCESS_TOKEN, "null")!!
        Log.e("access token:",preferences.getString(Constants.ACCESS_TOKEN, "null")!!)

    }
    fun setRevsCount(countRevs:Int){
        editor.putInt(Constants.COUNT_REVIEWS, countRevs).commit()
        editor.commit()
        Log.e("count not Reviews:","$countRevs")
    }
    fun getRevsCount():Int{
        return preferences.getInt(Constants.COUNT_REVIEWS,0)!!
        //  Log.e("count not Reviews:",preferences.getString(Constants.ACCESS_TOKEN, "null")!!)

    }
    fun setChatsCount(countChats:Int){
        editor.putInt(Constants.COUNT_CHATS, countChats).commit()
        editor.commit()
        Log.e("count not chats:","$countChats")
    }

    fun getChatsCount():Int{
        return preferences.getInt(Constants.COUNT_CHATS,0)!!
   //     Log.e("count not chats",preferences.getString(Constants.ACCESS_TOKEN, "null")!!)

    }


    fun setUser(user: User) {
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString("MyObject", json)
        editor.commit()
    }
    //this method will give the logged in user
   fun getUser(): User {
        val gson = Gson()
        val json: String = preferences.getString("MyObject", "")!!
        val obj: User = gson.fromJson(json, User::class.java)
        return obj
    }
    fun isNotificationEnabled(): Boolean {
        return preferences.getBoolean(Constants.NOTIFICATION, false)
    }

    fun setIsNotificationEnabled(state: Boolean) {
        editor.putBoolean(Constants.NOTIFICATION, state)
        editor.apply()
    }
    fun isFirstRun() = preferences.getBoolean(FIRST_TIME, true)

    fun setFirstRun(v:Boolean) {
        editor.putBoolean(FIRST_TIME, v).commit()
        editor.commit()
    }

    companion object {
        private const val PRIVATE_MODE = 0
        private const val PREFERENCE_NAME = "configuration"
        private const val FIRST_TIME = "isFirstRun"
    }

}