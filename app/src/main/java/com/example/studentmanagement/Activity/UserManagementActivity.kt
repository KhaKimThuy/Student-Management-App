package com.example.studentmanagement.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.UserListAdapter
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityUserManagementBinding


class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserManagementBinding
    lateinit var adapter: UserListAdapter
    lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = ArrayList<User>()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        UserDAL().GetListOfUser("", this) // -> loadListOfUser

        binding.radioPos.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbtnManager -> {
                    UserDAL().GetListOfUser("Manager", this) // -> loadListOfUser
                }
                R.id.rbtnStudent -> {
                    UserDAL().GetListOfUser("Student", this) // -> loadListOfUser
                }
            }
        }

    }

    fun loadListOfUser() {
        adapter = UserListAdapter(userList, this)
        binding.recyclerView.adapter = adapter
    }

}