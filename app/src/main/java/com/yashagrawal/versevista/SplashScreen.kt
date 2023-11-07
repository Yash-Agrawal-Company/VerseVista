package com.yashagrawal.versevista

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.yashagrawal.versevista.authentication.LoginActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splash = findViewById<ImageView>(R.id.splash)
        splash.scaleType = ImageView.ScaleType.FIT_XY;

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}