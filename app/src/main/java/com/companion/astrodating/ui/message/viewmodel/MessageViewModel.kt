package com.companion.astrodating.ui.message.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IUpdateBenefitsRepository
import com.companion.astrodating.ui.states.MessageUiState
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.Conversation
import io.agora.chat.CursorResult
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: IUpdateBenefitsRepository
) : ViewModel() {

    private var authToken: String = APP_EMPTY_STRING
    private var _chatConversationList: MutableLiveData<MutableList<Conversation>> =
        MutableLiveData()
    val allChatConversationList: LiveData<MutableList<Conversation>> = _chatConversationList
    private val chatConversationList: MutableList<Conversation> = ArrayList()

    private var _state: MutableLiveData<MessageUiState<UpdateBenefitsDomainDetails>> =
        MutableLiveData<MessageUiState<UpdateBenefitsDomainDetails>>()
    val state: LiveData<MessageUiState<UpdateBenefitsDomainDetails>> = _state

    var userId = ""
    var userName = ""
    var profileUrl = ""

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun updateBenefitByType(token: String,requestData: UpdateBenefitRequestData,id: String, name: String, profile: String) {
        viewModelScope.launch {
            _state.value = MessageUiState.Loading
            try {
                userId = id
                userName = name
                profileUrl = profile

                when (val result = repository.updateBenefitsDetails(token,requestData)) {
                    is ApiResult.Error -> {
                        _state.value = MessageUiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        print("!!!!Result code: ")
                        print(result.data.statusCode)
                        _state.value = MessageUiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _state.value = MessageUiState.Error(0, e.message.toString())
            }

        }
    }

    fun resetState() {
        _state.value = MessageUiState.Idle
    }


    fun doAsyncFetchConversationsFromServer(
        chatClient: ChatClient,
        limit: Int,
        cursor: String
    ) {
        chatClient.chatManager().asyncFetchConversationsFromServer(
            limit,
            cursor,
            object : ValueCallBack<CursorResult<Conversation>?> {
                override fun onSuccess(value: CursorResult<Conversation>?) {
                    if (value != null) {
                        val list = value.data
                        chatConversationList.clear()
                        Log.e(TAG,"===${list.size}, $list")
                        if (list != null && list.size > 0) {
                            chatConversationList.addAll(list)
                        }
                        _chatConversationList.postValue(chatConversationList)
                        val newCursor = value.cursor
                        if (!cursor.equals("undefined", true)) {
                            if (newCursor!=null && !TextUtils.isEmpty(newCursor)) {
                                Log.e(TAG,"=== doAsyncFetchConversationsFromServer ${list.size}, $list")
                                doAsyncFetchConversationsFromServer(chatClient,limit, newCursor)
                            }
                        }
                    }

                }

                override fun onError(error: Int, errorMsg: String) {
                    Log.e(TAG, "onError: doAsyncFetchConversationsFromServer $error, $errorMsg")
                    if (error==300){
//                        303, Unknown server error
                    }

                }
            })


    }
}