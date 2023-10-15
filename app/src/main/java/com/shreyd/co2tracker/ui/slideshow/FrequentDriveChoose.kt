package com.shreyd.co2tracker.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shreyd.co2tracker.FreqDrive
import com.shreyd.co2tracker.FreqDriveAdapter
import com.shreyd.co2tracker.databinding.FragmentFreqdrivesBinding

class FrequentDriveChoose : Fragment() {

    private var _binding: FragmentFreqdrivesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val drives = mutableListOf<FreqDrive>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFreqdrivesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TODO("This method is not being called")
        println("VIEW CREATED")
        val authUser = Firebase.auth.currentUser
        var email = ""
        authUser?.let{
            email = it.email!!
        }
        val id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")

        val dbFreqDrives = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Frequent Drives")

        val driveListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    drives.add(ds.getValue(FreqDrive::class.java)!!)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        }

        dbFreqDrives.addListenerForSingleValueEvent(driveListener)

        val adapter = FreqDriveAdapter(drives)
        binding.recyclerViewFrequentDrives.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}