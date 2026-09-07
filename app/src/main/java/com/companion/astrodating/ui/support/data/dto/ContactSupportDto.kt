package com.companion.astrodating.ui.support.data.dto

data class ContactSupportDto(
    val `data`: ContactSupportDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class ContactSupportDtoData(
    val newTicket: ContactSupportNewTicketDtoEntity? = null
)

data class ContactSupportNewTicketDtoEntity(
    val __v: Int? = null,
    val _id: String? = null,
    val attachment: String? = null,
    val createdAt: String? = null,
    val description: String? = null,
    val problemType: String? = null,
    val status: String? = null,
    val ticketId: String? = null,
    val timeLineActions: List<Any?>? = null,
    val updatedAt: String? = null,
    val user: String? = null,
    val userType: String? = null
)
