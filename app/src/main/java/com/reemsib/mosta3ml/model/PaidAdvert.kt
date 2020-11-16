package com.reemsib.mosta3ml.model

data class PaidAdvert(var id:Int, var title:String, var view_number:Int, var price:String, var adv_type:String, var images:ArrayList<AdvertImage>) {
}