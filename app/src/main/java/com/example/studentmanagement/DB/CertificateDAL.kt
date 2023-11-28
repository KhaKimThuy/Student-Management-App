package com.example.studentmanagement.DB

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.studentmanagement.Activity.AddNewUserActivity
import com.example.studentmanagement.Activity.HomePageActivity
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.Activity.UserManagementActivity
import com.example.studentmanagement.Domain.Certificate
import com.example.studentmanagement.Domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class CertificateDAL : DBConnection() {
    fun GetCertificateRef() : DatabaseReference {
        return database.getReference("Certificate")
    }

    fun CreateNewCerti(certi : Certificate, activity : ProfileActivity) {
        Toast.makeText(activity, "System error", Toast.LENGTH_SHORT).show()
        GetCertificateRef().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (certi.pk?.let { snapshot.hasChild(it) } == true){
                    Toast.makeText(activity, "Create certificate error", Toast.LENGTH_SHORT).show()
                } else {
                    if (certi.pk != null) {
                        GetCertificateRef().child(certi.pk).setValue(certi)
                        Toast.makeText(activity, "Create certificate successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("Create folder", "System error")
            }
        })
    }


    fun CertiObjectRef(certi : Certificate) : DatabaseReference {
        return GetCertificateRef().child(certi.pk)
    }

    fun UpdateCerti(certi : Certificate) {
        var objRef = CertiObjectRef(certi)
        objRef.child("certiName")?.setValue(certi.certiName)
        objRef.child("certiContent")?.setValue(certi.certiContent)
    }

    fun GetListOfCerti(user : User, activity : ProfileActivity) {
        val query = GetCertificateRef().orderByChild("userPK").equalTo(user.pk)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                activity.certiList.clear()
                for (snapshot in dataSnapshot.children) {
                    if (snapshot != null) {
                        val certi = snapshot.getValue(Certificate::class.java)
                        if (certi != null) {
                            activity.certiList.add(certi)
                        }
                    }
                    activity.loadUserCertificate()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun DeleteCerti(certi : Certificate) {
        GetCertificateRef().child(certi.pk).removeValue()
            .addOnSuccessListener {
            }
            .addOnFailureListener { error ->
                // Handle the error
            }
    }

}