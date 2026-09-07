package com.companion.astrodating.ui.topMatches.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.topMatches.domain.repository.IGetTopUserRepository
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopUserViewModel @Inject constructor(
    private val repository: IGetTopUserRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _topUserList: MutableLiveData<UiState<GetHomeUserDomain>> =
        MutableLiveData<UiState<GetHomeUserDomain>>()
    val topUserList: LiveData<UiState<GetHomeUserDomain>> = _topUserList

    private var _topMoreUserList: MutableLiveData<UiState<GetHomeUserDomain>> =
        MutableLiveData<UiState<GetHomeUserDomain>>()
    val topMoreUserList: LiveData<UiState<GetHomeUserDomain>> = _topMoreUserList

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun getTopUserDetails(pageNumber: Int) {
        viewModelScope.launch {
            _topUserList.value = UiState.Loading
            try {
                when (val result =
                    repository.getTopUserDetails(authToken, pageNumber, PER_PAGE_COUNT)) {
                    is ApiResult.Error -> {
                        _topUserList.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _topUserList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _topUserList.value = UiState.Error(0, e.message.toString())
            }

        }
    }


    fun getMoreTopUserDetails(pageNumber: Int) {
        viewModelScope.launch {
            _topMoreUserList.value = UiState.Loading
            try {
                when (val result =
                    repository.getTopUserDetails(authToken, pageNumber, PER_PAGE_COUNT)) {
                    is ApiResult.Error -> {
                        _topMoreUserList.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _topMoreUserList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _topMoreUserList.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    companion object {
        const val PER_PAGE_COUNT = 50
    }

}