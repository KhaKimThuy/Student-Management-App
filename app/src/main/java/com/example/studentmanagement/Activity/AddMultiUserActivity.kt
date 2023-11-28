package com.example.studentmanagement.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.AddMultiUserAdapter
import com.example.studentmanagement.Adapter.UserListAdapter
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityAddMultiUserBinding
import com.example.studentmanagement.databinding.ActivityAddNewUserBinding
import com.example.studentmanagement.databinding.ActivityHomePageBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AddMultiUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddMultiUserBinding

    private lateinit var  userList : ArrayList<User>
    private lateinit var adapter: AddMultiUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMultiUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )


        userList = ArrayList<User>()
        userList = intent.getParcelableArrayListExtra("userList")!!


        loadListOfUser()

        binding.buttonAdd.setOnClickListener(View.OnClickListener {
            for (user in userList) {
                user.position = "Student"
                user.avatarUrl = ""
                user.password = user.phone
                UserDAL().CreateNewUser(user)
            }
            Toast.makeText(this, "Added " + userList.size + " students", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserManagementActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun loadListOfUser() {
        adapter = AddMultiUserAdapter(userList, this)
        binding.recyclerView.adapter = adapter
    }

}