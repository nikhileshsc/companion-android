package com.companion.astrodating.ui.profileDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsDomain
import com.companion.astrodating.ui.profileDetails.domain.repository.IGetUserDetailsRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetUserDetailsViewModel @Inject constructor(
    private val repository: IGetUserDetailsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<GetUserDetailsDomain>> =
        MutableLiveData<UiState<GetUserDetailsDomain>>()
    val state: LiveData<UiState<GetUserDetailsDomain>> = _state

    fun getUserDetails(token: String, userId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getHomeUserDetails(token, userId)) {
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