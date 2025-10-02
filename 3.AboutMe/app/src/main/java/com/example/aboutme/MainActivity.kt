package com.example.aboutme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.example.aboutme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val miNombre: MiNombre = MiNombre("Alessandro Alcime")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.myName = miNombre
        binding.nicknameButton.setOnClickListener { setNickName(it) }

    }

    private fun setNickName(view: View) {
        if (!binding.nicknameEdit.text.isNullOrEmpty()){
            binding.apply {

                myName?.nickName = nicknameEdit.text.toString()
                invalidateAll()
                nicknameEdit.visibility = View.GONE
                nicknameTextview.visibility = View.VISIBLE
                nicknameButton.visibility = View.GONE
            }
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }
}
