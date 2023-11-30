package com.example.studentmanagement.Activity

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.studentmanagement.Fragment.HomeFragment
import com.example.studentmanagement.databinding.ActivityHomePageBinding


class HomePageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Dashboard";


        fragmentManager = supportFragmentManager
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.studentmanagement.R.id.frameLayout, HomeFragment())
        fragmentTransaction.commitNow()


    }

}