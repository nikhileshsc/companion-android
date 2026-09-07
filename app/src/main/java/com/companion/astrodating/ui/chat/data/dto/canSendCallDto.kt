package com.companion.astrodating.ui.chat.data.dto


data class canSendCallDto(
    val `data`: Data? = null,
    val message: String? = null,
    val statusCode: Int? = null,
    val error: String? = null
){
    data class Data(
        val allowed: Boolean? = null,
        val message: String? = null
    )
}