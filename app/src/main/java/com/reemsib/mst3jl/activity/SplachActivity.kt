package com.reemsib.mst3jl.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.adapter.ChatsAdapter
import com.reemsib.mst3jl.model.ChatRoom
import com.reemsib.mst3jl.setting.MySingleton
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.Constants
import com.reemsib.mst3jl.utils.URLs
import org.json.JSONException
import org.json.JSONObject

class SplachActivity : AppCompatActivity() {

    private lateinit var manager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)

        manager= PreferencesManager(applicationContext)

        Handler().postDelayed({
            //if the user is already logged in we will directly start the MainActivity (profile) activity
            if (manager.isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }

        }, 4000)

    }


}