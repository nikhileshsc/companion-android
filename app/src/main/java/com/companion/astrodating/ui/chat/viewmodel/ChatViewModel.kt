package com.companion.astrodating.ui.chat.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.chat.data.requestData.messageLimitRequestData
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData
import com.companion.astrodating.ui.chat.domain.repository.canSendCallRepository
import com.companion.astrodating.ui.chat.domain.repository.freeMessageLimitRepository
import com.companion.astrodating.ui.chat.domain.repository.sendMesssagePushNotificationRepository
import com.companion.astrodating.util.AGORA_TAG
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.Conversation
import io.agora.chat.CursorResult
import io.agora.chat.FetchMessageOption
import io.agora.chat.Presence
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: freeMessageLimitRepository,
    private val pushNotificationRepository: sendMesssagePushNotificationRepository,
    private val canSendCallRepository: canSendCallRepository
) : ViewModel() {

    private val _checkMessageLimitState: Boolean = false
    private var authToken: String = APP_EMPTY_STRING
    private var _chatConversationHistory: MutableLiveData<MutableList<ChatMessage>> =
        MutableLiveData()
    val getAllChatConversationHistory: LiveData<MutableList<ChatMessage>> = _chatConversationHistory
    private val chatHistoryMessages: MutableList<ChatMessage> = ArrayList()



    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }


    fun fetchChatHistoryMessages(
        chatClient: ChatClient,
        conversationId: String,
        type: Conversation.ConversationType, pageSize: Int,
        cursor: String,
        option: FetchMessageOption
    ) {
        chatClient.chatManager().asyncFetchHistoryMessages(
            conversationId,
            type,
            pageSize,
            cursor,
            option,
            object : ValueCallBack<CursorResult<ChatMessage>?> {
                override fun onSuccess(value: CursorResult<ChatMessage>?) {
                    if (value != null) {
                        val list = value.data
                        Log.e(AGORA_TAG,"list - ${list.size}, ${chatHistoryMessages.size}")
                       chatHistoryMessages.clear()
                        if (list != null && list.size > 0) {
                            chatHistoryMessages.addAll(list)
                            _chatConversationHistory.postValue(chatHistoryMessages)
                        }
                        val newCursor = value.cursor
                        if (!cursor.equals("undefined", true)) {
                            if (!TextUtils.isEmpty(newCursor)) {
                                Log.e(AGORA_TAG,"cursor - $cursor, newCursor-$newCursor, pageSize - $pageSize")

                                fetchChatHistoryMessages(
                                    chatClient,
                                    conversationId,
                                    type,
                                    pageSize,
                                    newCursor,
                                    option
                                )
                            }
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String) {
                    Log.e(AGORA_TAG, "onError: doAsyncFetchHistoryMessages $error, $errorMsg")
                }
            })
    }

    fun checkUserPresence(peerId: String, callback: (String) -> Unit) {
        val list = listOf(peerId)

        ChatClient.getInstance().presenceManager().fetchPresenceStatus(list, object : ValueCallBack<List<Presence>> {
            override fun onSuccess(presenceList: List<Presence>) {
                if (presenceList.isNotEmpty()) {
                    val presence = presenceList[0]
                    val statusMap: MutableMap<String, Int> = presence.statusList

                    if (statusMap.isNotEmpty()) {
                        val state = statusMap.values.toString()  // Common values: "online", "offline", or custom status
                        print(state)
                        callback(state)
                    } else {
                        callback("unknown") // No status published yet
                    }
                } else {
                    callback("not found") // Presence object missing
                }
            }

            override fun onError(code: Int, error: String?) {
                callback("error: $error") // Network or API failure
            }
        })
    }

    fun checkIfMessageCanBeSent(
        senderId: String,
        receiverId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
//            _checkMessageLimitState.value = UiState.Loading
            try {
//                val request = FreeMessageLimitRequest(receiverId) // Use correct request model here
                when (val result = chatRepository.checkfreemessagelimit(
                    messageLimitRequestData(senderId,receiverId)
                )) {
                    is ApiResult.Error -> {
//                        _checkMessageLimitState.value = UiState.Error(result.errorCode, result.errorMessage)
                        print("Error: ${result.errorMessage}")
                        if (result.errorCode == 403 || result.errorCode == 402) {
                            // Genuine backend response: limit actually reached / benefits exhausted
                            onResult(false, result.errorMessage)
                        } else {
                            // Network/timeout/server error - NOT an actual limit reached.
                            // Use a distinguishable marker so the UI shows an accurate
                            // "couldn't verify, try again" message instead of "Limit Reached".
                            onResult(false, "TECHNICAL_ERROR")
                        }
                    }

                    is ApiResult.Success -> {
                        if (result.data.data.allowed) {
//                            _checkMessageLimitState.value = UiState.Success(result.data)
                            onResult(true, null)
                        } else {
//                            _checkMessageLimitState.value = UiState.Error(403, result.data.message ?: "Not allowed")
                            print("Error: ${result.data.message}")
                            onResult(false, result.data.message)
                        }
                    }
                }
            } catch (e: Exception) {
//                _checkMessageLimitState.value = UiState.Error(0, e.message.toString())
                print("Error: $e")
                onResult(false, e.message)
            }
        }
    }

    fun sendMessagePushNotification(
        receiverId: String,
        message: String
    ){
        try{
            viewModelScope.launch { when(pushNotificationRepository.sendMessagePushNotification(
                authToken,
                sendMessagePushNotificationData(receiverId, message)
            )){
                is ApiResult.Error -> {
                }
                is ApiResult.Success -> {
                }
            } }
        }catch (e: Exception){
        }
    }

    fun checkIfCallCanBeSent(
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                when (val result = canSendCallRepository.canSendCall(
                    authToken
                )) {
                    is ApiResult.Error -> {
                        onResult(false, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        if (result.data.allowed == true) {
                            onResult(true, null)
                        } else {
                            onResult(false, result.data.message)
                        }
                    }
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}


