package com.example.studentmanagement.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.databinding.ActivityAddNewUserBinding

class AddNewUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddNewUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Add new user";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        binding.btnAddNew.setOnClickListener(View.OnClickListener {

            val selectedRadioButtonId = binding.radioPos.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val position: String
                if (binding.rbtnManager.isChecked) {
                    position = "Manager"
                } else {
                    position = "Student"
                }

                val status: String
                if (binding.rbtnNormal.isChecked) {
                    status = "Normal"
                } else {
                    status = "Locked"
                }

                createNewUser(binding.edtAddPhone.text.toString(), binding.edtAddName.text.toString(),
                    binding.edtAddAge.text.toString(), position, status)


            } else {
                Toast.makeText(this, "Fill all information in form", Toast.LENGTH_SHORT).show()
            }

        })

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun createNewUser(phone: String, name : String, age : String, position : String, status : String) {

        val user = User()
        user.phone = phone
        user.password = phone
        user.name = name
        user.age = age
        user.position = position
        user.status = status
        user.avatarUrl = ""

        UserDAL().CreateNewUser(user, this)
    }
}