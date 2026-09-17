package com.companion.astrodating.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    val body: String,
    val type: String,
    val title: String,
    // Only populated for chat-message pushes (type == NotificationTypeConstants.chatMessage).
    // Gson maps these automatically from the FCM data payload's matching keys;
    // they stay null for every other notification type.
    val senderId: String? = null,
    val senderName: String? = null,
    val profileUrl: String? = null
) : Parcelable
