package com.companion.astrodating.ui.otp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.otp.domain.model.CountryDomain
import com.companion.astrodating.ui.otp.domain.repository.ICountryRepository
import com.companion.astrodating.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val repository: ICountryRepository
) : ViewModel() {

    private var _state: MutableLiveData<UiState<CountryDomain>> =
        MutableLiveData<UiState<CountryDomain>>()
    val state: LiveData<UiState<CountryDomain>> = _state

    fun getCountryList(commonAuth: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result = repository.getAllCountryList(commonAuth)) {
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