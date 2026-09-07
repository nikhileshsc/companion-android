package com.companion.astrodating.ui.support.domain.model

data class ContactSupportDomainDetails(
    val newTicket: ContactSupportNewTicketDomainEntity
)

data class ContactSupportNewTicketDomainEntity(
    val __v: Int,
    val id: String,
    val attachment: String,
    val createdAt: String,
    val description: String,
    val problemType: String,
    val status: String,
    val ticketId: String,
    val timeLineActions: List<String?>,
    val updatedAt: String,
    val user: String,
    val userType: String
)
