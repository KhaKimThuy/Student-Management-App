package com.example.studentmanagement.Domain

import android.os.Parcel
import android.os.Parcelable

class User() : Parcelable{
    var pk : String = ""
    var phone : String = ""
    var password : String = ""
    var name : String = ""
    var avatarUrl : String = ""
    var position : String = ""
    var age : String = ""
    var status : String = ""

    constructor(parcel: Parcel) : this() {
        pk = parcel.readString().toString()
        phone = parcel.readString().toString()
        password = parcel.readString().toString()
        name = parcel.readString().toString()
        avatarUrl = parcel.readString().toString()
        position = parcel.readString().toString()
        age = parcel.readString().toString()
        status = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(pk)
        parcel.writeString(password)
        parcel.writeString(name)
        parcel.writeString(avatarUrl)
        parcel.writeString(position)
        parcel.writeString(age)
        parcel.writeString(status)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}