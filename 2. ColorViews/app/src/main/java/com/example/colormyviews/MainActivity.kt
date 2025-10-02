package com.example.colormyviews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.colormyviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        val clickableViews: List<View> = listOf(
            binding.boxOneText,
            binding.boxTwoText,
            binding.boxThreeText,
            binding.boxFourText,
            binding.boxFiveText,
            binding.constraintLayout,
            binding.redButton,
            binding.yellowButton,
            binding.greenButton
        )

        for (item in clickableViews) {
            item.setOnClickListener { makeColored(it) }
        }
    }

    private fun makeColored(view: View) {
        when (view.id) {
            binding.boxOneText.id -> view.setBackgroundColor(Color.DKGRAY)
            binding.boxTwoText.id -> view.setBackgroundColor(Color.GRAY)

            binding.boxThreeText.id -> view.setBackgroundResource(android.R.color.holo_green_light)
            binding.boxFourText.id -> view.setBackgroundResource(android.R.color.holo_green_dark)
            binding.boxFiveText.id -> view.setBackgroundResource(android.R.color.holo_green_light)

            binding.redButton.id -> binding.boxThreeText.setBackgroundResource(R.color.myRed)
            binding.yellowButton.id -> binding.boxFourText.setBackgroundResource(R.color.myYellow)
            binding.greenButton.id -> binding.boxFiveText.setBackgroundResource(R.color.myGreen)

            else -> view.setBackgroundColor(Color.LTGRAY)
        }
    }
}
