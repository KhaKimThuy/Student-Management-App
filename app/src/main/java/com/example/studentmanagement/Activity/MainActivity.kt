package com.example.studentmanagement.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityLoginBinding
import com.example.studentmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Animations
        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        binding.gifImageView.animation = topAnim;
        binding.tvWelcome.animation = bottomAnim;

        Handler().postDelayed({

            val sharedpreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, 0)
            var hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false)
            val phone = sharedpreferences.getString("phone_key", null)
            val password = sharedpreferences.getString("password_key", null)

            if (hasLoggedIn && phone != null && password != null) {
                UserDAL().LoadUser(phone, password, this)
            } else {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}