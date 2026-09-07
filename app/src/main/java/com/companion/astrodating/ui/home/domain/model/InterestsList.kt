package com.companion.astrodating.ui.home.domain.model

import com.companion.astrodating.R
import com.companion.astrodating.util.InterestTypeConstant

object InterestsList {

    val interestsList = listOf(
        InterestDomain(
            image = R.drawable.ic_interest_sentinterest,
            title = R.string.text_interest_sentinterest,
            interestType = InterestTypeConstant.sendInterest
        ),
        InterestDomain(
            image = R.drawable.ic_interest_receivedinterest,
            title = R.string.text_interest_receivedinterest,
            interestType = InterestTypeConstant.receivedInterest
        ),
        InterestDomain(
            image = R.drawable.ic_interest_shortlist,
            title = R.string.text_interest_shortlisted,
            interestType = InterestTypeConstant.shortlist
        ),
        InterestDomain(
            image = R.drawable.ic_interest_blocked,
            title = R.string.text_interest_blockedlist,
            interestType = InterestTypeConstant.block
        ),
        InterestDomain(
            image = R.drawable.ic_interest_declined,
            title = R.string.text_interest_declinedlist,
            interestType = InterestTypeConstant.declineInterest
        )
    )
}