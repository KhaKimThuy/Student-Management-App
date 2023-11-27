package com.example.studentmanagement.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentmanagement.Activity.UserManagementActivity
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

        binding.cardViewUserManagement.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), UserManagementActivity::class.java)
            requireActivity().startActivity(intent)
        })

    }
}