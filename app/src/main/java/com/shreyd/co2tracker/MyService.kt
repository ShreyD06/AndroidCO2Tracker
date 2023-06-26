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
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import androidx.core.app.ActivityCompat


class MyService: Service() {
    private lateinit var client: FusedLocationProviderClient
    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            val longitude: Double? = location?.longitude
            println("$latitude, $longitude")
        }

        stopSelf()
        return super.onStartCommand(intent, flags, startId)
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
            .setPriority(Notification.PRIORITY_MIN)
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
    }
}