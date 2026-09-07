package com.companion.astrodating.data.api

sealed class ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val errorCode: Int, val errorMessage: String): ApiResult<Nothing>()
}
