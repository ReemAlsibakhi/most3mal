package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

data class AllCategory(var id:Int, var name:String, var image:String,var sub_categories:ArrayList<SubCategory>):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readArrayList(SubCategory::class.java.classLoader)!! as ArrayList<SubCategory>,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllCategory> {
        override fun createFromParcel(parcel: Parcel): AllCategory {
            return AllCategory(parcel)
        }

        override fun newArray(size: Int): Array<AllCategory?> {
            return arrayOfNulls(size)
        }
    }
}