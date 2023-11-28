package com.example.studentmanagement.Activity

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.UserListAdapter
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityUserManagementBinding
import java.util.Locale

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserManagementBinding
    lateinit var adapter: UserListAdapter
    lateinit var userList: ArrayList<User>
    val UPDATE_USER_CODE : Int = 101
    var updateUser : Boolean = false


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

        binding.search?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchList(query)
                //Toast.makeText(context, "Query: $query", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


    }


    fun searchList(text: String) {
        val searchList = java.util.ArrayList<User>()
        for (dataClass in userList) {
            val userInfo = dataClass.name.lowercase() +
                    dataClass.position.lowercase() +
                    dataClass.age +
                    dataClass.status.lowercase()
            Log.d("TAG" , "Search query : " + userInfo)

            if (userInfo?.contains(text.lowercase(Locale.getDefault())) == true)
            {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }


    fun loadListOfUser() {
        adapter = UserListAdapter(userList, this)
        binding.recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_USER_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val userPosition = data.getIntExtra("position", -1)
                if (userPosition > 0) {
                    userList[userPosition] = data.getParcelableExtra("user")!!
                    adapter.notifyItemChanged(userPosition)
                }
            }
        }
    }
}