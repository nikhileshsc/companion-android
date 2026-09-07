package com.companion.astrodating.ui.message.domain.model

data class MessageInfo(
    val userId : String,
    val userName : String,
    val message : String,
    val msgTime : Long,
    val isUnread : Boolean,
    val unreadMsgCount : String,
    val profileUrl : String
)
