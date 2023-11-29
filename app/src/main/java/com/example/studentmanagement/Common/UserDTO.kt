package com.example.studentmanagement.Common

import android.graphics.Bitmap
import com.example.studentmanagement.Domain.User
import java.util.Calendar

class UserDTO{
    companion object {
        lateinit var currentUser: User
        lateinit var userAvatar: Bitmap
        var lastLogin: String = Calendar.getInstance().time.toString()
    }
}