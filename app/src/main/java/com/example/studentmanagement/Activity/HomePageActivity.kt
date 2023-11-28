package com.example.studentmanagement.Activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.Fragment.HomeFragment
import com.example.studentmanagement.Fragment.ProfileFragment
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityAddNewUserBinding
import com.example.studentmanagement.databinding.ActivityHomePageBinding
import com.example.studentmanagement.databinding.ActivityLoginBinding
import com.example.studentmanagement.databinding.FragmentProfileBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private val READ_EXTERNAL_STORAGE_REQUEST = 110
    private val PICK_CSV_FILE_REQUEST = 111

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
//                val intent = Intent(this, AddNewUserActivity::class.java)
//                startActivity(intent)

                showBottomDialog()

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
            importCSVFile()
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
    }

}