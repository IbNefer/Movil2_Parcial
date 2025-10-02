package com.example.android.gdgfinder.search

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.android.gdgfinder.R
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.gdgfinder.databinding.FragmentGdgListBinding
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

private const val LOCATION_PERMISSION_REQUEST = 1

private const val LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

class GdgListFragment : Fragment() {


    private val viewModel: GdgListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentGdgListBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val adapter = GdgListAdapter(GdgClickListener { chapter ->
            val destination = Uri.parse(chapter.website)
            startActivity(Intent(Intent.ACTION_VIEW, destination))
        })

        binding.gdgChapterList.adapter = adapter

        viewModel.showNeedLocation.observe(viewLifecycleOwner) { value ->
            if (value) {
                Snackbar.make(
                    binding.root,
                    "No location. Enable location in settings and check app permissions!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        viewModel.regionList.observe(viewLifecycleOwner) { regions ->
            regions ?: return@observe
            val chipGroup = binding.regionsList
            val inflater = LayoutInflater.from(chipGroup.context)

            chipGroup.removeAllViews()
            regions.forEach { regionName ->
                val chip = inflater.inflate(R.layout.region, chipGroup, false) as Chip
                chip.text = regionName
                chip.tag = regionName
                chip.setOnCheckedChangeListener { button, isChecked ->
                    viewModel.onFilterChanged(button.tag as String, isChecked)
                }
                chipGroup.addView(chip)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLastLocationOrStartLocationUpdates()
    }


    private fun requestLocationPermission() {
        requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
    }


    private fun requestLastLocationOrStartLocationUpdates() {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                startLocationUpdates(fusedLocationClient)
            } else {
                viewModel.onLocationUpdated(location)
            }
        }
    }


    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }


        val request = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000L).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                viewModel.onLocationUpdated(location)
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, null)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLastLocationOrStartLocationUpdates()
                }
            }
        }
    }
}


