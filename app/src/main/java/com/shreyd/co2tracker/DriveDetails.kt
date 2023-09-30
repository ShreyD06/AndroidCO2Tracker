package com.shreyd.co2tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class DriveDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_details)

        val driveId = intent.getStringExtra("driveId")

        val authUser = Firebase.auth.currentUser
        var email = ""
        authUser?.let{
            email = it.email!!
        }
        val id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")

        val dbUserDrives = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Drives").child(driveId!!)

        val singleDriveListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        dbUserDrives.addListenerForSingleValueEvent(singleDriveListener)
    }
}