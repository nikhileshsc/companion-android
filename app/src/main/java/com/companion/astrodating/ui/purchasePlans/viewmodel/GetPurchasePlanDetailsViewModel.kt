package com.companion.astrodating.ui.purchasePlans.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.purchasePlans.data.requestData.CreateInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.CreateInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.GetPlanDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.VerifyInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IGetPurchasePlansRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPurchasePlanDetailsViewModel @Inject constructor(
    private val repository: IGetPurchasePlansRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<GetPlanDomainDetails>> =
        MutableLiveData<UiState<GetPlanDomainDetails>>()
    val state: LiveData<UiState<GetPlanDomainDetails>> = _state

    private var _createInAppSubscriptionState: MutableLiveData<UiState<CreateInAppSubscriptionDomainDetails>> =
        MutableLiveData<UiState<CreateInAppSubscriptionDomainDetails>>()
    val createInAppSubscriptionState: LiveData<UiState<CreateInAppSubscriptionDomainDetails>> = _createInAppSubscriptionState

    private var _verifyInAppSubscriptionState: MutableLiveData<UiState<VerifyInAppSubscriptionDomainDetails>> =
        MutableLiveData<UiState<VerifyInAppSubscriptionDomainDetails>>()
    val verifyInAppSubscriptionState: LiveData<UiState<VerifyInAppSubscriptionDomainDetails>> = _verifyInAppSubscriptionState
    fun getPurchasePlanDetails(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
//            try {
                when (val result = repository.getPlanDetails(token)) {
                    is ApiResult.Error -> {
                        _state.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _state.value = UiState.Success(result.data)
                    }
                }

//            } catch (e: Exception) {
//                _state.value = UiState.Error(0, e.message.toString())
//            }

        }
    }

    fun createInAppSubscriptionDetails(token: String,requestData: CreateInAppSubscriptionRequestData) {
        viewModelScope.launch {
            _createInAppSubscriptionState.value = UiState.Loading
            try {
                when (val result = repository.createInAppSubscriptionDetails(token,requestData)) {
                    is ApiResult.Error -> {
                        _createInAppSubscriptionState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _createInAppSubscriptionState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _createInAppSubscriptionState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun verifyInAppSubscriptionDetails(token: String,requestData: VerifyInAppSubscriptionRequestData) {
        viewModelScope.launch {
            _verifyInAppSubscriptionState.value = UiState.Loading
            try {
                when (val result = repository.verifyInAppSubscriptionDetails(token,requestData)) {
                    is ApiResult.Error -> {
                        _verifyInAppSubscriptionState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _verifyInAppSubscriptionState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _verifyInAppSubscriptionState.value = UiState.Error(0, e.message.toString())
            }

        }
    }
}