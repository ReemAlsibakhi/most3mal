package com.reemsib.mst3jl.utils

import android.content.Context
import android.graphics.Color
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower

class CustomProgressDialog private constructor(context: Context) {

      init {
          ctx = context
          dialog = ACProgressFlower.Builder(ctx).
          direction(ACProgressConstant.DIRECT_CLOCKWISE).
          themeColor(Color.WHITE).fadeColor(Color.DKGRAY).build()
      }
      fun showDialog(){
           dialog!!.show()

      }
      fun hideDialog(){
           dialog!!.dismiss()


      }

     companion object {

         private var mInstance: CustomProgressDialog? = null
         private var ctx: Context? = null
         var dialog: ACProgressFlower ? = null


         @Synchronized
         fun getInstance(context: Context): CustomProgressDialog {
             if (mInstance == null) {
                 mInstance = CustomProgressDialog(context)
             }
             return mInstance  as CustomProgressDialog
         }

     }
}