package com.trendster.harpic.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trendster.harpic.R
import com.trendster.harpic.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private var _binding : ActivityAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.closeButton.setOnClickListener {
            finish()
        }



    }
}