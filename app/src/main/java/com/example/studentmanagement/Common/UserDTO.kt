package com.example.studentmanagement.Common

import android.graphics.Bitmap
import com.example.studentmanagement.Domain.User

class UserDTO{
    companion object {
        lateinit var currentUser: User
        var userAvatar: Bitmap? = null
    }
}