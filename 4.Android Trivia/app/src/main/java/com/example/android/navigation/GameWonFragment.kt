
package com.example.android.navigation

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameWonBinding


class GameWonFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameWonBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_won, container, false)
         binding.nextMatchButton.setOnClickListener { view:View ->
             view.findNavController().navigate(GameWonFragmentDirections.actionGameWonFragmentToGameFragment2())
         }
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context,
                "Questions Asked: ${args.numQuestions} Correct Answers: ${args.numCorrect}",Toast.LENGTH_LONG).show()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.winner_menu,menu)
        if(null == getShareIntent().resolveActivity(requireActivity().packageManager)){
           menu!!.findItem(R.id.share).setVisible(false)
        }
    }

    private fun getShareIntent() : Intent{
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        return activity?.let { ShareCompat.IntentBuilder.from(it) }
            ?.setText(getString(R.string.share_success_text,args.numCorrect,args.numQuestions))
            ?.setType("text/plain")
            ?.intent ?: throw IllegalStateException("Activity cannot be null")

    }

    private fun shareSuccess(){
        startActivity(getShareIntent())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.share -> shareSuccess()
        }
        return super.onOptionsItemSelected(item)
    }
}
