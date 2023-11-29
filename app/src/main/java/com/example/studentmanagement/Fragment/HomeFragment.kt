package com.example.studentmanagement.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentmanagement.Activity.LoginActivity
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.Activity.UserManagementActivity
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.R
import com.example.studentmanagement.databinding.ActivityUserManagementBinding
import com.example.studentmanagement.databinding.FragmentHomeBinding
import com.example.studentmanagement.databinding.FragmentProfileBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (UserDTO.currentUser!!.position != "Student") {
            binding.cardViewUserManagement.setOnClickListener(View.OnClickListener {
                val intent = Intent(requireActivity(), UserManagementActivity::class.java)
                requireActivity().startActivity(intent)
            })
        } else {
            binding.cardViewUserManagement.visibility = View.GONE
        }

        binding.cardViewProfile?.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra("position", -1)
            intent.putExtra("user", UserDTO.currentUser)
            startActivity(intent)
        })

        binding.cardViewLogout?.setOnClickListener(View.OnClickListener {
//            editor.putString(LoginActivity.PHONE_KEY, null)
//            editor.putString(LoginActivity.PASSWORD_KEY, null)
//            editor.putBoolean("hasLoggedIn", false)
//            editor.apply()

            val sharedPreferences = context?.getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.apply()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        })
    }
}