package com.example.studentmanagement.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    companion object {
        const val SHARED_PREFS = "hasLoggedIn"
        const val PHONE_KEY = "phone_key"
        const val PASSWORD_KEY = "password_key"
    }

    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save local data
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        binding.tvCreateNewAcc.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddNewUserActivity::class.java)
            startActivity(intent)
        })
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            login(binding.edtPhone.text.toString(), binding.edtPassword.text.toString())
        })
    }

    private fun login(phone : String, pass : String) {
        UserDAL().LoginUser(phone, pass, this)
    }
}