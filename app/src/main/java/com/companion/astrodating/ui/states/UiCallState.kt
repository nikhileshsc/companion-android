package com.companion.astrodating.ui.states

sealed class UiCallState<out T> {
    data class Success<T>(val data: T) : UiCallState<T>()
    data class Error(val errorCode: Int, val error: String) : UiCallState<Nothing>()
}
