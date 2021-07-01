package com.trendster.harpic.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.trendster.harpic.databinding.ActivityUserBinding
import com.trendster.harpic.ui.MainActivity
import com.trendster.harpic.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
