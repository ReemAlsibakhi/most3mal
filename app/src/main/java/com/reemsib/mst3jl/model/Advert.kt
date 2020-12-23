package com.reemsib.mst3jl.model
import android.os.Parcel
import android.os.Parcelable

data class Advert(
    var id: Int,
    var title: String,
    var description: String,
    var view_number: Int,
    var price: Int,
    var price_type: String,
    var adv_type: String,
    var accept_negotiation: Int,
    var allow_reply: Int,
    var allow_offer_location: Int,
    var in_favorite: Boolean,
    var rate: String,
    var distance: Int,
    var images: ArrayList<Image>,
    var category: Category,
    var user: User,
    //  var year: ModelYear,
   //  var company: Company,
    var city: City,
    var reviews: ArrayList<Reviews>,
    var created_at: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.createTypedArrayList(Image.CREATOR) as ArrayList<Image>,
        parcel.readParcelable(Category::class.java.classLoader)!!,
        parcel.readParcelable(User::class.java.classLoader)!!,
       // parcel.readParcelable(ModelYear::class.java.classLoader)!!,
//        parcel.readParcelable(Company::class.java.classLoader)!!,
        parcel.readParcelable(City::class.java.classLoader)!!,
        parcel.createTypedArrayList(Reviews.CREATOR) as ArrayList<Reviews>,
        parcel.readString()!!
    ) {

    }
    //constructor():this(-1,"",)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(view_number)
        parcel.writeInt(price)
        parcel.writeString(price_type)
        parcel.writeString(adv_type)
        parcel.writeInt(accept_negotiation)
        parcel.writeInt(allow_reply)
        parcel.writeInt(allow_offer_location)
        parcel.writeByte(if (in_favorite) 1 else 0)
        parcel.writeString(rate)
        parcel.writeInt(distance)
        parcel.writeTypedList(images)
        parcel.writeParcelable(category, flags)
        parcel.writeParcelable(user, flags)
        //arcel.writeParcelable(year, flags)
      //  parcel.writeParcelable(company, flags)
        parcel.writeParcelable(city, flags)
        parcel.writeTypedList(reviews)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Advert> {
        override fun createFromParcel(parcel: Parcel): Advert {
            return Advert(parcel)
        }

        override fun newArray(size: Int): Array<Advert?> {
            return arrayOfNulls(size)
        }
    }

}