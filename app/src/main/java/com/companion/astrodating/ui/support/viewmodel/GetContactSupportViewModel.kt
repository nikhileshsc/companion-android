package com.companion.astrodating.ui.support.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.support.domain.model.ContactSupportDomainDetails
import com.companion.astrodating.ui.support.domain.repository.IGetContactSupportRepository
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetContactSupportViewModel @Inject constructor(
    private val repository: IGetContactSupportRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _state: MutableLiveData<UiState<ContactSupportDomainDetails>> =
        MutableLiveData<UiState<ContactSupportDomainDetails>>()
    val state: LiveData<UiState<ContactSupportDomainDetails>> = _state

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun createNewTicket(requestData: ContactSupportRequestData) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.createNewTicketDetails(authToken, requestData)) {
                    is ApiResult.Error -> {
                        _state.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _state.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _state.value = UiState.Error(0, e.message.toString())
            }

        }
    }
}