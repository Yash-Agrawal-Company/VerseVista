package com.yashagrawal.versevista

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.yashagrawal.versevista.authentication.LoginActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private var DELAY_TIME : Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splash = findViewById<ImageView>(R.id.splash)
        splash.scaleType = ImageView.ScaleType.FIT_XY;
        auth = FirebaseAuth.getInstance()


        Handler(Looper.getMainLooper()).postDelayed({

            if (auth.currentUser != null) {
                DELAY_TIME = 0
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, DELAY_TIME)
    }
}