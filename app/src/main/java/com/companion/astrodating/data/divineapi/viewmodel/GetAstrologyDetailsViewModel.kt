package com.companion.astrodating.data.divineapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.AstrologyDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetAstrologyDetailsRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAstrologyDetailsViewModel @Inject constructor(
    private val repository: IGetAstrologyDetailsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<AstrologyDomainDetails>> =
        MutableLiveData<UiState<AstrologyDomainDetails>>()
    val state: LiveData<UiState<AstrologyDomainDetails>> = _state

    fun getAstrologyDetails(token: String, requestData: AshtakootMilanRequestData) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getAstrologyDetails(token, requestData)) {
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