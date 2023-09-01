package com.shreyd.co2tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File


class MyService: Service() {
    private lateinit var client: FusedLocationProviderClient
    val path = "/json/locations.json"
    private val Context.dataStore by preferencesDataStore(
        name = "Locations"
    )
    private lateinit var scope: CoroutineScope
    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
        val job = SupervisorJob()
        scope = CoroutineScope(Dispatchers.IO + job)

    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ENTER -> enter()
            EXIT -> exit()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun writeStore(key: String, value: Double) {
        val dataStoreKey = doublePreferencesKey(key)
        dataStore.edit {settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun readStore(key: String): Double? {
        val dataStoreKey = doublePreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun enter() {
        startForeground(NOTIFICATION_ID, createNotification())


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            println("do nothing")
        }
        client.lastLocation.addOnSuccessListener { location: Location? ->
            val latitude: Double? = location?.latitude
            var latitude_new = 0.0
            if(latitude!=null) {
                latitude_new = latitude
            }
            val longitude: Double? = location?.longitude
            var longitude_new = 0.0
            if(longitude!=null) {
                longitude_new = longitude
            }
            scope.launch {
                writeStore("LatEnter", latitude_new)
                writeStore("LongEnter", longitude_new)
            }
            println("$latitude, $longitude")
        }

        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exit() {
        startForeground(NOTIFICATION_ID, createNotification())


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //Log.e("startTime", startTime)
            println("do nothing")
        }
        client.lastLocation.addOnSuccessListener { location: Location? ->
            val latitude: Double? = location?.latitude
            val longitude: Double? = location?.longitude
            var latitudeExN = 0.0
            if(latitude!=null) {
                latitudeExN = latitude
            }
            var longitudeExN = 0.0
            if(longitude!=null) {
                longitudeExN = longitude
            }
            scope.launch {
                writeStore("LatExit", latitudeExN)
                writeStore("LongExit", longitudeExN)
               // TODO("Make API Call")
            }
            println("EXIT FUNCTION")
            println("$latitude, $longitude")
        }

        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("location_service", "Location Service")
            } else {
                ""
            }

        val notificationBuilder = Notification.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)

        return notificationBuilder.build()
    }

    @Suppress("SameParameterValue")
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            channel
        } else {
            null
        }
        return channelId
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ENTER = "ENTER"
        const val EXIT = "EXIT"
    }


}