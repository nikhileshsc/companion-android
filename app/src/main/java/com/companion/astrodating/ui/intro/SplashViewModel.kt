package com.companion.astrodating.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _isTimeout: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isTimeout: LiveData<Boolean> = _isTimeout

    init {
        viewModelScope.launch {
            delay(2500)
            _isTimeout.value = true
        }
    }
}