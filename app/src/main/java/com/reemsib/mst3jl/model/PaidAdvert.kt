package com.reemsib.mst3jl.model

data class PaidAdvert(var id:Int, var title:String, var view_number:Int, var price:String, var adv_type:String, var images:ArrayList<AdvertImage>) {
}