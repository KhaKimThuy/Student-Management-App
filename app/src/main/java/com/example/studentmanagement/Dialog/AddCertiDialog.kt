package com.example.studentmanagement.Dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.DB.CertificateDAL
import com.example.studentmanagement.Domain.Certificate
import com.example.studentmanagement.R

class AddCertiDialog(
    title: String,
    activity: ProfileActivity? = null) : AppCompatDialogFragment() {

    private lateinit var certiName : EditText
    private lateinit var certiDesc : EditText
    private val title = title
    private val activity = activity

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_certi_dialog, null)
        certiName = view.findViewById(R.id.edt_certiName)
        certiDesc = view.findViewById(R.id.edt_certiDesc)

        builder.setView(view)
            .setTitle(title)
            .setNegativeButton("CANCEL") { _, _ ->
            }
            .setPositiveButton("ADD") { dialog, _ ->
                    if (addCerti()) {
                        dialog.dismiss()
                    }
            }



        return builder.create()
    }

    private fun addCerti() : Boolean {
        if (certiName.text.toString().isEmpty()) {
            certiName.error = "Certificate name is required"
        } else {
            val certi = Certificate()
            certi.pk = CertificateDAL().GetCertificateRef().push().key!!
            certi.userPK = activity?.user?.pk ?: "null"
            certi.certiName = certiName.text.toString()
            certi.certiContent = certiDesc.text.toString()
            if (activity != null) {
                CertificateDAL().CreateNewCerti(certi, activity)
                return true
            }
        }
        return false
    }
}