package com.reemsib.mosta3ml.model

class ChatRoom(
    var id:Int,
    var unread_messages_count:Int,
    var message:ChatMessage,
    var user: User,
    var created_at: String
) {
}