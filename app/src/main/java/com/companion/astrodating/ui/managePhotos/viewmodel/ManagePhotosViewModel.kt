package com.companion.astrodating.ui.managePhotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.managePhotos.data.requestData.DeletePhotoRequestData
import com.companion.astrodating.ui.managePhotos.data.requestData.SetProfilePhotoRequestData
import com.companion.astrodating.ui.managePhotos.domain.model.DeletePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotosUserDomain
import com.companion.astrodating.ui.managePhotos.domain.model.SetProfilePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.repository.IManagePhotosRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ManagePhotosViewModel @Inject constructor(
    private val repository: IManagePhotosRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _state: MutableLiveData<UiState<GetMyPhotosUserDomain>> =
        MutableLiveData<UiState<GetMyPhotosUserDomain>>()
    val state: LiveData<UiState<GetMyPhotosUserDomain>> = _state

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

    fun getMyPhotos() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                when (val result =
                    repository.getMyPhotos(authToken)) {
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

    fun setProfilePhoto(galleryId:String) {
        viewModelScope.launch {
            _setProfileState.value = UiState.Loading
            try {
                when (val result =
                    repository.setProfilePhoto(authToken, SetProfilePhotoRequestData(galleryId = galleryId))) {
                    is ApiResult.Error -> {
                        _setProfileState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _setProfileState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _setProfileState.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun deletePhoto(galleryId:String) {
        viewModelScope.launch {
            _deletePhotoState.value = UiState.Loading
            try {
                when (val result =
                    repository.deletePhoto(authToken, DeletePhotoRequestData(galleryId = galleryId))) {
                    is ApiResult.Error -> {
                        _deletePhotoState.value = UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _deletePhotoState.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _deletePhotoState.value = UiState.Error(0, e.message.toString())
            }

        }
    }


    fun uploadPhoto(fileType: String, part: MultipartBody.Part) {
        viewModelScope.launch {
            _uploadPhotoState.value = UiState.Loading
            try {
                when (val result =
                    repository.uploadPhoto(authToken, fileType,part)) {
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