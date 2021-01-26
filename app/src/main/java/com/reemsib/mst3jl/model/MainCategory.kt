package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

data class MainCategory(var id:Int, var name:String, var image:String, var has_models:Int):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeInt(has_models)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainCategory> {
        override fun createFromParcel(parcel: Parcel): MainCategory {
            return MainCategory(parcel)
        }

        override fun newArray(size: Int): Array<MainCategory?> {
            return arrayOfNulls(size)
        }
    }
}