package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

data class Reviews(var id:Int, var advertisement:Int, var content:String, var rate:Int,var user:User,var created_at:String) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(advertisement)
        parcel.writeString(content)
        parcel.writeInt(rate)
        parcel.writeParcelable(user, flags)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reviews> {
        override fun createFromParcel(parcel: Parcel): Reviews {
            return Reviews(parcel)
        }

        override fun newArray(size: Int): Array<Reviews?> {
            return arrayOfNulls(size)
        }
    }
}