package com.example.studentmanagement.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.CertificateListAdapter
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.DB.CertificateDAL
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Dialog.AddCertiDialog
import com.example.studentmanagement.Domain.Certificate
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    lateinit var user : User
    lateinit var adapter: CertificateListAdapter
    private var userPosition : Int = -1
    private var changeAvatar : Boolean = false
    lateinit var certiList : ArrayList<Certificate>
    private val READ_EXTERNAL_STORAGE_REQUEST = 110
    private val PICK_CSV_FILE_REQUEST = 111
    lateinit var importedCerti : ArrayList<Certificate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Profile";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);


        importedCerti = ArrayList<Certificate>()

        user = intent.getParcelableExtra("user")!!
        userPosition = intent.getIntExtra("position", -1)

        loadUserProfile()
        certiList = ArrayList<Certificate>()
        CertificateDAL().GetListOfCerti(user, this)

        binding.recyclerViewCertificate?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCertificate?.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerViewCertificate!!.context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.btnUpdate.setOnClickListener(View.OnClickListener {
            // Certification
            for (certi in importedCerti) {
                certi.userPK = user.pk
                CertificateDAL().CreateNewCerti(certi)
            }
            importedCerti.clear()

            // User information
            getUpdateInformation()
            if (changeAvatar) {
                uploadAvatar()
            }
            UserDAL().UpdateUserProfile(user, this)
            val returnIntent = Intent()
            returnIntent.putExtra("position", userPosition)
            returnIntent.putExtra("user", user)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        })

        binding.imageViewCamera.setOnClickListener(View.OnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        })

        binding.imageViewAddCertificate?.setOnClickListener(View.OnClickListener {
            showAddCertiDialog()
        })

        binding.importCerti?.setOnClickListener(View.OnClickListener {
            importCSVFile()
        })

        binding.exportCerti?.setOnClickListener(View.OnClickListener {
            askFileName()
        })

    }
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                deleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Throws(Resources.NotFoundException::class)
    private fun deleteDialog() {
        var userP = ""
        if (UserDTO.currentUser?.position ?: "Student" == "Admin") {
            userP = "user";
        } else if ((UserDTO.currentUser?.position ?: "Student" == "Manager")) {
            userP = "student";
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setIcon(R.drawable.warning)
            .setMessage("Are you sure delete this $userP?")
            .setPositiveButton("Delete") { dialog, _ ->
                UserDAL().DeleteUser(user, this)
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun updateUIAdapter(certi : Certificate) {
        certiList.add(certi)
        adapter.notifyDataSetChanged()
    }

    private fun loadUserProfile() {
        if (user.avatarUrl != "") {
            Picasso.get().load(user.avatarUrl).into(binding.imgAvatar)
        } else {
            binding.imgAvatar.setImageResource(R.drawable.user)
        }

        binding.tvUsername2.text = user.name
        binding.tvPosition2.text = user.position
        binding.tvName.setText(user.name)
        binding.tvAge.setText(user.age)
        binding.tvPhone.setText(user.phone)
        binding.tvStatus.setText(user.status)

        if (user.lastLogin.isEmpty()) {
            binding.tvLoginHistory?.text = "User have not logged once"
        }else {
            binding.tvLoginHistory?.text = user.lastLogin
        }
    }

    fun loadUserCertificate() {
        adapter = CertificateListAdapter(certiList, this)
        binding.recyclerViewCertificate?.adapter = adapter
    }

    private fun getUpdateInformation() {
        user.name = binding.tvName.text.toString()
        user.age = binding.tvAge.text.toString()
        user.status = binding.tvStatus.text.toString()
        user.phone = binding.tvPhone.text.toString()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode != PICK_CSV_FILE_REQUEST) {
            val uri: Uri = data?.data!!
            binding.imgAvatar.setImageURI(uri)
            changeAvatar = true

        } else {

            if (requestCode == PICK_CSV_FILE_REQUEST && data != null) {
                fileuri = data.data
                fileuri?.let { loadCSVFile(it) }
            }
        }
    }

    private fun uploadAvatar() {
        val drawable = binding.imgAvatar.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        UserDAL().UpdateAvatar(byteArray, user)
    }

    private fun showAddCertiDialog() {

        val certiDialog = AddCertiDialog("Add certificate for student", this)
        certiDialog.show(supportFragmentManager, "Certificate dialog")

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


    private fun loadCSVFile(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line : String?
            while (reader.readLine().also { line = it } != null) {
                val row : List<String> = line!!.split(",")
                try {
                    var certi = Certificate()
                    certi.certiName = row[0]
                    certi.certiContent = row[1]
                    importedCerti.add(certi)

                    certiList.add(certi)
                    adapter.notifyDataSetChanged()

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            inputStream?.close() // Close the inputStream when done
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Import file error", Toast.LENGTH_SHORT).show()
        }
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


    private fun <T> csvOf (
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
                listOf("name", "content"),
                certiList
            ) {
                listOf(it.certiName, it.certiContent)
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
        }
    }
}