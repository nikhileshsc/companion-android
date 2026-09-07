package com.companion.astrodating.ui.states

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val errorCode: Int, val error: String) : UiState<Nothing>()
}

sealed class MessageUiState<out T> {
    object Idle: MessageUiState<Nothing>()
    object Loading : MessageUiState<Nothing>()
    data class Success<T>(val data: T) : MessageUiState<T>()
    data class Error(val errorCode: Int, val error: String) : MessageUiState<Nothing>()
}

sealed class HomeMessageUiState<out T> {
    object Idle: HomeMessageUiState<Nothing>()
    object Loading : HomeMessageUiState<Nothing>()
    data class Success<T>(val data: T) : HomeMessageUiState<T>()
    data class Error(val errorCode: Int, val error: String) : HomeMessageUiState<Nothing>()
}