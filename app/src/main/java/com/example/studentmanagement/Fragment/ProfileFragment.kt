package com.example.studentmanagement.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.DB.UserDAL
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.FragmentProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data!!
            binding.imgAvatar.setImageURI(fileUri)

            val inputStream = fileUri?.let { requireActivity().contentResolver.openInputStream(it) }
            UserDTO.userAvatar =  BitmapFactory.decodeStream(inputStream)
            uploadAvatar()

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
        
        binding.imageViewCamera.setOnClickListener(View.OnClickListener {

            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        })
    }

    fun loadUserProfile() {
        if (UserDTO.currentUser?.avatarUrl ?: "" == "") {
            binding.imgAvatar.setImageResource(R.drawable.user)
        } else {
            binding.imgAvatar.setImageBitmap(UserDTO.userAvatar)
        }
        binding.tvUsername2.text = UserDTO.currentUser?.name ?: "Error"
        binding.tvPosition2.text = UserDTO.currentUser?.position ?: "Error"
        binding.tvName.text = UserDTO.currentUser?.name ?: "Error"
        binding.tvAge.text = UserDTO.currentUser?.age ?: "Error"
        binding.tvPhone.text = UserDTO.currentUser?.phone ?: "Error"
        binding.tvStatus.text = UserDTO.currentUser?.status ?: "Error"
    }

    private fun uploadAvatar() {
        val drawable = binding.imgAvatar.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()



        UserDTO.currentUser?.let { UserDAL().UpdateAvatar(byteArray, it) }
    }
}