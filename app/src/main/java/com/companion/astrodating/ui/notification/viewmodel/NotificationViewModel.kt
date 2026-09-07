package com.companion.astrodating.ui.notification.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainDetails
import com.companion.astrodating.ui.notification.domain.repository.IGetNotificationRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: IGetNotificationRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _notificationList: MutableLiveData<UiState<NotificationDomainDetails>> =
        MutableLiveData<UiState<NotificationDomainDetails>>()
    val notificationList: LiveData<UiState<NotificationDomainDetails>> = _notificationList

    private var _moreNotificationList: MutableLiveData<UiState<NotificationDomainDetails>> =
        MutableLiveData<UiState<NotificationDomainDetails>>()
    val moreNotificationList: LiveData<UiState<NotificationDomainDetails>> = _moreNotificationList

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun getNotificationDetails(pageNumber: Int) {
        viewModelScope.launch {
            _notificationList.value = UiState.Loading
            try {
                when (val result =
                    repository.getNotificationDetails(authToken, pageNumber, PER_PAGE_COUNT)) {
                    is ApiResult.Error -> {
                        _notificationList.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _notificationList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _notificationList.value = UiState.Error(0, e.message.toString())
            }

        }
    }


    fun getMoreNotificationDetails(pageNumber: Int) {
        viewModelScope.launch {
            _moreNotificationList.value = UiState.Loading
            try {
                when (val result =
                    repository.getNotificationDetails(authToken, pageNumber, PER_PAGE_COUNT)) {
                    is ApiResult.Error -> {
                        _moreNotificationList.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _moreNotificationList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _moreNotificationList.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    companion object {
        const val PER_PAGE_COUNT = 50
    }

}