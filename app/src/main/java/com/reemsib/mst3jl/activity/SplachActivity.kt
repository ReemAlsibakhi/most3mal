package com.reemsib.mst3jl.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.setting.PreferencesManager

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