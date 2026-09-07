package com.companion.astrodating.ui.support.data.mapper

import com.companion.astrodating.ui.support.data.dto.ContactSupportDto
import com.companion.astrodating.ui.support.data.dto.ContactSupportNewTicketDtoEntity
import com.companion.astrodating.ui.support.domain.model.ContactSupportDomainDetails
import com.companion.astrodating.ui.support.domain.model.ContactSupportNewTicketDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetContactSupportMapper @Inject constructor() {

    fun mapToDomainModel(dto: ContactSupportDto): ContactSupportDomainDetails {
        return ContactSupportDomainDetails(
            newTicket =mapToNewTicketDomainModel(dto.data?.newTicket!!)
        )
    }

    fun mapToNewTicketDomainModel(dto: ContactSupportNewTicketDtoEntity): ContactSupportNewTicketDomainEntity {
        return ContactSupportNewTicketDomainEntity(
            __v = dto.__v?: 0,
            id = dto._id?: APP_EMPTY_STRING,
            attachment = dto.attachment?: APP_EMPTY_STRING,
            createdAt = dto.createdAt?: APP_EMPTY_STRING,
            description = dto.description?: APP_EMPTY_STRING,
            problemType = dto.problemType?: APP_EMPTY_STRING,
            status = dto.status?: APP_EMPTY_STRING,
            ticketId = dto.ticketId?: APP_EMPTY_STRING,
            timeLineActions = emptyList(),
            updatedAt = dto.updatedAt?: APP_EMPTY_STRING,
            user = dto.user?: APP_EMPTY_STRING,
            userType = dto.userType?: APP_EMPTY_STRING
        )
    }


}