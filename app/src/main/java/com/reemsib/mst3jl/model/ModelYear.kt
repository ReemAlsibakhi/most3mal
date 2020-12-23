package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

data class ModelYear(var id:Int, var year:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(year)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelYear> {
        override fun createFromParcel(parcel: Parcel): ModelYear {
            return ModelYear(parcel)
        }

        override fun newArray(size: Int): Array<ModelYear?> {
            return arrayOfNulls(size)
        }
    }
}