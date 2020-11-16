package com.reemsib.mosta3ml.model

class ChatMessage(

    var user_id:Int,
    var chat_id:Int,
    var message: String,
    var read_at: String,
    var created_at: String
) {
}