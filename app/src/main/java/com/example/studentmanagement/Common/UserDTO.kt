package com.example.studentmanagement.Common

import android.graphics.Bitmap
import com.example.studentmanagement.Domain.User
import java.util.Calendar

class UserDTO{
    companion object {
        var currentUser: User? = null
        var userAvatar: Bitmap? = null
        var lastLogin: String = Calendar.getInstance().time.toString()
    }
}