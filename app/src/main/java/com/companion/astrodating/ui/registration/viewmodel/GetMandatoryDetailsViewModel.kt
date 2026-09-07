package com.companion.astrodating.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryDetailsDomain
import com.companion.astrodating.ui.registration.domain.repository.IGetMandatoryDetailsRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetMandatoryDetailsViewModel @Inject constructor(
    private val repository: IGetMandatoryDetailsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<GetMandatoryDetailsDomain>> =
        MutableLiveData<UiState<GetMandatoryDetailsDomain>>()
    val state: LiveData<UiState<GetMandatoryDetailsDomain>> = _state

    fun getMandatoryDetails(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getMandatoryDetails(token)) {
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