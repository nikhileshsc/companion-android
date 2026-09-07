package com.companion.astrodating.di

import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.mapper.GetAshtakootMilanMapper
import com.companion.astrodating.data.divineapi.data.mapper.GetAstrologyDetailsMapper
import com.companion.astrodating.data.divineapi.data.mapper.GetDashakootMilanMapper
import com.companion.astrodating.data.divineapi.data.mapper.GetManglikDoshaMapper
import com.companion.astrodating.data.divineapi.data.mapper.GetMatchingPlanetaryMapper
import com.companion.astrodating.data.divineapi.data.repository.GetAshtakootMilanDetailsRepoImpl
import com.companion.astrodating.data.divineapi.data.repository.GetAstrologyDetailsRepoImpl
import com.companion.astrodating.data.divineapi.data.repository.GetDashakootMilanDetailsRepoImpl
import com.companion.astrodating.data.divineapi.data.repository.GetManglikDoshaRepoImpl
import com.companion.astrodating.data.divineapi.data.repository.GetMatchingPlanetaryRepoImpl
import com.companion.astrodating.data.divineapi.domain.repository.IGetAshtakootMilanRepository
import com.companion.astrodating.data.divineapi.domain.repository.IGetAstrologyDetailsRepository
import com.companion.astrodating.data.divineapi.domain.repository.IGetDashakootMilanRepository
import com.companion.astrodating.data.divineapi.domain.repository.IGetManglikDoshaRepository
import com.companion.astrodating.data.divineapi.domain.repository.IGetMatchingPlanetaryRepository
import com.companion.astrodating.ui.call.data.mapper.RtcTokenMapper
import com.companion.astrodating.ui.call.data.repository.RtcTokenRepoImpl
import com.companion.astrodating.ui.call.domain.repository.IRtcTokenRepository
import com.companion.astrodating.ui.chat.data.mapper.canSendCallMapper
import com.companion.astrodating.ui.chat.data.mapper.freeMessageMapper
import com.companion.astrodating.ui.chat.data.mapper.sendMessagePushNotificationMapper
import com.companion.astrodating.ui.chat.data.repository.canSendCallRepoImpl
import com.companion.astrodating.ui.chat.data.repository.messageLimitRepoImpl
import com.companion.astrodating.ui.chat.data.repository.sendMessagePushNotificationRepoImpl
import com.companion.astrodating.ui.chat.domain.repository.canSendCallRepository
import com.companion.astrodating.ui.chat.domain.repository.freeMessageLimitRepository
import com.companion.astrodating.ui.chat.domain.repository.sendMesssagePushNotificationRepository
import com.companion.astrodating.ui.home.data.mapper.GetHomeUsersMapper
import com.companion.astrodating.ui.home.data.mapper.UpdateLocationMapper
import com.companion.astrodating.ui.home.data.mapper.UpdateOnlineStatusMapper
import com.companion.astrodating.ui.home.data.repository.GetHomeUserRepoImpl
import com.companion.astrodating.ui.home.data.repository.UpdateLocationRepositoryImpl
import com.companion.astrodating.ui.home.domain.repository.IGetHomeUserRepository
import com.companion.astrodating.ui.home.domain.repository.IUpdateLocationRepository
import com.companion.astrodating.ui.interests.data.mapper.InterestsMapper
import com.companion.astrodating.ui.interests.data.repository.InterestsRepoImpl
import com.companion.astrodating.ui.interests.domain.repository.IInterestsRepository
import com.companion.astrodating.ui.managePhotos.data.mapper.ManagePhotosMapper
import com.companion.astrodating.ui.managePhotos.data.repository.ManagePhotosRepoImpl
import com.companion.astrodating.ui.managePhotos.domain.repository.IManagePhotosRepository
import com.companion.astrodating.ui.message.data.mapper.GetAgoraTokenDetailsMapper
import com.companion.astrodating.ui.message.data.repository.IGetAgoraTokenDetailsRepository
import com.companion.astrodating.ui.message.domain.repository.GetAgoraTokenDetailsRepoImpl
import com.companion.astrodating.ui.notification.data.mapper.GetNotificationMapper
import com.companion.astrodating.ui.notification.data.repository.GetNotificationRepoImpl
import com.companion.astrodating.ui.notification.domain.repository.IGetNotificationRepository
import com.companion.astrodating.ui.onlineusers.data.mapper.GetOnlineUsersMapper
import com.companion.astrodating.ui.onlineusers.data.repository.GetOnlineUsersRepoImpl
import com.companion.astrodating.ui.onlineusers.domain.repository.IGetOnlineUsersRepository
import com.companion.astrodating.ui.otp.data.mapper.CountryDtoMapper
import com.companion.astrodating.ui.otp.data.mapper.OtpDtoMapper
import com.companion.astrodating.ui.otp.data.repository.CountryRepoImpl
import com.companion.astrodating.ui.otp.data.repository.OtpRepoImpl
import com.companion.astrodating.ui.otp.domain.repository.ICountryRepository
import com.companion.astrodating.ui.otp.domain.repository.IOtpRepository
import com.companion.astrodating.ui.profile.data.mapper.ProfileDtoMapper
import com.companion.astrodating.ui.profile.data.repository.ProfileRepoImpl
import com.companion.astrodating.ui.profile.domain.repository.IProfileRepository
import com.companion.astrodating.ui.profileDetails.data.mapper.GetUserDetailsMapper
import com.companion.astrodating.ui.profileDetails.data.repositroy.GetUserDetailsRepoImpl
import com.companion.astrodating.ui.profileDetails.domain.repository.IGetUserDetailsRepository
import com.companion.astrodating.ui.purchasePlans.data.mapper.GetPurchasePlanDetailsMapper
import com.companion.astrodating.ui.purchasePlans.data.mapper.UpdateBenefitsDetailsMapper
import com.companion.astrodating.ui.purchasePlans.data.repository.GetPurchasePlansDetailsRepoImpl
import com.companion.astrodating.ui.purchasePlans.data.repository.UpdateBenefitsDetailsRepoImpl
import com.companion.astrodating.ui.purchasePlans.domain.repository.IGetPurchasePlansRepository
import com.companion.astrodating.ui.purchasePlans.domain.repository.IUpdateBenefitsRepository
import com.companion.astrodating.ui.registration.data.mapper.AddUpdateMandatoryDetailsMapper
import com.companion.astrodating.ui.registration.data.mapper.GetMandatoryDetailsMapper
import com.companion.astrodating.ui.registration.data.repository.AddUpdateMandatoryDetailsRepoImpl
import com.companion.astrodating.ui.registration.data.repository.GetMandatoryDetailsRepoImpl
import com.companion.astrodating.ui.registration.domain.repository.IAddUpdateMandatoryDetailsRepository
import com.companion.astrodating.ui.registration.domain.repository.IGetMandatoryDetailsRepository
import com.companion.astrodating.ui.support.data.mapper.GetContactSupportMapper
import com.companion.astrodating.ui.support.data.repository.GetContactSupportRepoImpl
import com.companion.astrodating.ui.support.domain.repository.IGetContactSupportRepository
import com.companion.astrodating.ui.topMatches.data.repository.GetTopUserRepoImpl
import com.companion.astrodating.ui.topMatches.domain.repository.IGetTopUserRepository
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.mapper.GetAboutDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.repository.GetAboutDetailsRepoImpl
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.repository.IGetAboutDetailsRepository
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper.GetBasicDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper.GetMasterDataDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.repository.GetBasicDetailsRepoImpl
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.repository.GetMasterDataDetailsRepoImpl
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetBasicDetailsRepository
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetMasterDetailsRepository
import com.companion.astrodating.ui.uploadKyc.data.mapper.UploadKycMapper
import com.companion.astrodating.ui.uploadKyc.data.repository.UploadKycRepoImpl
import com.companion.astrodating.ui.uploadKyc.domain.repository.IUploadKycRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCountryRepository(api: CompanionApi, mapper: CountryDtoMapper): ICountryRepository {
        return CountryRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideOtpRepository(api: CompanionApi, mapper: OtpDtoMapper): IOtpRepository {
        return OtpRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetMandatoryDetailsRepository(api: CompanionApi, mapper: GetMandatoryDetailsMapper): IGetMandatoryDetailsRepository {
        return GetMandatoryDetailsRepoImpl(api = api, mapper = mapper)
    }
    @Singleton
    @Provides
    fun provideAddUpdateMandatoryDetailsRepository(api: CompanionApi, mapper: AddUpdateMandatoryDetailsMapper): IAddUpdateMandatoryDetailsRepository {
        return AddUpdateMandatoryDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Provides
    fun provideUpdateOnlineStatusMapper(): UpdateOnlineStatusMapper {
        return UpdateOnlineStatusMapper()
    }

//    @Provides
//    fun provideGetHomeUsersMapper(): GetHomeUsersMapper {
//        return GetHomeUsersMapper()
//    }

//    @Provides
//    fun provideUpdateLocationMapper(): UpdateLocationMapper {
//        return UpdateLocationMapper()
//    }

    @Provides
    fun provideGetHomeUserRepository(api: CompanionApi, mapper: GetHomeUsersMapper, mapper2: UpdateOnlineStatusMapper): IGetHomeUserRepository {
        return GetHomeUserRepoImpl(api = api, mapper = mapper, mapper2 = mapper2)
    }

    @Singleton
    @Provides
    fun provideUpdateLocationRepository(api: CompanionApi, mapper: UpdateLocationMapper): IUpdateLocationRepository{
        return UpdateLocationRepositoryImpl(api= api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetTopUserRepository(api: CompanionApi, mapper: GetHomeUsersMapper): IGetTopUserRepository {
        return GetTopUserRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetUserDetailsRepository(api: CompanionApi, mapper: GetUserDetailsMapper): IGetUserDetailsRepository {
        return GetUserDetailsRepoImpl(api = api, mapper = mapper)
    }
    @Singleton
    @Provides
    fun provideGetAboutDetailsRepository(api: CompanionApi, mapper: GetAboutDetailsMapper): IGetAboutDetailsRepository {
        return GetAboutDetailsRepoImpl(api = api, mapper = mapper)
    }
    @Singleton
    @Provides
    fun provideGetBasicDetailsRepository(api: CompanionApi, mapper: GetBasicDetailsMapper): IGetBasicDetailsRepository {
        return GetBasicDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetAshtakootMilanDetailsRepository(api: DivineApi, mapper: GetAshtakootMilanMapper): IGetAshtakootMilanRepository {
        return GetAshtakootMilanDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(api: CompanionApi, mapper: ProfileDtoMapper): IProfileRepository {
        return ProfileRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetDashakootMilanDetailsRepository(api: DivineApi, mapper: GetDashakootMilanMapper): IGetDashakootMilanRepository {
        return GetDashakootMilanDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideAstrologyDetailsRepository(api: DivineApi, mapper: GetAstrologyDetailsMapper): IGetAstrologyDetailsRepository {
        return GetAstrologyDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideManglikDoshaDetailsRepository(api: DivineApi, mapper: GetManglikDoshaMapper): IGetManglikDoshaRepository {
        return GetManglikDoshaRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideMatchingPlanetaryDetailsRepository(api: DivineApi, mapper: GetMatchingPlanetaryMapper): IGetMatchingPlanetaryRepository {
        return GetMatchingPlanetaryRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideInterestsRepository(api: CompanionApi, mapper: InterestsMapper): IInterestsRepository {
        return InterestsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideManagePhotosRepository(api: CompanionApi, mapper: ManagePhotosMapper): IManagePhotosRepository {
        return ManagePhotosRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideUploadKycRepository(api: CompanionApi, mapper: UploadKycMapper): IUploadKycRepository {
        return UploadKycRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(api: CompanionApi, mapper: GetNotificationMapper): IGetNotificationRepository {
        return GetNotificationRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideContactSupportRepository(api: CompanionApi, mapper: GetContactSupportMapper): IGetContactSupportRepository {
        return GetContactSupportRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun providePurchasePlansRepository(api: CompanionApi, mapper: GetPurchasePlanDetailsMapper): IGetPurchasePlansRepository {
        return GetPurchasePlansDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideUpdateBenefitsRepository(api: CompanionApi, mapper: UpdateBenefitsDetailsMapper): IUpdateBenefitsRepository {
        return UpdateBenefitsDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetMasterDataDetailsRepository(api: CompanionApi, mapper: GetMasterDataDetailsMapper): IGetMasterDetailsRepository {
        return GetMasterDataDetailsRepoImpl(api = api, mapper = mapper)
    }
    @Singleton
    @Provides
    fun provideAgoraTokenDetailsRepository(api: CompanionApi, mapper: GetAgoraTokenDetailsMapper): IGetAgoraTokenDetailsRepository {
        return GetAgoraTokenDetailsRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideMessageLimitRepository(api: CompanionApi, mapper: freeMessageMapper): freeMessageLimitRepository{
        return messageLimitRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideGetOnlineUsersRepository(api: CompanionApi, mapper: GetOnlineUsersMapper): IGetOnlineUsersRepository {
        return GetOnlineUsersRepoImpl(api = api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideSendMessagePushNotificationRepository(api: CompanionApi, mapper: sendMessagePushNotificationMapper): sendMesssagePushNotificationRepository{
        return sendMessagePushNotificationRepoImpl(api= api, mapper = mapper)
    }

    @Singleton
    @Provides
    fun provideRtcTokenRepository(api: CompanionApi, mapper: RtcTokenMapper): IRtcTokenRepository {
        return RtcTokenRepoImpl(api= api, mapper= mapper)
    }

    @Singleton
    @Provides
    fun provideCanSendCallRepository(api: CompanionApi, mapper: canSendCallMapper): canSendCallRepository {
        return canSendCallRepoImpl(api= api, mapper = mapper)
    }
}
