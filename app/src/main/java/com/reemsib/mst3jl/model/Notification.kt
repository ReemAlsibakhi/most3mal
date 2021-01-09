package com.reemsib.mst3jl.model
import android.os.Parcel
import android.os.Parcelable

data class Notification(
    var id: String,
    var type: String,
    var title: String,
    var message: String,
    var user: User,
    var advertisement_id: String,
    var created_at: String):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeParcelable(user, flags)
        parcel.writeString(advertisement_id)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }

}