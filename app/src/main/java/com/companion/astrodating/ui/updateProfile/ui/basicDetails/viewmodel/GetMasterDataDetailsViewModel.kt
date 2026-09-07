package com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetMasterDomainDetails
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetMasterDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetMasterDataDetailsViewModel @Inject constructor(
    private val repository: IGetMasterDetailsRepository
) : ViewModel() {

    private var _getMasterDataDetailsState: MutableLiveData<UiState<GetMasterDomainDetails>> =
        MutableLiveData<UiState<GetMasterDomainDetails>>()
    val getMasterDataDetailsState: LiveData<UiState<GetMasterDomainDetails>> = _getMasterDataDetailsState


    fun getMasterDataDetails(token: String) {
        viewModelScope.launch {
            _getMasterDataDetailsState.value = UiState.Loading
            try {
                when (val result = repository.getMasterDataDetails(token)) {
                    is ApiResult.Error -> {
                        _getMasterDataDetailsState.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _getMasterDataDetailsState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _getMasterDataDetailsState.value = UiState.Error(0, e.message.toString())
            }

        }
    }


}