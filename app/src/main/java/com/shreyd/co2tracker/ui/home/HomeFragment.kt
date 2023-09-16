package com.shreyd.co2tracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.shreyd.co2tracker.Drive
import com.shreyd.co2tracker.DriveAdapter
import com.shreyd.co2tracker.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val drives = mutableListOf<Drive>()


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
        drives.add(Drive("fdsa", listOf(1.2, 23.4), listOf(2.3, 34.5), 1234561, 4321562, emission=16.0))
        drives.add(Drive("fdksa", listOf(1.2, 23.4), listOf(2.3, 34.5), 1234561, 4321562, emission=16.0))

        val adapter = DriveAdapter(drives)
        binding.recycler.adapter = adapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}