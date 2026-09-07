package com.companion.astrodating.ui.support.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.support.domain.model.ContactSupportDomainDetails
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData

interface IGetContactSupportRepository {

    suspend fun createNewTicketDetails(
        token: String,
        requestData: ContactSupportRequestData
    ): ApiResult<ContactSupportDomainDetails>
}