package com.companion.astrodating.base

import androidx.lifecycle.MutableLiveData

/**
 * Lightweight in-process event bus used to notify the UI (HomePageActivity)
 * about events that should surface as an in-app pop-up card and/or a
 * bottom-navigation badge, regardless of where those events originate
 * (FCM push, Agora chat unread-count polling, etc).
 */
data class InAppAlertEvent(
    val type: String,
    val title: String,
    val body: String
)

object InAppEventBus {

    const val TYPE_CHAT_MESSAGE = "chat_message"

    val alertEvent = MutableLiveData<InAppAlertEvent?>()
    val interestBadgeCount = MutableLiveData(0)

    fun postAlert(event: InAppAlertEvent) {
        alertEvent.postValue(event)
    }

    fun consumeAlert() {
        alertEvent.postValue(null)
    }

    fun incrementInterestBadge() {
        val current = interestBadgeCount.value ?: 0
        interestBadgeCount.postValue(current + 1)
    }

    fun resetInterestBadge() {
        interestBadgeCount.postValue(0)
    }
}
