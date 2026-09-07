package com.companion.astrodating.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.profile.domain.model.LogoutDomain
import com.companion.astrodating.ui.profile.domain.repository.IProfileRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: IProfileRepository
): ViewModel() {

    private lateinit var authToken: String
    private var _logoutUser: MutableLiveData<UiState<LogoutDomain>> = MutableLiveData()
    val logoutUser: LiveData<UiState<LogoutDomain>> = _logoutUser

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _logoutUser.value = UiState.Loading
            when (val result = profileRepo.logoutUser(authToken)) {
                is ApiResult.Error -> {
                    _logoutUser.value = UiState.Error(result.errorCode,result.errorMessage)
                }
                is ApiResult.Success -> {
                    _logoutUser.value = UiState.Success(result.data)
                }
            }
        }
    }

}