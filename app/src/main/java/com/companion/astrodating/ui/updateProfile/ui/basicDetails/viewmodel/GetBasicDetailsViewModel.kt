package com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.requestData.UpdateBasicDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetBasicDetailsDomain
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetBasicDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetBasicDetailsViewModel @Inject constructor(
    private val repository: IGetBasicDetailsRepository
) : ViewModel() {

    private var _getBasicDetailsState: MutableLiveData<UiState<GetBasicDetailsDomain>> =
        MutableLiveData<UiState<GetBasicDetailsDomain>>()
    val getBasicDetailsState: LiveData<UiState<GetBasicDetailsDomain>> = _getBasicDetailsState

    private var _updateBasicDetailsState: MutableLiveData<UiState<GetBasicDetailsDomain>> =
        MutableLiveData<UiState<GetBasicDetailsDomain>>()
    val updateBasicDetailsState: LiveData<UiState<GetBasicDetailsDomain>> = _updateBasicDetailsState

    fun getBasicDetails(token: String) {
        viewModelScope.launch {
            _getBasicDetailsState.value = UiState.Loading
            try {
                when (val result = repository.getBasicDetails(token)) {
                    is ApiResult.Error -> {
                        _getBasicDetailsState.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _getBasicDetailsState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _getBasicDetailsState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun updateBasicDetails(
        token: String,
        maritalStatus: String,
        lookingFor: String,
        height: Double,
        religion: String,
        community: String,
        currentCity: String,
        profession: String,
        education: String
    ) {
        viewModelScope.launch {
            _updateBasicDetailsState.value = UiState.Loading
            try {
                when (val result = repository.updateBasicDetails(
                    token,
                    UpdateBasicDetailsRequestData(
                        status = maritalStatus,
                        lookingFor = lookingFor,
                        height = height,
                        religion = religion,
                        community = community,
                        currentCity = currentCity,
                        profession = profession,
                        education = education
                    )
                )) {
                    is ApiResult.Error -> {
                        _updateBasicDetailsState.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _updateBasicDetailsState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _updateBasicDetailsState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

}