
package com.example.android.marsrealestate.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsrealestate.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        // Get the MarsProperty from the safe args bundle
        val marsProperty = DetailFragmentArgs.fromBundle(requireArguments()).selectedProperty

        // ViewModel Factory
        val viewModelFactory = DetailViewModelFactory(marsProperty, requireActivity().application)

        // Modern ViewModel initialization with factory
        val viewModel: DetailViewModel by viewModels { viewModelFactory }

        binding.viewModel = viewModel

        return binding.root
    }
}
