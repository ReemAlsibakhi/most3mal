package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

data class Comentator(var id: Int, var name: String, var mobile:String) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(mobile)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comentator> {
        override fun createFromParcel(parcel: Parcel): Comentator {
            return Comentator(parcel)
        }

        override fun newArray(size: Int): Array<Comentator?> {
            return arrayOfNulls(size)
        }
    }
}