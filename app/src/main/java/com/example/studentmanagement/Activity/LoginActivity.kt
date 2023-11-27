package com.example.studentmanagement.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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