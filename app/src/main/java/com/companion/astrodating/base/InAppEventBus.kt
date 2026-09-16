package com.companion.astrodating.base

import androidx.lifecycle.MutableLiveData

/**
 * Lightweight in-process event bus used to notify the UI (HomePageActivity)
 * about events that should surface as an in-app pop-up card and/or a
 * bottom-navigation badge, regardless of where those events originate
 * (FCM push, Agora chat unread-count polling, etc).
 *
 * Alerts are queued: only one card is shown at a time. If a new alert
 * arrives while one is already on screen, it waits its turn - nothing is
 * dropped. Call [advance] once the current alert has been dismissed (the
 * user tapped it) to show the next queued one, if any.
 */
data class InAppAlertEvent(
    val type: String,
    val title: String,
    val body: String,
    // Only populated for TYPE_CHAT_MESSAGE - the Agora conversation id for
    // the sender, which doubles as SEC_USER_ID elsewhere in the app.
    val conversationId: String? = null,
    val senderName: String? = null,
    val senderAvatarUrl: String? = null
)

object InAppEventBus {

    const val TYPE_CHAT_MESSAGE = "chat_message"

    /** The alert currently shown on screen, or null if none is showing. */
    val currentAlert = MutableLiveData<InAppAlertEvent?>()

    val interestBadgeCount = MutableLiveData(0)

    private val pendingAlerts = ArrayDeque<InAppAlertEvent>()
    @Volatile private var isShowingAlert = false

    @Synchronized
    fun postAlert(event: InAppAlertEvent) {
        if (isShowingAlert) {
            pendingAlerts.addLast(event)
        } else {
            isShowingAlert = true
            currentAlert.postValue(event)
        }
    }

    /** Call after the current alert is dismissed to show the next queued alert, if any. */
    @Synchronized
    fun advance() {
        val next = pendingAlerts.removeFirstOrNull()
        isShowingAlert = next != null
        currentAlert.postValue(next)
    }

    fun incrementInterestBadge() {
        val current = interestBadgeCount.value ?: 0
        interestBadgeCount.postValue(current + 1)
    }

    fun resetInterestBadge() {
        interestBadgeCount.postValue(0)
    }
}
