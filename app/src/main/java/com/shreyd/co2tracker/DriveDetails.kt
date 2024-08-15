package com.shreyd.co2tracker

import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shreyd.co2tracker.model.DriveInfo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class DriveDetails : AppCompatActivity(), OnMapReadyCallback {

    lateinit var driveInfo: DriveInfo
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_details)

        val driveId = intent.getStringExtra("driveId")

        println(driveId)
        val authUser = Firebase.auth.currentUser
        var email = ""
        authUser?.let{
            email = it.email!!
        }
        val id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")
//
        val drive = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Drives").child(driveId!!)
        val dbDrive = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Drives")

        val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US)

        val formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        val startTime: TextView = findViewById(R.id.startTime)
        val endTime: TextView = findViewById(R.id.endTime)
        val distance: TextView = findViewById(R.id.distance)
        val emission: TextView = findViewById(R.id.emission)

        val button: Button = findViewById(R.id.backButton)
        button.setOnClickListener {
            val tmIntent = Intent(this, TempMain::class.java)
            tmIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivityIfNeeded(tmIntent, 0)
        }


//        dbDrive.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("ID: " + driveId)
//                println("Time" + snapshot.child(driveId).child("startTime").getValue().toString())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                println("Error")
//            }
//
//        })
        drive.get().addOnSuccessListener {
            val medDrive = it.getValue(Drive2::class.java)
            println(medDrive!!.startTime)
            val instant = medDrive.startTime?.let { it1 -> Instant.ofEpochMilli(it1) }

            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            println(formatter2.format(date))
            startTime.text = formatter2.format(date)
            endTime.text = formatter.format(medDrive.endTime)
            println(medDrive.distance)
            distance.text = medDrive.distance
            emission.text = "${medDrive.emission} kg"
            println(medDrive.emission)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
//
//        val singleDriveListener = object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                driveInfo = snapshot.getValue(DriveInfo::class.java)!!
//                Log.e("lol", Gson().toJson(driveInfo))
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("lol", error.details)
//            }
//
//        }
//
//        dbUserDrives.addListenerForSingleValueEvent(singleDriveListener)
    }

    override fun onMapReady(map: GoogleMap) {
        createPolyLine(map)
    }

    private fun createPolyLine(map: GoogleMap) {
        val rectOptions = PolylineOptions()
        //this is the color of route
        //this is the color of route
        rectOptions.color(Color.argb(255, 85, 166, 27))

        var startLatLng: LatLng? = null
        var endLatLng: LatLng? = null

        rectOptions.add(LatLng(38.9124267,-77.239105))
        rectOptions.add(LatLng(38.917988,-77.230499))
        rectOptions.add(LatLng(38.908458,-77.214317))
        rectOptions.add(LatLng(38.9057249,-77.2122041))
        map.addPolyline(rectOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(38.9124267,-77.239105), 13f)
        map.animateCamera(cameraUpdate)
    }
}