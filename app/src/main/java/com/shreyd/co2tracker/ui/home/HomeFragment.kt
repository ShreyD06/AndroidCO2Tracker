package com.shreyd.co2tracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.shreyd.co2tracker.Drive
import com.shreyd.co2tracker.Drive2
import com.shreyd.co2tracker.DriveAdapter
import com.shreyd.co2tracker.R
import com.shreyd.co2tracker.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val drives = mutableListOf<Drive2>()
    lateinit var adapter: DriveAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Data From Firebase
        adapter = DriveAdapter(drives)
        binding.recycler.adapter = adapter
        val authUser = Firebase.auth.currentUser
        var email = ""
        authUser?.let{
            email = it.email!!
        }
        val id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")

        val dbUserDrives = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Drives")
        var change = 0
        val driveListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("error", "Success")
                change ++
                if(change == 1) {
                    for(ds in snapshot.children) {
                        val drive = ds.getValue(Drive2::class.java)
                        drives.add(drive!!)
                        println(drive.startTime)
                        println("-----------SIZE ${drives.size}--------------")

                        adapter.notifyDataSetChanged()
                    }
               }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("error", error.details)
                println(error)
            }

        }

        dbUserDrives.addValueEventListener(driveListener)



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}