package com.companion.astrodating.ui.chat.data.dto


data class messageLimitDto(
    val `data`: Data? = null,
    val message: String? = null,
    val statusCode: Int? = null
){
    data class Data(
        val allowed: Boolean? = null,
        val message: String? = null
    )
}