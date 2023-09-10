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
import android.util.Log
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
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shreyd.co2tracker.databinding.ActivityTempMainBinding
import com.shreyd.co2tracker.datastore.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class TempMain : AppCompatActivity(), EasyPermissions.PermissionCallbacks  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTempMainBinding

    lateinit var client: ActivityRecognitionClient
    lateinit var storage: SharedPreferences
    private lateinit var userEmail: String
    private lateinit var dbUsers: DatabaseReference


    private val userDataStore by lazy { UserDataStore.getInstance() }


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

        //TODO("Clear dataStore here")
        CoroutineScope(Dispatchers.Main).launch { userDataStore.clearData() }


        val rawDrives = mutableListOf<Drive>()
        val dbRawDrives = FirebaseDatabase.getInstance().getReference("RawDrives")
        val synthDrives = mutableListOf<Drive>()
        val dbDrives = FirebaseDatabase.getInstance().getReference("Drives")

        val driveListener = object : ValueEventListener {
            var change = 0
            var times = 0
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                times++
                if(times <= 1) {
                    println("NEW LOAD")
                    for (ds in dataSnapshot.children) {
                        println(change)
                        change++
                        val rdrive = com.shreyd.co2tracker.Drive(
                            ds.key,
                            listOf(
                                ds.child("startLoc").child("0").value.toString().toDouble(),
                                ds.child("startLoc").child("1").value.toString().toDouble()
                            ),
                            listOf(
                                ds.child("endLoc").child("0").value.toString().toDouble(),
                                ds.child("endLoc").child("1").value.toString().toDouble()
                            ),
                            ds.child("startTime").value.toString().toLong(),
                            ds.child("endTime").value.toString().toLong()
                        )
                        rawDrives.add(rdrive)
                    }
                    for(i in 0..rawDrives.size - 2) {
                        if(rawDrives[i+1].startTime!! - rawDrives[i].endTime!! < 300000) {
                            //Synthesize drives
                            synthDrives.add(rawDrives[i])
                            if(i == rawDrives.size - 2) {
                                val waypoints = mutableListOf<List<Double?>>()

                                if(synthDrives.size == 1) {
                                    waypoints.add(synthDrives[0].endLoc)
                                }
                                else {
                                    for(k in 1..synthDrives.size - 1) {
                                        waypoints.add(synthDrives[k].startLoc)
                                    }
                                    waypoints.add(rawDrives[i].startLoc)
                                }


                                val newDrive = com.shreyd.co2tracker.Drive(
                                    synthDrives[0].id,
                                    synthDrives[0].startLoc,
                                    rawDrives[i + 1].endLoc,
                                    synthDrives[0].startTime,
                                    rawDrives[i + 1].endTime,
                                    waypoints
                                )

                                dbDrives.child(newDrive.id!!).setValue(newDrive)
                            }
                        }
                        else if(synthDrives.size > 0){

                            val waypoints = mutableListOf<List<Double?>>()

                            if(synthDrives.size == 1) {
                                waypoints.add(synthDrives[0].endLoc)
                            }
                            else {
                                for(k in 1..synthDrives.size - 1) {
                                    waypoints.add(synthDrives[k].startLoc)
                                }
                                waypoints.add(rawDrives[i].startLoc)
                            }


                            val newDrive = com.shreyd.co2tracker.Drive(
                                synthDrives[0].id,
                                synthDrives[0].startLoc,
                                rawDrives[i].endLoc,
                                synthDrives[0].startTime,
                                rawDrives[i].endTime,
                                waypoints
                            )
                            dbDrives.child(newDrive.id!!).setValue(newDrive)

                            if(i == rawDrives.size - 2) {
                                dbDrives.child(rawDrives[i+1].id!!).setValue(rawDrives[i+1])
                            }


                            waypoints.clear()
                            synthDrives.clear()
                        }
                        else {
                            val newDrive = com.shreyd.co2tracker.Drive(
                                rawDrives[i].id,
                                rawDrives[i].startLoc,
                                rawDrives[i].endLoc,
                                rawDrives[i].startTime,
                                rawDrives[i].endTime
                            )

                            dbDrives.child(newDrive.id!!).setValue(newDrive)

                            if(i == rawDrives.size - 2) {
                                dbDrives.child(rawDrives[i+1].id!!).setValue(rawDrives[i+1])
                            }
                        }
                    }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                println("Cancelled")
            }
        }

        dbRawDrives.addValueEventListener(driveListener)
        //Clear dbRawDrives here

        //Google maps API call here

        val authUser = Firebase.auth.currentUser
        var email = ""
        authUser?.let{
            email = it.email!!
        }
        val id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")
        userEmail = id

        dbUsers = FirebaseDatabase.getInstance().getReference("Users")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(ds in dataSnapshot.children) {
                    if(ds.child("email").value.toString().lowercase(Locale.getDefault()) == email) {
                        val emailId = ds.child("email").value.toString()
                        if(ds.child("cartype").value.toString() == "Sedan") {
                            println("MAKING API CALL")
                            val activity_id = "passenger_vehicle-vehicle_type_car-fuel_source_na-engine_size_na-vehicle_age_na-vehicle_weight_na"
                            carApiRequest("https://beta4.api.climatiq.io/estimate", activity_id, ds.child("Emissions").value.toString().toDouble(), emailId)
                        }

                        else if(ds.child("cartype").value.toString() == "Motorcycle") {
                            println("MAKING API CALL")
                            val activity_id = "passenger_vehicle-vehicle_type_motorcycle-fuel_source_na-engine_size_na-vehicle_age_na-vehicle_weight_na"
                            carApiRequest("https://beta4.api.climatiq.io/estimate", activity_id, ds.child("Emissions").value.toString().toDouble(), emailId)
                        }

                        else if(ds.child("cartype").value.toString() == "Pickup Truck/Minivan") {
                            println("MAKING API CALL")
                            val activity_id = "commercial_vehicle-vehicle_type_truck_light-fuel_source_na-engine_size_na-vehicle_age_na-vehicle_weight_na"
                            carApiRequest("https://beta4.api.climatiq.io/estimate", activity_id, ds.child("Emissions").value.toString().toDouble(), emailId)
                        }

                        else if(ds.child("cartype").value.toString() == "Truck") {
                            println("MAKING API CALL")
                            val activity_id = "passenger_vehicle-vehicle_type_motorcycle-fuel_source_na-engine_size_na-vehicle_age_na-vehicle_weight_na"
                            carApiRequest("https://beta4.api.climatiq.io/estimate", activity_id, ds.child("Emissions").value.toString().toDouble(), emailId)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Cancelled")
            }

        }

        dbUsers.addValueEventListener(userListener)


        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        val intent2 = Intent(this, ActivityTransitionReceiver::class.java)
        intent.action = "MYLISTENINGACTION"
        intent2.action = "MYLISTENINGACTION"
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

        val result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(
            result, intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )
        val events2: MutableList<ActivityTransitionEvent> = ArrayList()
        transitionEvent = ActivityTransitionEvent(
            DetectedActivity.IN_VEHICLE,
            ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos()
        )
        events2.add(transitionEvent)

        val result2 = ActivityTransitionResult(events2)
        SafeParcelableSerializer.serializeToIntentExtra(
            result2, intent2,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )


        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            sendBroadcast(intent)
            delay(5000)
            sendBroadcast(intent2)
        }

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
//        userDataStoreDemo()
    }

//    private fun userDataStoreDemo() {
//        lifecycleScope.launch {
//            userDataStore.setAuthToken("my tiken is 0")
//
//            userDataStore.getAuthToken()?.let { Log.e("MyToken: ", it) }
//        }
//
//    }

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

    private fun carApiRequest(sUrl: String, activityId: String, currentEmissions: Double, emailId: String): String? {
        val distance = 50
        var okHttpClient: OkHttpClient = OkHttpClient()
        var result: String? = null
        val json = "{\"emission_factor\":{\"activity_id\":\"$activityId\", \"data_version\":\"4.4\", \"region\":\"US\"}, \"parameters\":{\"distance\":$distance, \"distance_unit\":\"mi\"}}"
        try {
            val body: RequestBody = json.toRequestBody("/application/json".toMediaTypeOrNull())

            val url = URL(sUrl)

            val request = Request.Builder().post(body).url(url).addHeader("Authorization", "Bearer: ${Constants.KEY}").build()

            okHttpClient.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    result = response.body?.string()
                    println(result)
                    println(result?.subSequence(8, result!!.indexOf(",")))
                    val em = result?.subSequence(8, result!!.indexOf(",")).toString().toDouble()
                    val totalEm = currentEmissions + em
                    dbUsers.child(emailId).child("Emissions").setValue(totalEm)
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

            })
            return result
        }
        catch(err:Error) {
            print("Error when executing get request: "+err.localizedMessage)
        }
        return result
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