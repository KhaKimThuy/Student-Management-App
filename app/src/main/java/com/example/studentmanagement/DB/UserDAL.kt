package com.example.studentmanagement.DB

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.studentmanagement.Activity.AddNewUserActivity
import com.example.studentmanagement.Activity.HomePageActivity
import com.example.studentmanagement.Activity.LoginActivity
import com.example.studentmanagement.Activity.MainActivity
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.Activity.UserManagementActivity
import com.example.studentmanagement.Common.UserDTO
import com.example.studentmanagement.Domain.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale




class UserDAL : DBConnection(){
    fun GetUserRef(): DatabaseReference {
        return database.getReference("User")
    }

    fun CreateNewUser(user : User, activity : AddNewUserActivity? = null) {
        var pk = GetUserRef().push().key
        if (pk != null) {
            user.pk = pk
        }
        GetUserRef().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (pk?.let { snapshot.hasChild(it) } == true){
                    if (activity != null) {
                        Toast.makeText(activity.applicationContext,"Number phone is already registered", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if (pk != null) {
                        GetUserRef().child(pk).setValue(user)
                    }
                    if (activity != null) {
                        Toast.makeText(activity.applicationContext, "Create user successfully", Toast.LENGTH_SHORT).show()
                    }
                    activity?.finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                if (activity != null) {
                    Toast.makeText(activity.applicationContext,"Fail to register, error system!", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun UserObjectRef(user : User) : DatabaseReference {
        return GetUserRef().child(user.pk)
    }

    fun UpdateUserProfile(user : User) {
        var objRef = UserObjectRef(user)
        objRef.child("age")?.setValue(user.age)
        objRef.child("name")?.setValue(user.name)
        objRef.child("password")?.setValue(user.password)
        objRef.child("position")?.setValue(user.position)
        objRef.child("phone")?.setValue(user.phone)
    }

    fun LoginUser(phone : String?, pass : String?, activity: LoginActivity) {
            val query = GetUserRef().orderByChild("phone")
                .equalTo(phone).limitToFirst(1)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount < 1) {
                        activity.binding.edtPhone.error = "User is not exist"
                        activity.binding.edtPhone.requestFocus()
                    }else{
                        for (snapshot in dataSnapshot.children) {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                if (user.password == pass) {
// Init current user
                                    UserDTO.currentUser = User()
                                    UserDTO.currentUser = user
                                    UserDTO.lastLogin = user.lastLogin

                                    UpdateLastLogin(user)

                                    if (user.avatarUrl != "") {
                                        PicassoToBitmap(user.avatarUrl)
                                    }

                                    val editor = activity.sharedpreferences.edit()
                                    editor.putString(LoginActivity.PHONE_KEY, user.phone)
                                    editor.putString(LoginActivity.PASSWORD_KEY, user.password)
                                    editor.putBoolean("hasLoggedIn", true)
                                    editor.apply()

                                    Toast.makeText(activity.applicationContext, "Login successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(activity.applicationContext, HomePageActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()

                                } else {
                                    activity.binding.edtPassword.error = "Wrong password"
                                    activity.binding.edtPassword.requestFocus()
                                }
                            }
                        }
                    }

                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                }
            })
    }

    fun LoadUser(phone : String?, pass : String?, activity : MainActivity) {
        val query = GetUserRef().orderByChild("phone")
            .equalTo(phone).limitToFirst(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            if (user.password == pass) {
                                UserDTO.currentUser = User()
                                UserDTO.currentUser = user
                                UserDTO.lastLogin = user.lastLogin
                                UpdateLastLogin(user)
                                if (user.avatarUrl != "") {
                                    PicassoToBitmap(user.avatarUrl)
                                }
                                val intent = Intent(activity, HomePageActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()
                            }
                        }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })
    }

    fun SortUserList(criteriaList: ArrayList<String>, orderList: ArrayList<Boolean>, userList : ArrayList<User>) : ArrayList<User>{
        var sortedList = ArrayList<User>()
        sortedList.addAll(userList)
        for (idx in 0..< criteriaList.size) {
            // Sort by age
            if (criteriaList[idx] == "Age") {
                sortedList = if (orderList[idx]){
                    ArrayList<User>(sortedList.sortedByDescending{it.age.toInt()})
                } else {
                    ArrayList<User>(sortedList.sortedBy{it.age.toInt()})
                }
            }

            // Sort by name
            else if (criteriaList[idx] == "Name") {
                sortedList = if (orderList[idx]) {
                    ArrayList<User>(sortedList.sortedByDescending{it.name})
                } else {
                    ArrayList<User>(sortedList.sortedBy{it.name})
                }
            }

            // Sort by login time
            else if (criteriaList[idx] == "The last time login") {
                sortedList = if (orderList[idx]) {
                    ArrayList<User>(sortedList.sortedByDescending{
                        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        sdf.parse(it.lastLogin).time // all done
//                        sdf.parse(it.lastLogin).time
                    })
                } else {
                    ArrayList<User>(sortedList.sortedBy{
                        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        sdf.parse(it.lastLogin).time // all done
//                        sdf.parse(it.lastLogin).time
                    })
                }
            }
        }

        return sortedList
    }

//    fun SortUserByAge(userList: ArrayList<User>) : ArrayList<User>{
//        return ArrayList<User>(userList.sortedBy{it.age.toInt()})
//    }
//    fun SortUserByName(userList: ArrayList<User>) : ArrayList<User>{
//        return ArrayList<User>(userList.sortedBy{it.name})
//    }
//    fun SortUserByTimeLogin (userList: ArrayList<User>) : ArrayList<User>{
//        return ArrayList<User>(userList.sortedBy {
//            val sdf = SimpleDateFormat("dd/MM/yyyy '@'hh:mm a")
//            sdf.parse(it.lastLogin).time
//        })
//    }

    fun UpdateLastLogin(user : User) {
        UserObjectRef(user).child("lastLogin").setValue(Calendar.getInstance().time.toString())
    }

    fun UpdateAvatar (byteArray : ByteArray, user : User) {
        val uploadTask = user.pk?.let { storageRef.child("$it.jpeg").putBytes(byteArray) }
        if (uploadTask != null) {
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val downloadUrlTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    val root = UserObjectRef(user)
                    val newAvatarUrl = uri.toString()
//                    PicassoToBitmap(newAvatarUrl)
                    root?.child("avatarUrl")?.setValue(newAvatarUrl)
                }
            }?.addOnFailureListener {

            }
        }
    }

    fun GetListOfUser(activity : UserManagementActivity) {
//        val query : Query
//        if (position == "") {
//            query = GetUserRef().orderByChild("name")
//        } else {
//            query = GetUserRef().orderByChild("position").equalTo(position)
//        }
        GetUserRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (!activity.updateUser) {
                    activity.userList.clear()
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot != null) {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                if (user.position == "Student") {
                                    activity.userList.add(user)
                                }
                            }
                        }
//                    }
                    activity.updateUser = true
                    activity.loadListOfUser()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })
    }

    fun DeleteUser(user : User, activity: ProfileActivity) {
        GetUserRef().child(user.pk).removeValue()
            .addOnSuccessListener {
                Toast.makeText(activity, "Delete user successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                // Handle the error
                Toast.makeText(activity, "Fail to delete", Toast.LENGTH_SHORT).show()
            }
    }

    fun PicassoToBitmap(imgUrl : String) {
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    Log.d("TAG", "HOme fragment --- ")
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