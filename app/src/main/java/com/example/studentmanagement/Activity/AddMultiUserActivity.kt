package com.example.studentmanagement.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.AddMultiUserAdapter
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityAddMultiUserBinding
import java.io.File
import java.io.IOException

class AddMultiUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddMultiUserBinding

    private lateinit var  userList : ArrayList<User>
    private lateinit var adapter: AddMultiUserAdapter
    private lateinit var target : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMultiUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Check file content";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        userList = ArrayList<User>()

        target = intent.getStringExtra("target").toString()
        if (target == "import") {
            binding.buttonAdd.text = "Import"
        } else {
            binding.buttonAdd.text = "Export"
        }

        userList = intent.getParcelableArrayListExtra("userList")!!


        loadListOfUser()
        binding.buttonAdd.setOnClickListener(View.OnClickListener {
            if (binding.buttonAdd.text == "Import") {
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

            } else {
                // Export list of students in csv form
                askFileName()
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun askFileName() {
        val v = this.layoutInflater.inflate(R.layout.ask_filename_dialog, null)
        val fileName = v.findViewById<EditText>(R.id.edt_fileName)
        AlertDialog.Builder(this).setView(v)
            .setPositiveButton("OK"){
                    dialog,_->
                exportFile(fileName.text.toString())
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL"){
                    dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun <T> csvOf(
        headers: List<String>,
        data: List<T>,
        itemBuilder: (T) -> List<String>
    ) = buildString {
        append(headers.joinToString(",") { "$it" })
        append("\n")
        data.forEach { item ->
            append(itemBuilder(item).joinToString(",") { "$it" })
            append("\n")
        }
    }

    private fun exportFile(fileName:String) {
        if (fileName.isEmpty()) {
            Toast.makeText(this, "File name must not be empty", Toast.LENGTH_SHORT).show()
        }else{
            val csvContent = csvOf(
                listOf("name", "age", "phone", "status"),
                userList
            ) {
                listOf(it.name, it.age, it.phone, it.status)
            }

            val fileName = "$fileName.csv"
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName)

            try {
                file.writeText(csvContent)
                Toast.makeText(this, "CSV file exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error exporting CSV file", Toast.LENGTH_SHORT).show()
            }

            finish()

        }
    }

    private fun loadListOfUser() {
        adapter = AddMultiUserAdapter(userList, this)
        binding.recyclerView.adapter = adapter
    }

}