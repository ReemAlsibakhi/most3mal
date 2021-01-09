package com.reemsib.mst3jl.model

import android.os.Parcel
import android.os.Parcelable

class ChatInfo(
    var id:Int,
    var user1_id:Int,
    var user2_id:Int,
    var created_at: String,
    var messages: ArrayList<Message>,
    var user1: User,
    var user2: User):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Message.CREATOR) as ArrayList<Message>,
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.readParcelable(User::class.java.classLoader)!!
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(user1_id)
        parcel.writeInt(user2_id)
        parcel.writeString(created_at)
        parcel.writeTypedList(messages)
        parcel.writeParcelable(user1, flags)
        parcel.writeParcelable(user2, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatInfo> {
        override fun createFromParcel(parcel: Parcel): ChatInfo {
            return ChatInfo(parcel)
        }

        override fun newArray(size: Int): Array<ChatInfo?> {
            return arrayOfNulls(size)
        }
    }


}