package com.companion.astrodating.ui.message.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.message.data.repository.IGetAgoraTokenDetailsRepository
import com.companion.astrodating.ui.message.domain.model.GetAgoraTokenDomainDetails
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAgoraTokenDetailsViewModel @Inject constructor(
    private val repository: IGetAgoraTokenDetailsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<GetAgoraTokenDomainDetails>> =
        MutableLiveData<UiState<GetAgoraTokenDomainDetails>>()
    val state: LiveData<UiState<GetAgoraTokenDomainDetails>> = _state

    fun getAgoraTokenDetails(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getAgoraTokenDetails(token)) {
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