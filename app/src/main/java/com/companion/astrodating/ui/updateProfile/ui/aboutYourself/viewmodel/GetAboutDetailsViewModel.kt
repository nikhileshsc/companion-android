package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.requestData.UpdateAboutDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.model.GetAboutDetailsDomain
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.repository.IGetAboutDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAboutDetailsViewModel @Inject constructor(
    private val repository: IGetAboutDetailsRepository
) : ViewModel() {

    private var _getAboutDetailsState: MutableLiveData<UiState<GetAboutDetailsDomain>> =
        MutableLiveData<UiState<GetAboutDetailsDomain>>()
    val getAboutDetailsState: LiveData<UiState<GetAboutDetailsDomain>> = _getAboutDetailsState

    private var _updateAboutDetailsState: MutableLiveData<UiState<GetAboutDetailsDomain>> =
        MutableLiveData<UiState<GetAboutDetailsDomain>>()
    val updateAboutDetailsState: LiveData<UiState<GetAboutDetailsDomain>> = _updateAboutDetailsState

    fun getAboutDetails(token: String) {
        viewModelScope.launch {
            _getAboutDetailsState.value = UiState.Loading
            try {
                when (val result = repository.getAboutDetails(token)) {
                    is ApiResult.Error -> {
                        _getAboutDetailsState.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _getAboutDetailsState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _getAboutDetailsState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun updateAboutDetails(
        token: String,
        aboutYourSelf: String,
        interest: String,
        expectations: String,
        needsUpdateProfile: Boolean
    ) {
        viewModelScope.launch {
            _updateAboutDetailsState.value = UiState.Loading
            try {
                when (val result = repository.updateAboutDetails(
                    token,
                    UpdateAboutDetailsRequestData(
                        aboutYourself = aboutYourSelf,
                        interest = interest,
                        expectations = expectations,
                        needsUpdateProfile = needsUpdateProfile
                    )
                )) {
                    is ApiResult.Error -> {
                        _updateAboutDetailsState.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _updateAboutDetailsState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _updateAboutDetailsState.value = UiState.Error(0, e.message.toString())
            }

        }
    }


}