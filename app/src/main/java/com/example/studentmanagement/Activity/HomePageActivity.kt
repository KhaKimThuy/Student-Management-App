package com.example.studentmanagement.Activity

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.databinding.ActivityHomePageBinding
import com.squareup.picasso.Picasso


class HomePageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Dashboard";

        binding.imgMainAvatar?.setImageBitmap(UserDTO.userAvatar)
        binding.tvUsername2?.text = UserDTO.currentUser.name
        binding.tvPosition2?.text = UserDTO.currentUser.position


        if (UserDTO.currentUser!!.position != "Student") {
            binding.cardViewUserManagement?.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, UserManagementActivity::class.java)
                startActivity(intent)
            })
        } else {
            binding.cardViewUserManagement?.visibility = View.GONE
        }

        binding.cardViewProfile?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("position", -1)
            intent.putExtra("user", UserDTO.currentUser)
            startActivityForResult(intent, 99)
        })

        binding.cardViewLogout?.setOnClickListener(View.OnClickListener {

            val sharedPreferences = this.getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99) {
            val userrr : User = data?.getParcelableExtra("user")!!
            UserDTO.currentUser = userrr
            binding.imgMainAvatar?.setImageBitmap(UserDTO.userAvatar)
            binding.tvUsername2?.text = userrr.name
            binding.tvPosition2?.text = userrr.position
        }
    }

}