package com.companion.astrodating.ui.otp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.domain.repository.IOtpRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestOtpViewModel @Inject constructor(
    private val repository: IOtpRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<OtpDomain>> = MutableLiveData<UiState<OtpDomain>>()
    val state: LiveData<UiState<OtpDomain>> = _state

    fun requestOtpFor(
        requestData: RequestOtpRequestData,
        commonAuth: String
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.requestOtp(
                    requestData,
                    commonAuth = commonAuth
                )) {
                    is ApiResult.Error -> {
                        _state.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _state.value = UiState.Success(result.data)
                    }
                }
            } catch (t: Throwable) {
                _state.value = UiState.Error(0, t.message.toString())
            }

        }
    }
}