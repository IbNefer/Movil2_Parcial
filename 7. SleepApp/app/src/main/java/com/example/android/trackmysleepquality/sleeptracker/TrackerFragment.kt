
package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

class TrackerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = TrackerViewModelFactory(dataSource, application)
        val sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory)[SleepTrackerViewModel::class.java]

        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = SleepNightAdapter(SleepNightListener { night ->
            sleepTrackerViewModel.onSleepNightClicked(night)
        })

        binding.sleepRecyclerView.adapter = adapter
        binding.sleepRecyclerView.layoutManager = GridLayoutManager(context, 3)

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner) { nights ->
            adapter.submitList(nights)
        }

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner) { night ->
            night?.let {
                findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                sleepTrackerViewModel.doneNavigating()
            }
        }

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner) { show ->
            if (show == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        }

        sleepTrackerViewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner) { nightId ->
            nightId?.let {
                findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(nightId)
                )
                sleepTrackerViewModel.onSleepDataQualityNavigated()
            }
        }

        return binding.root
    }
}
