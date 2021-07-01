package com.trendster.harpic.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.trendster.harpic.R
import com.trendster.harpic.ui.user.UserActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            },
            1500
        )
        supportActionBar?.hide()
    }
}