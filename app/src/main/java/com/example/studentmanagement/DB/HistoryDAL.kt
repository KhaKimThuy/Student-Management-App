package com.example.studentmanagement.DB

import com.example.studentmanagement.Common.UserDTO
import com.google.firebase.database.DatabaseReference

class HistoryDAL : DBConnection() {
    fun GetHistoryRef(): DatabaseReference {
        return database.getReference("History")
    }

    fun AddHistoryLoginForUser() {
//        UserDTO.currentUser?.pk
//        var history = UserDTO.currentUser
//        val loginHistoryEntry = GetHistoryRef().push()
//        loginHistoryEntry.setValue(currentDateTime.toString())
//            .addOnSuccessListener {
//                // Login history saved successfully
//            }
//            .addOnFailureListener { error ->
//                // Failed to save login history
//            }
    }
}