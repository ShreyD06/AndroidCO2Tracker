package com.shreyd.co2tracker.ui.home

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseUser
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
import kotlin.properties.Delegates



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var savedEms: TextView? = null
    private var totalEms: TextView? = null
    private lateinit var authUser: FirebaseUser
    private lateinit var id: String
    private var numEnter = 0

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

        numEnter++

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        savedEms = _binding!!.co2emits
        totalEms = _binding!!.totalEmission

        authUser = Firebase.auth.currentUser!!
        var email = ""
        authUser.let{
            email = it.email!!
        }
        id = email.replace(".", "").replace("#", "")
            .replace("$", "").replace("[", "").replace("]", "")

        val dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(id)

        dbUsers.child("Emissions").get().addOnSuccessListener {
            totalEms!!.text = it.value.toString()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        dbUsers.child("Saved Emissions").get().addOnSuccessListener {
            savedEms!!.text = it.value.toString()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get Data From Firebase
        adapter = DriveAdapter(drives)
        binding.recycler.adapter = adapter


        if(numEnter == 1) {
            val lineDrives = mutableListOf<Drive2>()
            val dbUserDrives = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Drives")
            var change = 0
            val driveListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("error", "Success")
                    change ++
                    if(change == 1) {
                        val threshold = snapshot.childrenCount - 5
                        var countD = 0L
                        for(ds in snapshot.children) {
                            countD++
                            val tDrive = ds.getValue(Drive2::class.java)
                            print(ds.key)
                            tDrive!!.id = ds.key
                            lineDrives.add(tDrive)

                            if (countD > threshold) {
                                tDrive.id = ds.key
                                drives.add(tDrive)
                                println(tDrive.startTime)
                                println("-----------SIZE ${drives.size}--------------")
                                println(countD)

                                adapter.notifyItemInserted(drives.size-1)
                            }

                            if (countD == snapshot.childrenCount) {
                                val weekDrives = isInWeek(lineDrives)
                                println("WDRIVES $weekDrives")
                                val linedataset = LineDataSet(weekDrives, "first")

                                linedataset.color = resources.getColor(R.color.purple_200)

                                linedataset.circleRadius = 10f
                                linedataset.setDrawFilled(true)
                                linedataset.valueTextSize = 20F

                                linedataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                                binding.apply {
                                    getTheGraph.data = LineData(LineDataSet(weekDrives, "first"))
                                    getTheGraph.animateXY(2000, 2000)
                                }

                            }
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
    }

    fun isInWeek(drives: List<Drive2>): ArrayList<Entry> {
        print("DRIVES: ${drives}")
        val currentTime = System.currentTimeMillis()
        val weekMilli = 30*24*60*60*1000L
        val weekSinceDrives = ArrayList<Entry>()

        var ct = 0
        for (drive in drives.slice(0..drives.size-2)) {
            if (currentTime - drive.endTime!! < weekMilli) {
                print(drive.id)
                weekSinceDrives.add(Entry(ct.toFloat(), drive.emission.toFloat()))
                ct++
            }
        }


        return weekSinceDrives
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}