package com.example.studentmanagement.DB

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

open class DBConnection {
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference.child("UserAvatars")
}