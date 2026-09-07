package com.companion.astrodating.ui.otp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.data.requestData.VerifyOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.VerifyOtpDomain
import com.companion.astrodating.ui.otp.domain.repository.IOtpRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val repository: IOtpRepository
) : ViewModel() {

    private var _verifyOtp: MutableLiveData<UiState<VerifyOtpDomain>> =
        MutableLiveData<UiState<VerifyOtpDomain>>()
    val verifyOtp: LiveData<UiState<VerifyOtpDomain>> = _verifyOtp

    fun verifyOtp(otp: String, requestData: RequestOtpRequestData, deviceToken: String?, commonAuth: String) {
        viewModelScope.launch {
            _verifyOtp.value = UiState.Loading
            when (val result = repository.verifyOtp(
                VerifyOtpRequestData(
                    countryCode = requestData.countryCode,
                    deviceToken = deviceToken ?: APP_EMPTY_STRING,
                    email = requestData.email,
                    mobile = requestData.mobile,
                    otp = otp,
                    type = requestData.type
                ),
                commonAuth = commonAuth
            )) {
                is ApiResult.Error -> {
                    _verifyOtp.value = UiState.Error(result.errorCode, result.errorMessage)
                }

                is ApiResult.Success -> {
                    _verifyOtp.value = UiState.Success(result.data)
                }
            }
        }
    }
}
