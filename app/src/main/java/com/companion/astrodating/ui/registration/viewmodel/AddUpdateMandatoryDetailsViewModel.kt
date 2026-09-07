package com.companion.astrodating.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.registration.data.requestData.AddUpdateMandatoryDetailsRequestData
import com.companion.astrodating.ui.registration.domain.model.AddUpdateMandatoryDetailsDomain
import com.companion.astrodating.ui.registration.domain.repository.IAddUpdateMandatoryDetailsRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateMandatoryDetailsViewModel @Inject constructor(
    private val repository: IAddUpdateMandatoryDetailsRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<AddUpdateMandatoryDetailsDomain>> =
        MutableLiveData<UiState<AddUpdateMandatoryDetailsDomain>>()
    val state: LiveData<UiState<AddUpdateMandatoryDetailsDomain>> = _state

    fun addUpdateMandatoryDetails(
        token: String,
        fullName: String,
        gender: String,
        birthDate: String,
        age: Int,
        timeOfBirth: String,
        cityOfBirth: String?,
        latitudeOfCityOfBirth: String,
        longitudeOfCityOfBirth: String,
        community: String,
        currentCity: String?,
        latitudeOfCurrentCity: String,
        longitudeOfCurrentCity: String
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.addUpdateMandatoryDetails(
                    token,
                    AddUpdateMandatoryDetailsRequestData(
                        age = age,
                        birthDate = birthDate,
                        cityOfBirth = cityOfBirth!!,
                        community = community,
                        currentCity = currentCity!!,
                        fullName = fullName,
                        gender = gender,
                        latitudeOfCityOfBirth = latitudeOfCityOfBirth,
                        latitudeOfCurrentCity = latitudeOfCurrentCity,
                        longitudeOfCityOfBirth = longitudeOfCityOfBirth,
                        longitudeOfCurrentCity = longitudeOfCurrentCity,
                        timeOfBirth = timeOfBirth

                    )
                )) {
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