
package com.example.android.guesstheword.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.ScoreFragmentBinding


class ScoreFragment : Fragment() {

    private lateinit var viewModelFactory: ScoreViewModelFactory
    private lateinit var viewModel: ScoreViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.score_fragment,
                container,
                false
        )

        val scoreFragmentArgs by navArgs<ScoreFragmentArgs>()

        viewModelFactory = ScoreViewModelFactory(scoreFragmentArgs.score)
        viewModel = ViewModelProvider(this, viewModelFactory)[ScoreViewModel::class.java]

        //Data binding in score fragment
        binding.scoreViewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.eventPlayAgain.observe(this, Observer { playAgain ->
            if (playAgain){
                onPlayAgain()
                viewModel.onPlayAgainEventComplete()
            }
        })


        return binding.root
    }

    private fun onPlayAgain() {
        findNavController().navigate(ScoreFragmentDirections.actionRestart())
    }
}
