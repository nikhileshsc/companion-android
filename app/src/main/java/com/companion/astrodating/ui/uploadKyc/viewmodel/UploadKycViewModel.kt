package com.companion.astrodating.ui.uploadKyc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.managePhotos.domain.model.DeletePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.SetProfilePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.uploadKyc.domain.model.GetKycDomain
import com.companion.astrodating.ui.uploadKyc.domain.repository.IUploadKycRepository
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UploadKycViewModel @Inject constructor(
    private val repository: IUploadKycRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _state: MutableLiveData<UiState<GetKycDomain>> =
        MutableLiveData<UiState<GetKycDomain>>()
    val state: LiveData<UiState<GetKycDomain>> = _state

    private var _setProfileState: MutableLiveData<UiState<SetProfilePhotoDomain>> =
        MutableLiveData<UiState<SetProfilePhotoDomain>>()
    val setProfileState: LiveData<UiState<SetProfilePhotoDomain>> = _setProfileState

    private var _deletePhotoState: MutableLiveData<UiState<DeletePhotoDomain>> =
        MutableLiveData<UiState<DeletePhotoDomain>>()
    val deletePhotoState: LiveData<UiState<DeletePhotoDomain>> = _deletePhotoState

    private var _uploadPhotoState: MutableLiveData<UiState<UploadPhotoDomain>> =
        MutableLiveData<UiState<UploadPhotoDomain>>()
    val uploadPhotoState: LiveData<UiState<UploadPhotoDomain>> = _uploadPhotoState

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun getKyc() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result =
                    repository.getKyc(authToken)) {
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

    fun uploadDocument(fileType: String, part: MultipartBody.Part) {
        viewModelScope.launch {
            _uploadPhotoState.value = UiState.Loading
            try {
                when (val result =
                    repository.uploadKycDocument(authToken, fileType,part)) {
                    is ApiResult.Error -> {
                        _uploadPhotoState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _uploadPhotoState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _uploadPhotoState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

}