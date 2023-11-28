package com.example.studentmanagement.Common

import android.graphics.Bitmap
import com.example.studentmanagement.Domain.User

class UserDTO{
    companion object {
        var currentUser: User? = null
        var userAvatar: Bitmap? = null
    }
}