package com.companion.astrodating.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.home.domain.repository.IGetHomeUserRepository
import com.companion.astrodating.ui.home.domain.repository.IUpdateLocationRepository
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.companion.astrodating.ui.home.data.requestData.updateOnlineStatusRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IUpdateBenefitsRepository
import com.companion.astrodating.ui.states.HomeMessageUiState
import com.companion.astrodating.ui.states.MessageUiState
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val repository: IGetHomeUserRepository,
    private val repositoryLocation: IUpdateLocationRepository,
    private val repositoryBenefit: IUpdateBenefitsRepository
) : ViewModel() {

    private lateinit var authToken: String
    private var _homeUserList: MutableLiveData<UiState<GetHomeUserDomain>> =
        MutableLiveData<UiState<GetHomeUserDomain>>()
    val homeUserList: LiveData<UiState<GetHomeUserDomain>> = _homeUserList

    private var _homeMoreUserList: MutableLiveData<UiState<GetHomeUserDomain>> =
        MutableLiveData<UiState<GetHomeUserDomain>>()
    val homeMoreUserList: LiveData<UiState<GetHomeUserDomain>> = _homeMoreUserList


    private var _state: MutableLiveData<HomeMessageUiState<UpdateBenefitsDomainDetails>> =
        MutableLiveData<HomeMessageUiState<UpdateBenefitsDomainDetails>>()
    val state: LiveData<HomeMessageUiState<UpdateBenefitsDomainDetails>> = _state

    init {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    fun updateBenefitByType(token: String,requestData: UpdateBenefitRequestData) {
        viewModelScope.launch {
            _state.value = HomeMessageUiState.Loading
            try {
                when (val result = repositoryBenefit.updateBenefitsDetails(token,requestData)) {
                    is ApiResult.Error -> {
                        _state.value = HomeMessageUiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        print("!!!!Result code: ")
                        print(result.data.statusCode)
                        _state.value = HomeMessageUiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _state.value = HomeMessageUiState.Error(0, e.message.toString())
            }

        }
    }

    fun resetState() {
        _state.value = HomeMessageUiState.Idle
    }

    fun getHomeUserDetails(
        searchText: String,
        minHeight: Double,
        maxHeight: Double,
        minAge: Int,
        maxAge: Int,
        education: ArrayList<String>,
        profession: ArrayList<String>,
        religion: ArrayList<String>,
        country: ArrayList<String>,
        city: ArrayList<String>,
        status: ArrayList<String>,
        community: ArrayList<String>,
        pageNumber: Int
    ) {
        viewModelScope.launch {
            _homeUserList.value = UiState.Loading
            try {
                when (val result = repository.getHomeUserDetails(
                    authToken,
                    searchText,
                    minHeight,
                    maxHeight,
                    minAge,
                    maxAge,
                    education,
                    profession,
                    religion,
                    country,
                    city,
                    status,
                    community,
                    pageNumber,
                    PER_PAGE_COUNT
                )) {
                    is ApiResult.Error -> {
                        _homeUserList.value = UiState.Error(result.errorCode, result.errorMessage)
                    }
                    is ApiResult.Success -> {
                        _homeUserList.value = UiState.Success(result.data)
                    }
                }
            } catch (e: Exception) {
                _homeUserList.value = UiState.Error(0, e.message.toString())
            }
        }
    }


    fun getMoreHomeUserDetails(searchText: String,
                               minHeight: Double,
                               maxHeight: Double,
                               minAge: Int,
                               maxAge: Int,
                               education: ArrayList<String>,
                               profession: ArrayList<String>,
                               religion: ArrayList<String>,
                               country: ArrayList<String>,
                               city: ArrayList<String>,
                               status: ArrayList<String>,
                               community: ArrayList<String>,
                               pageNumber: Int) {
        viewModelScope.launch {
            _homeMoreUserList.value = UiState.Loading
            try {
                when (val result = repository.getHomeUserDetails(
                    authToken,
                    searchText,
                    minHeight,
                    maxHeight,
                    minAge,
                    maxAge,
                    education,
                    profession,
                    religion,
                    country,
                    city,
                    status,
                    community,
                    pageNumber,
                    PER_PAGE_COUNT
                )) {
                    is ApiResult.Error -> {
                        _homeMoreUserList.value =
                            UiState.Error(result.errorCode, result.errorMessage)
                    }

                    is ApiResult.Success -> {
                        _homeMoreUserList.value = UiState.Success(result.data)
                    }
                }

            } catch (e: Exception) {
                _homeMoreUserList.value = UiState.Error(0, e.message.toString())
            }

        }
    }

    fun updateLocation(
//        latitude: Double,
//        longitude: Double
        requestData: updateLocationRequestData
    ){
        viewModelScope.launch {
//            _updateLocationState.value = UiState.Loading
            Log.d(TAG, "updateLocation request: latitude=${requestData.latitude}, longitude=${requestData.longitude}")
            try {
                when (val result = repositoryLocation.getUpdateLocationDetails(
                    authToken,
//                    updateLocationRequestData(latitude,longitude)
                    requestData
                )){
                    is ApiResult.Error -> {
                        Log.d(TAG, "xxxxxxxData: "+ result.errorMessage)
                    }
                    is ApiResult.Success -> {
                        Log.d(TAG, "xxxxxxxData: "+ result.data)
                    }
                }

            }catch (e: Exception){
                Log.d(TAG,"Errorxxxxxx: ${e.message.toString()}")
            }}
    }

    fun updateOnlineStatus(
        isOnline: Boolean
    ){
        viewModelScope.launch {
//            _updateOnlineStatusState.value = UiState.Loading
            try {
                when (val result = repository.getOnlineStatusDetails(
                    authToken,
                    updateOnlineStatusRequestData(isOnline)
                )){
                    is ApiResult.Error -> {
                        Log.d(TAG,"xxx: "+result.errorMessage)
                    }
                    is ApiResult.Success -> {
                        Log.d(TAG,"!!!!!!!!!!!")
                        print(result.data)
                    }
                }
            }catch (e: Exception){
                Log.d(TAG,"Error: xx${e.message.toString()}")
            }}
    }



    companion object {
        const val PER_PAGE_COUNT = 50
    }

}