package com.example.studentmanagement.DB

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.studentmanagement.Activity.AddNewUserActivity
import com.example.studentmanagement.Activity.HomePageActivity
import com.example.studentmanagement.Activity.LoginActivity
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.Activity.UserManagementActivity
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDAL : DBConnection(){
    fun GetUserRef(): DatabaseReference {
        return database.getReference("User")
    }

    fun CreateNewUser(user : User, activity : AddNewUserActivity) {
        var pk = GetUserRef().push().key
        if (pk != null) {
            user.pk = pk
        }
        GetUserRef().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (pk?.let { snapshot.hasChild(it) } == true){
                    Toast.makeText(activity.applicationContext,"Number phone is already registered", Toast.LENGTH_SHORT).show()
                }else{
                    if (pk != null) {
                        GetUserRef().child(pk).setValue(user)
                    }
                    Toast.makeText(activity.applicationContext, "Create user successfully", Toast.LENGTH_SHORT).show()
                    var intent = Intent(activity, HomePageActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                Toast.makeText(activity.applicationContext,"Fail to register, error system!", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun UserObjectRef(user : User) : DatabaseReference {
        return GetUserRef().child(user.pk)
    }

    fun UpdateUserProfile(user : User, activity : ProfileActivity) {

        var objRef = UserObjectRef(user)
        objRef.child("age")?.setValue(user.age)
        objRef.child("name")?.setValue(user.name)
        objRef.child("password")?.setValue(user.password)
        objRef.child("position")?.setValue(user.position)
        objRef.child("phone")?.setValue(user.phone)

    }

    fun LoginUser(phone : String, pass : String, activity: LoginActivity) {

        if (phone.contains('.') || phone.contains('#')
            || phone.contains('$') || phone.contains('[')
            || phone.contains(']')) {

            activity.binding.edtPhone.error = "User is not exist"
            activity.binding.edtPhone.requestFocus()

        }else {
            GetUserRef().addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Check if pk in database
                    if (snapshot.hasChild(phone)){
                        val user = snapshot.child(phone).getValue(User::class.java)
                        if (user != null) {
                            if(user.phone == phone && user.password == pass){
                                if (user.status == "Locked") {
                                    Toast.makeText(activity.applicationContext, "Your account is locked !!!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Save current user
                                    UserDTO.currentUser = user

                                    // Save user's avatar to local
                                    if (user.avatarUrl != "") {
                                        UserDTO.currentUser?.let {PicassoToBitmap(it.avatarUrl) }
                                    }

                                    Toast.makeText(activity.applicationContext, "Login successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(activity.applicationContext, HomePageActivity::class.java)
                                    activity.startActivity(intent)
                                }
                            }else{
                                activity.binding.edtPassword.error = "Wrong password"
                                activity.binding.edtPassword.requestFocus()
                            }
                        }
                    }else{
                        activity.binding.edtPhone.error = "User is not exist"
                        activity.binding.edtPhone.requestFocus()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity.applicationContext, "System error", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun UpdateAvatar (byteArray : ByteArray) {
        val uploadTask = UserDTO.currentUser.phone?.let { storageRef.child("$it.jpeg").putBytes(byteArray) }
        if (uploadTask != null) {
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val downloadUrlTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    val root = UserDTO.currentUser.phone?.let { it1 -> GetUserRef().child(it1) }
                    val newAvatarUrl = uri.toString()
                    root?.child("avatarUrl")?.setValue(newAvatarUrl)
                    UserDTO.currentUser?.let {PicassoToBitmap(newAvatarUrl) }
                }
            }?.addOnFailureListener {

            }
        }
    }

    fun GetListOfUser(position : String, activity : UserManagementActivity) {
        val query : Query
        if (position == "") {
            query = GetUserRef().orderByChild("name")
        } else {
            query = GetUserRef().orderByChild("position").equalTo(position)
        }
        activity.userList.clear()
        query .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.position != "Admin") {
                            activity.userList.add(user)
                        }
                    }
                }
                activity.loadListOfUser()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })
    }

    fun PicassoToBitmap(imgUrl : String) {
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    UserDTO.userAvatar = bitmap
                }
            }
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        Picasso.get().load(imgUrl).into(target)
    }
}