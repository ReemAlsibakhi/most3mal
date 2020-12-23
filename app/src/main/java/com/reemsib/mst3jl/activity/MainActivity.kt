package com.reemsib.mst3jl.activity

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.setting.PreferencesManager
import com.reemsib.mst3jl.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bell_white.*
import android.location.LocationListener;
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private lateinit var manager:PreferencesManager
    var broadcastReceiver: BroadcastReceiver? = null
    var active = false
    private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       manager=PreferencesManager(applicationContext)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_favourite, R.id.navigation_profile, R.id.navigation_more)
        )
         //  setupActionBarWithNavController(navController, appBarConfiguration)
         navView.setupWithNavController(navController)

        btn_addAdvert.setOnClickListener(this)
        relative_noti.setOnClickListener(this)
        Log.e("counter_rev",manager.getRevsCount().toString())
        if (manager.isLoggedIn){
         counter()
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == "notify") {
                    if(active){
                      counter()
                    }
                }
            }
        }

        if (manager.isFirstRun()) {
            requestPermissionLocation()
            Log.e("FistRun","${manager.isFirstRun()}")
        }
        requestLocUpdate()

    }
    fun counter(){
        if ( manager.getRevsCount()!=0){
            badge_notification_sec.visibility=View.VISIBLE
            badge_notification_sec.text=manager.getRevsCount().toString()
        }else{
            Log.e("counter_rev_0",manager.getRevsCount().toString())
            badge_notification_sec.visibility=View.GONE
        }

    }
    private fun requestLocUpdate() {
        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

    }

    private fun requestPermissionLocation() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(
            object :
                PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        checkGPSEnable()
                    } else {
                        requestLocUpdate()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                   manager.setFirstRun(true)
                   finish()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkGPSEnable() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id
                ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            })
//            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
//                dialog.cancel()
//                finish()
           // })
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_addAdvert -> {
                if (manager.isLoggedIn) {
                    val i = Intent(this, AddAdvertActivity::class.java)
                    i.putExtra(Constants.UPDATE, "0")
                    startActivity(i)
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.relative_noti -> {
                if (manager.isLoggedIn) {
                    startActivityForResult(Intent(this, NotificationActivtiy::class.java), 1)
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        active = true
    }
    override fun onStop() {
        super.onStop()
        active = false
    }
    override fun onResume() {
        super.onResume()
        active = true

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver!!,
            IntentFilter("notify")
        )

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val back_noti = data!!.getIntExtra("back_notify",-1)
                if (back_noti==0){
                    Log.e("back", back_noti.toString())
                    manager.setRevsCount(0)
                    counter()
                //   recreate()


                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
             val yourLocation = LatLng(location.latitude, location.longitude)
             manager.setLocation(yourLocation)
            Log.e("My Location","${manager.getLat()},${manager.getlng()}")

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}


