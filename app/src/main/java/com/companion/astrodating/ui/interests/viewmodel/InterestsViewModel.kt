package com.companion.astrodating.ui.interests.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.domain.model.GetInterestsDomain
import com.companion.astrodating.ui.interests.domain.model.UpdateInterestDomain
import com.companion.astrodating.ui.interests.domain.repository.IInterestsRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val repository: IInterestsRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _updateInterestState: MutableLiveData<UiState<UpdateInterestDomain>> =
        MutableLiveData<UiState<UpdateInterestDomain>>()
    val updateInterestState: LiveData<UiState<UpdateInterestDomain>> = _updateInterestState

    private var _getInterestState: MutableLiveData<UiState<GetInterestsDomain>> =
        MutableLiveData<UiState<GetInterestsDomain>>()
    val getInterestState: LiveData<UiState<GetInterestsDomain>> = _getInterestState


    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun updateInterestDetails(requestData:InterestRequestData) {
        viewModelScope.launch {
            _updateInterestState.value = UiState.Loading
            try {
                when (val result =
                    repository.updateInterest(authToken,requestData)) {
                    is ApiResult.Error -> {
                        _updateInterestState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _updateInterestState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _updateInterestState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun getInterestByType(interestType:String) {
        viewModelScope.launch {
            _getInterestState.value = UiState.Loading
            try {
                when (val result =
                    repository.getInterestByType(authToken,interestType)) {
                    is ApiResult.Error -> {
                        _getInterestState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _getInterestState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _getInterestState.value = UiState.Error(0, e.message.toString())
            }

        }
    }



}