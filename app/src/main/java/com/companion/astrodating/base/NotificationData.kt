package com.companion.astrodating.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    val body: String,
    val type: String,
    val title: String
) : Parcelable
