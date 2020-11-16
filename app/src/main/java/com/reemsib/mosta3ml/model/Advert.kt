package com.reemsib.mosta3ml.model

data class Advert(var id:Int,var title:String, var description:String ,
                  var view_number:Int, var price:Int,
                  var price_type:String,var adv_type:String,
                  var accept_negotiation:Int,var end_date:String,var allow_reply:Int,
                  var allow_offer_location:Int,
                  var in_favorite:Boolean,
                  var rate:String,
                  var images:ArrayList<AdvertImage>,
                  var user:User,var city:City,var reviews:ArrayList<Reviews>,var created_at:String) {
}