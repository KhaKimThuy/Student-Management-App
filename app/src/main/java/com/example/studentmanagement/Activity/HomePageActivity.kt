package com.example.studentmanagement.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.Fragment.HomeFragment
import com.example.studentmanagement.Fragment.ProfileFragment
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityAddNewUserBinding
import com.example.studentmanagement.databinding.ActivityHomePageBinding
import com.example.studentmanagement.databinding.ActivityLoginBinding
import com.example.studentmanagement.databinding.FragmentProfileBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fragmentManager = supportFragmentManager
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, HomeFragment())
        fragmentTransaction.commitNow()


        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true
        }

        if (UserDTO.currentUser?.position ?: "" == "Admin") {
            binding.imgAddNew.visibility = View.VISIBLE
            binding.imgAddNew.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, AddNewUserActivity::class.java)
                startActivity(intent)
            })
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (fragment != null) {
            fragmentTransaction.replace(R.id.frameLayout, fragment)
        }
        fragmentTransaction.commit()
    }
}