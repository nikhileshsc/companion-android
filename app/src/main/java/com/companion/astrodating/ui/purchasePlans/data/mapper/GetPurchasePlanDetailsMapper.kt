package com.companion.astrodating.ui.purchasePlans.data.mapper

import com.companion.astrodating.ui.purchasePlans.data.dto.AndroidInAppPurchaseResDtoEntity
import com.companion.astrodating.ui.purchasePlans.data.dto.BenefitDtoEntity
import com.companion.astrodating.ui.purchasePlans.data.dto.CreateInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.dto.CurrentSubscriptionDtoEntity
import com.companion.astrodating.ui.purchasePlans.data.dto.GetPlanDtoDetails
import com.companion.astrodating.ui.purchasePlans.data.dto.PlanDtoEntity
import com.companion.astrodating.ui.purchasePlans.data.dto.SubscriptionDtoEntity
import com.companion.astrodating.ui.purchasePlans.data.dto.VerifyInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.domain.model.AndroidInAppPurchaseResDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.BenefitDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.CreateInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.CurrentSubscriptionDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.GetPlanDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.PlanDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.SubscriptionDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.VerifyInAppSubscriptionDomainDetails
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetPurchasePlanDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetPlanDtoDetails): GetPlanDomainDetails {
        return GetPlanDomainDetails(
            currentSubcription =mapToCurrentSubscriptionDomainModel(dto.data?.currentSubcription ?: CurrentSubscriptionDtoEntity()),
            plans = toPlanDomainList(dto.data?.plans ?: emptyList())

        )
    }

    fun mapToCurrentSubscriptionDomainModel(dto: CurrentSubscriptionDtoEntity): CurrentSubscriptionDomainEntity {
        return CurrentSubscriptionDomainEntity(
            id = dto._id?: APP_EMPTY_STRING,
            androidInAppPurchaseRes = mapToAndroidInAppPurchaseResDomainModel(dto.androidInAppPurchaseRes?:AndroidInAppPurchaseResDtoEntity()),
            planName = dto.planName?: APP_EMPTY_STRING,
            subscribedPlan = dto.subscribedPlan?: APP_EMPTY_STRING,
            subscriptionStatus = dto.subscriptionStatus?: APP_EMPTY_STRING,
        )
    }

    fun mapToAndroidInAppPurchaseResDomainModel(dto: AndroidInAppPurchaseResDtoEntity): AndroidInAppPurchaseResDomainEntity {
        return AndroidInAppPurchaseResDomainEntity(
            acknowledged = dto.acknowledged ?: false,
            autoRenewing  = dto.autoRenewing ?: false,
            orderId  = dto.orderId ?: APP_EMPTY_STRING,
            packageName  = dto.packageName ?: APP_EMPTY_STRING,
            productId  = dto.productId ?: APP_EMPTY_STRING,
            purchaseState  = dto.purchaseState ?: 0,
            purchaseTime  = dto.purchaseTime ?: 0,
            purchaseToken  = dto.purchaseToken ?: APP_EMPTY_STRING,
            quantity  = dto.quantity ?: 0


            )
    }
    private fun toPlanDomainList(list: List<PlanDtoEntity>): List<PlanDomainEntity> {
        return list.map {
            PlanDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                androidGroupId = it.androidGroupId ?: APP_EMPTY_STRING,
                androidSubscriptionId = it.androidSubscriptionId ?: APP_EMPTY_STRING,
                benefits = toBenefitDomainList(it.benefits ?: emptyList()),
                benefitsDescriptions = it.benefitsDescriptions ?: emptyList(),
                createdAt = it.createdAt ?: APP_EMPTY_STRING,
                duration = it.duration ?: APP_EMPTY_STRING,
                iOsSubscriptionId = it.iOsSubscriptionId ?: APP_EMPTY_STRING,
                isActive = it.isActive ?: false,
                isMostPopular = it.isMostPopular ?: false,
                order = it.order ?: 0,
                planDescription = it.planDescription ?: APP_EMPTY_STRING,
                planName = it.planName ?: APP_EMPTY_STRING,
                price = it.price ?: 0,
                status = it.status ?: APP_EMPTY_STRING,
                subscriptionStatus = it.subscriptionStatus ?: APP_EMPTY_STRING,
                type = it.type ?: APP_EMPTY_STRING,
                updatedAt = it.updatedAt ?: APP_EMPTY_STRING
            )
        }
    }

    private fun toBenefitDomainList(list: List<BenefitDtoEntity>): List<BenefitDomainEntity> {
        return list.map {
            BenefitDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                count = it.count ?: 0,
                title = it.title ?: APP_EMPTY_STRING,
                updateKey = it.updateKey ?: APP_EMPTY_STRING
            )
        }
    }

    fun mapToCreateInAppSubscriptionDomainModel(dto: CreateInAppSubscriptionDto): CreateInAppSubscriptionDomainDetails {
        return CreateInAppSubscriptionDomainDetails(
            subscription =mapToSubscriptionDomainModel(dto.data?.subscription!!),

            )
    }

    fun mapToSubscriptionDomainModel(dto: SubscriptionDtoEntity): SubscriptionDomainEntity {
        return SubscriptionDomainEntity(
            id = dto._id?: APP_EMPTY_STRING,
            subscribedPlan = dto.subscribedPlan?: APP_EMPTY_STRING,

            )
    }

    fun mapToVerifyInAppSubscriptionDomainModel(dto: VerifyInAppSubscriptionDto): VerifyInAppSubscriptionDomainDetails {
        return VerifyInAppSubscriptionDomainDetails(
            data = dto.data!!,
            statusCode = dto.statusCode ?: 0

            )
    }

}