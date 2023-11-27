package com.example.studentmanagement.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityAddNewUserBinding
import com.example.studentmanagement.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding
    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra("user")!!
        loadUserProfile()

        binding.btnUpdate.setOnClickListener(View.OnClickListener {
//            UserDAL().G
        })


    }

    private fun loadUserProfile() {
        if (user.avatarUrl != "") {
            Picasso.get().load(user.avatarUrl).into(binding.imgAvatar)
        }

        binding.tvUsername2.text = user.name
        binding.tvPosition2.text = user.position
        binding.tvName.setText(user.name)
        binding.tvAge.setText(user.age)
        binding.tvPhone.setText(user.phone)
        binding.tvStatus.setText(user.status)

    }

}