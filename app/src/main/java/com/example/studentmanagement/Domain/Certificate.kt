package com.example.studentmanagement.Domain

import android.os.Parcel
import android.os.Parcelable

class Certificate() : Parcelable{
    var pk : String = ""
    var userPK : String = ""
    var certiName : String = ""
    var certiContent : String = ""

    constructor(parcel: Parcel) : this() {
        pk = parcel.readString().toString()
        userPK = parcel.readString().toString()
        certiName = parcel.readString().toString()
        certiContent = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pk)
        parcel.writeString(userPK)
        parcel.writeString(certiName)
        parcel.writeString(certiContent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Certificate> {
        override fun createFromParcel(parcel: Parcel): Certificate {
            return Certificate(parcel)
        }

        override fun newArray(size: Int): Array<Certificate?> {
            return arrayOfNulls(size)
        }
    }
}

