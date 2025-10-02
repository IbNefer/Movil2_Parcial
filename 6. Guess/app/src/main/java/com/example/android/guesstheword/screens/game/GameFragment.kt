
package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        Log.i("GameFragment","Called ViewModelProviders Of!")
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished
        -> if (hasFinished){
            gameFinished()
            viewModel.onGameFinishComplete()
        }})

        viewModel.buzzArray.observe(viewLifecycleOwner, Observer { buzzState ->
            if (buzzState != BuzzType.NO_BUZZ){
                buzz(buzzState.pattern)
                viewModel.onBuzzCompleted()
            }
        })

        return binding.root

    }



    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
        findNavController(this).navigate(action)
    }


    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                it.vibrate(pattern, -1)
            }
        }
    }




}
