package com.companion.astrodating.ui.onlineusers.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomain
import com.companion.astrodating.ui.onlineusers.domain.repository.IGetOnlineUsersRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineUsersViewModel @Inject constructor(
    private val repository: IGetOnlineUsersRepository
): ViewModel() {

    private lateinit var authToken: String
    private var _onlineUsersList: MutableLiveData<UiState<GetOnlineUsersDomain>> =
        MutableLiveData<UiState<GetOnlineUsersDomain>>()
    val onlineUsersList: LiveData<UiState<GetOnlineUsersDomain>> = _onlineUsersList

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun getOnlineUsers(
        pageNumber: Int,
        perPage: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _onlineUsersList.value = UiState.Loading
            try {
                Log.d("OnlineUsersVM", "Calling repository...")

                val result = repository.GetOnlineUsersDetails(
                    authToken,
                    pageNumber,
                    perPage,
                    latitude,
                    longitude
                )

                when (result) {
                    is ApiResult.Error -> {
                        Log.e("OnlineUsersVM", "❌ Error: ${result.errorCode} - ${result.errorMessage}")
                        _onlineUsersList.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        Log.d("OnlineUsersVM", "✅ Success! Users fetched: ${result.data.users.size}")
                        _onlineUsersList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                Log.e("OnlineUsersVM", "💥 Exception caught: ${e.message}", e)
                _onlineUsersList.value = UiState.Error(0, e.message ?: "Unknown Error")
            }
        }

    }
}