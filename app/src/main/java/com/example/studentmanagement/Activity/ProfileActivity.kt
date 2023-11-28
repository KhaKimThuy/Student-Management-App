package com.example.studentmanagement.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanagement.Adapter.CertificateListAdapter
import com.example.studentmanagement.DB.CertificateDAL
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.Dialog.AddCertiDialog
import com.example.studentmanagement.Domain.Certificate
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    lateinit var user : User
    lateinit var adapter: CertificateListAdapter
    private var userPosition : Int = -1
    private var changeAvatar : Boolean = false
    lateinit var certiList : ArrayList<Certificate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra("user")!!
        userPosition = intent.getIntExtra("position", -1)

        loadUserProfile()
        certiList = ArrayList<Certificate>()
        CertificateDAL().GetListOfCerti(user, this)

        binding.recyclerViewCertificate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCertificate.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerViewCertificate.context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.btnUpdate.setOnClickListener(View.OnClickListener {
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

        binding.imageViewDelete.setOnClickListener(View.OnClickListener {
            UserDAL().DeleteUser(user, this)
            finish()
        })

        binding.imageViewCamera.setOnClickListener(View.OnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        })

        binding.imageViewAddCertificate.setOnClickListener(View.OnClickListener {
            showAddCertiDialog()
        })

//        registerForContextMenu(binding.recyclerViewCertificate)
    }

//    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        menuInflater.inflate(R.menu.certificate_menu, menu)
//    }


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

    }

    fun loadUserCertificate() {
        adapter = CertificateListAdapter(certiList, this)
        binding.recyclerViewCertificate.adapter = adapter
    }

    private fun getUpdateInformation() {
        user.name = binding.tvName.text.toString()
        user.age = binding.tvAge.text.toString()
        user.status = binding.tvStatus.text.toString()
        user.phone = binding.tvPhone.text.toString()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            binding.imgAvatar.setImageURI(uri)
            changeAvatar = true
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
}