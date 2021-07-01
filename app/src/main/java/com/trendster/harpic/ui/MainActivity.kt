package com.trendster.harpic.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.trendster.harpic.R
import com.trendster.harpic.databinding.ActivityMainBinding
import com.trendster.harpic.ui.about.AboutActivity
import com.trendster.harpic.ui.quotes.QuotesActivity
import com.trendster.harpic.ui.user.UserActivity
import com.trendster.harpic.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        navController = findNavController(R.id.mainNavHost)
        binding.contentMain.mainBottomNav.setupWithNavController(navController)
        supportActionBar?.hide()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_quotes -> {
                    closeDrawer()
                    startActivity(Intent(this, QuotesActivity::class.java))
                    true
                }
                R.id.item_about -> {
                    closeDrawer()
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.item_help -> {
                    Toast.makeText(this, "Email - knishant362@gmail.com", Toast.LENGTH_LONG).show()
                    closeDrawer()
                    true
                }
                R.id.item_logout -> {
                    auth.signOut()
                    closeDrawer()
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }
    }

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}
