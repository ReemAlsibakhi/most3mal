package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

    data class AdvertImage(var id:Int,var advertisement_id:Int,var image:String,
                           var adv_type:String,var view_number:Int):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
     )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(advertisement_id)
        parcel.writeString(image)
        parcel.writeString(adv_type)
        parcel.writeInt(view_number)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdvertImage> {
        override fun createFromParcel(parcel: Parcel): AdvertImage {
            return AdvertImage(parcel)
        }

        override fun newArray(size: Int): Array<AdvertImage?> {
            return arrayOfNulls(size)
        }
    }
}