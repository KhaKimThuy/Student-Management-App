package com.example.studentmanagement.Activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.UserListAdapter
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityUserManagementBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserManagementBinding
    private val READ_EXTERNAL_STORAGE_REQUEST = 110
    private val PICK_CSV_FILE_REQUEST = 111


    lateinit var adapter: UserListAdapter
    lateinit var userList: ArrayList<User>
    val UPDATE_USER_CODE : Int = 101
    var updateUser : Boolean = false


//    // Sort dialog
//    var cbCriteria = ArrayList<CheckBox>()
//    var swOrder = ArrayList<Switch>()
//
//    val v = R.layout.sort_dialog
//
//    val cbAge = findViewById<CheckBox>(R.id.cbAge)
//    val cbName = findViewById<CheckBox>(R.id.cbName)
//    val cbTime = findViewById<CheckBox>(R.id.cbTime)
//
//    val swAge = findViewById<Switch>(R.id.swAge)
//    val swName = findViewById<Switch>(R.id.swName)
//    val swTime = findViewById<Switch>(R.id.swTime)

    val sortCriteria = ArrayList<String>()
    val sortOrder = ArrayList<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (UserDTO.currentUser?.position) {
            "Admin" -> {
                supportActionBar?.title = "User Management";
            }
            "Manager" -> {
                supportActionBar?.title = "Student Management";
            }
            "Student" -> {
                supportActionBar?.title = "List of people";
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        userList = ArrayList<User>()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        UserDAL().GetListOfUser(this) // -> loadListOfUser

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!UserDTO.currentUser?.position.equals("Student")) {
            menuInflater.inflate(R.menu.manage_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export -> {
                val intent = Intent(this, AddMultiUserActivity::class.java)
                intent.putExtra("target", "export")
                intent.putParcelableArrayListExtra("userList", userList)
                startActivity(intent)
                true
            }

            R.id.sort -> {
                showSortOptionDialog()
                true
            }

            R.id.addUser -> {
                showBottomDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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

    private fun showSortOptionDialog() {

        val viewDialog = this.layoutInflater.inflate(R.layout.sort_dialog, null)
        // Sort dialog
        var cbCriteria = ArrayList<CheckBox>()
        var swOrder = ArrayList<Switch>()


        val cbAge = viewDialog.findViewById<CheckBox>(R.id.cbAge)
        val cbName = viewDialog.findViewById<CheckBox>(R.id.cbName)
        val cbTime = viewDialog.findViewById<CheckBox>(R.id.cbTime)

        val swAge = viewDialog.findViewById<Switch>(R.id.swAge)
        val swName = viewDialog.findViewById<Switch>(R.id.swName)
        val swTime = viewDialog.findViewById<Switch>(R.id.swTime)

        // Sort dialog
        cbCriteria.add(cbAge)
        cbCriteria.add(cbName)
        cbCriteria.add(cbTime)

        swOrder.add(swAge)
        swOrder.add(swName)
        swOrder.add(swTime)

        AlertDialog.Builder(this).setView(viewDialog)
            .setTitle("Set sort criteria")
            .setPositiveButton("OK") {
                    dialog,_->

                // Get criteria and order
                sortCriteria.clear()
                sortOrder.clear()

                for (idx in 0..<cbCriteria.size) {
                    if (cbCriteria[idx].isChecked) {
                        sortCriteria.add(cbCriteria[idx].text.toString())
                        sortOrder.add(swOrder[idx].isChecked)
                    }
                }

                // Process sort
                val sortedList = UserDAL().SortUserList(sortCriteria, sortOrder, userList)

                userList.clear()

                userList.addAll(sortedList)

                adapter.notifyDataSetChanged()

                dialog.dismiss()
            }
            .setNegativeButton("CANCEL"){
                    dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()

    }

    fun loadListOfUser() {
        adapter = UserListAdapter(userList, this)
        binding.recyclerView.adapter = adapter
    }


    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val importFile: LinearLayout = dialog.findViewById(R.id.importFile)
        val addUser: LinearLayout = dialog.findViewById(R.id.addUser)

        importFile.setOnClickListener(View.OnClickListener {
            importCSVFile()
            dialog.dismiss()
        })

        addUser.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddNewUserActivity::class.java)
            startActivity(intent)   
            dialog.dismiss()
        })

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.setGravity(Gravity.BOTTOM)
    }



    private fun loadCSVFile(uri: Uri) {
        try {
            var userList = ArrayList<User>()
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line : String?
            while (reader.readLine().also { line = it } != null) {
                val row : List<String> = line!!.split(",")

                var user = User()

                try {
                    user.name = row[0]
                    user.age = row[1]
                    user.phone = row[2]
                    user.status = row[3]
                    userList.add(user)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            inputStream?.close() // Close the inputStream when done
            val intent = Intent(this, AddMultiUserActivity::class.java)
            intent.putExtra("target", "import")
            intent.putParcelableArrayListExtra("userList", userList)
            startActivity(intent)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Import file error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importCSVFile() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST)
        } else {
            openFilePicker()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker()
                } else {
                }
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, PICK_CSV_FILE_REQUEST)
    }

    private var fileuri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CSV_FILE_REQUEST && data != null) {
            fileuri = data.data
            fileuri?.let { loadCSVFile(it) }
        }

        else if (requestCode == UPDATE_USER_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val userPosition = data.getIntExtra("position", -1)
                if (userPosition > 0) {
                    userList[userPosition] = data.getParcelableExtra("user")!!
                    adapter.notifyItemChanged(userPosition)
                }
            }
        }
    }



    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelableArrayList("userList", userList)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userList.clear()
        val ul = savedInstanceState?.getParcelableArray("yourParcelableArray")
        if (ul != null) {
            userList.addAll(ul.filterIsInstance<User>())
        }
        adapter.notifyDataSetChanged()
    }
}