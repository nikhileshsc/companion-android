package com.companion.astrodating.data.divineapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.MatchingPlanetaryPositionDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetMatchingPlanetaryRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetMatchingPlanetaryDetailsViewModel @Inject constructor(
    private val repository: IGetMatchingPlanetaryRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<MatchingPlanetaryPositionDomainDetails>> =
        MutableLiveData<UiState<MatchingPlanetaryPositionDomainDetails>>()
    val state: LiveData<UiState<MatchingPlanetaryPositionDomainDetails>> = _state

    fun getMatchingPlanetaryDetails(token: String, requestData: AshtakootMilanRequestData) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getMatchingPlanetaryDetails(token, requestData)) {
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