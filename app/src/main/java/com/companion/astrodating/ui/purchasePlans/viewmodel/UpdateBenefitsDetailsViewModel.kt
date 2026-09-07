package com.companion.astrodating.ui.purchasePlans.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IUpdateBenefitsRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBenefitsDetailsViewModel @Inject constructor(
    private val repository: IUpdateBenefitsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<UpdateBenefitsDomainDetails>> =
        MutableLiveData<UiState<UpdateBenefitsDomainDetails>>()
    val state: LiveData<UiState<UpdateBenefitsDomainDetails>> = _state


    fun updateBenefitByType(token: String,requestData: UpdateBenefitRequestData) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.updateBenefitsDetails(token,requestData)) {
                    is ApiResult.Error -> {
                        _state.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        print("!!!!Result code: ")
                        print(result.data.statusCode)
                        _state.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _state.value = UiState.Error(0, e.message.toString())
            }

        }
    }

}