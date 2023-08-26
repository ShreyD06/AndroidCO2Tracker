package com.shreyd.co2tracker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.*
import com.shreyd.co2tracker.databinding.ActivityTempMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class TempMain : AppCompatActivity(), EasyPermissions.PermissionCallbacks  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTempMainBinding

    lateinit var client: ActivityRecognitionClient
    lateinit var storage: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTempMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarTempMain.toolbar)

        binding.appBarTempMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_temp_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_ptransport, R.id.nav_coffsets
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        intent.action = "MYLISTENINGACTION"
        val events: MutableList<ActivityTransitionEvent> = ArrayList()
        var transitionEvent: ActivityTransitionEvent
        transitionEvent = ActivityTransitionEvent(
            DetectedActivity.STILL,
            ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        transitionEvent = ActivityTransitionEvent(
            DetectedActivity.IN_VEHICLE,
            ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        transitionEvent = ActivityTransitionEvent(
            DetectedActivity.IN_VEHICLE,
            ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        val result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(
            result, intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )
        sendBroadcast(intent)


        // The Activity Recognition Client returns a
        // list of activities that a user might be doing
        client = ActivityRecognition.getClient(this)

        // variable to check whether the user have already given the permissions
        storage = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)

        // check for devices with Android 10 (29+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            // check for permission
            && !ActivityTransitionsUtil.hasActivityTransitionPermissions(this)
        ) {
            // request for permission
            requestActivityTransitionPermission()
        } else {
            // when permission is already allowed
            requestForUpdates()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.temp_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_temp_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe
    fun onDataReceived(event: ActivityTransitionReceiver.OnReceiverEvent) {
        // These 2 lines are for debug purposes
        print(event.getEvents())
        print("----------GOING TO SERVICE-------------")

        val serviceIntentEnter: Intent = Intent(this, MyService::class.java).apply {
            action=MyService.ENTER
        }
        val serviceIntentExit: Intent = Intent(this, MyService::class.java).apply {
            action=MyService.EXIT
        }

        fun isLocationEnabled(): Boolean {
            val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }

        fun requestPermission() {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }

        fun checkPermissions(): Boolean {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                return true
            }
            return false
        }
        if(checkPermissions()) {
            if(isLocationEnabled()) {
                //latitude and longitude shown here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    println("Second check passed")
                }
                if(event.getEvents()[1] == "ENTER") {
                    startForegroundService(serviceIntentEnter)
                }

                else {
                    startForegroundService(serviceIntentExit)
                }
            }
            else {
                //settings open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else {
            //request permission
            requestPermission()
        }
    }

    // when permission is denied
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // permission is denied permanently
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }

    // after giving permission
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        requestForUpdates()
    }

    // request for permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)


    }

    // To register for changes we have to also supply the requestActivityTransitionUpdates() method
    // with the PendingIntent object that will contain an intent to the component
    // (i.e. IntentService, BroadcastReceiver etc.) that will receive and handle updates appropriately.
    private fun requestForUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            println("Not registered, reboot app")
        }
        else {
            client
                .requestActivityTransitionUpdates(
                    ActivityTransitionsUtil.getActivityTransitionRequest(),
                    getPendingIntent()
                )
                .addOnSuccessListener {
                    showToast("successful registration")
                    println("REGISTERED")
                }
                .addOnFailureListener {
                    showToast("Unsuccessful registration")
                    println("NOT REGISTERED")
                }

        }

    }

    // Deregistering from updates
    // call the removeActivityTransitionUpdates() method
    // of the ActivityRecognitionClient and pass
    // ourPendingIntent object as a parameter
    private fun deregisterForUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            println("Not registered, reboot app")
        }
        else {
            client
                .removeActivityTransitionUpdates(getPendingIntent())
                .addOnSuccessListener {
                    getPendingIntent().cancel()
                    showToast("successful deregistration")
                }
                .addOnFailureListener { e: Exception ->
                    showToast("unsuccessful deregistration")
                }
        }
    }

    // creates and returns the PendingIntent object which holds
    // an Intent to an BroadCastReceiver class
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            Constants.REQUEST_CODE_INTENT_ACTIVITY_TRANSITION,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }



    // requesting for permission
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestActivityTransitionPermission() {
        EasyPermissions.requestPermissions(
            this,
            "You need to allow Activity Transition Permissions in order to recognize your activities",
            Constants.REQUEST_CODE_ACTIVITY_TRANSITION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
    }
}