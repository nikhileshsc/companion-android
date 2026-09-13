package com.companion.astrodating.data.api


import com.companion.astrodating.ui.call.data.dto.RtcTokenDto
import com.companion.astrodating.ui.call.data.dto.RtcTokenRequestDto
import com.companion.astrodating.ui.chat.data.dto.SendMessagePushNotificationDto
import com.companion.astrodating.ui.chat.data.dto.canSendCallDto
import com.companion.astrodating.ui.chat.data.dto.messageLimitDto
import com.companion.astrodating.ui.chat.data.requestData.messageLimitRequestData
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData
import com.companion.astrodating.ui.home.data.dto.GetHomeUsersDto
import com.companion.astrodating.ui.home.data.dto.UpdateLocationDto
import com.companion.astrodating.ui.home.data.dto.UpdateOnlineStatusDto
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.companion.astrodating.ui.home.data.requestData.updateOnlineStatusRequestData
import com.companion.astrodating.ui.interests.data.dto.GetInterestDto
import com.companion.astrodating.ui.interests.data.dto.UpdateInterestDto
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.managePhotos.data.dto.DeletePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.GetMyPhotosDto
import com.companion.astrodating.ui.managePhotos.data.dto.SetProfilePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.UploadPhotoDto
import com.companion.astrodating.ui.managePhotos.data.requestData.DeletePhotoRequestData
import com.companion.astrodating.ui.managePhotos.data.requestData.SetProfilePhotoRequestData
import com.companion.astrodating.ui.message.data.dto.GetAgoraTokenDto
import com.companion.astrodating.ui.notification.data.dto.NotificationDto
import com.companion.astrodating.ui.onlineusers.data.dto.GetOnlineUsersDto
import com.companion.astrodating.ui.otp.data.dto.CountryDto
import com.companion.astrodating.ui.otp.data.dto.OtpDto
import com.companion.astrodating.ui.otp.data.dto.VerifyOtpDto
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.data.requestData.VerifyOtpRequestData
import com.companion.astrodating.ui.profile.data.dto.LogoutDto
import com.companion.astrodating.ui.profileDetails.data.dto.GetUserDetailsDto
import com.companion.astrodating.ui.purchasePlans.data.dto.CreateInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.dto.GetPlanDtoDetails
import com.companion.astrodating.ui.purchasePlans.data.dto.UpdateBenefitsDto
import com.companion.astrodating.ui.purchasePlans.data.dto.VerifyInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.requestData.CreateInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.ui.registration.data.dto.AddUpdateMandatoryDetailsDto
import com.companion.astrodating.ui.registration.data.dto.GetMandatoryDetailsDto
import com.companion.astrodating.ui.registration.data.requestData.AddUpdateMandatoryDetailsRequestData
import com.companion.astrodating.ui.support.data.dto.ContactSupportDto
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.dto.GetAboutDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.requestData.UpdateAboutDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetBasicDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetMasterDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.requestData.UpdateBasicDetailsRequestData
import com.companion.astrodating.ui.uploadKyc.data.dto.GetKycDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface CompanionApi {

    @GET("api/app/v1/user/getCountryCodesAndFlags")
    suspend fun getCountryList(@Header("authorization") token: String): Response<CountryDto>

    @POST("api/app/v1/user/requestOtp")
    suspend fun requestOtp(
        @Header("authorization") token: String,
        @Body body: RequestOtpRequestData
    ): Response<OtpDto>


    @POST("api/app/v1/user/verifyOtp")
    suspend fun verifyOtp(
        @Header("authorization") token: String,
        @Body body: VerifyOtpRequestData
    ): Response<VerifyOtpDto>

    @GET("api/app/v1/user/getMandatoryDetails")
    suspend fun getMandatoryDetails(
        @Header("authorization") token: String
    ): Response<GetMandatoryDetailsDto>

    @POST("api/app/v1/user/addUpdateMandatoryDetails")
    suspend fun addUpdateMandatoryDetails(
        @Header("authorization") token: String, @Body body: AddUpdateMandatoryDetailsRequestData
    ): Response<AddUpdateMandatoryDetailsDto>

    @GET("api/app/v1/user/getHomeUsers")
    suspend fun getHomeUsers(
        @Header("authorization") token: String,
        @Query("searchText") searchText: String,
        @Query("minHeight") minHeight: Double,
        @Query("maxHeight") maxHeight: Double,
        @Query("minAge") minAge: Int,
        @Query("maxAge") maxAge: Int,
        @Query("education[]") education: List<String>,
        @Query("profession[]") profession: List<String>,
        @Query("religion[]") religion: List<String>,
        @Query("status[]") status: List<String>,
        @Query("country[]") country: List<String>,
        @Query("city[]") city: List<String>,
        @Query("community[]") community: List<String>,
        @Query("pageNumber") pageNumber: Int,
        @Query("perPage") perPage: Int
    ): Response<GetHomeUsersDto>

    @GET("api/app/v1/user/getTopUsers")
    suspend fun getTopUsers(
        @Header("authorization") token: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("perPage") perPage: Int
    ): Response<GetHomeUsersDto>

    @GET("api/app/v1/user/getUserDetailsById")
    suspend fun getUsersDetailsById(
        @Header("authorization") token: String,
        @Query("userId") userId: String
    ): Response<GetUserDetailsDto>

    @GET("api/app/v1/user/getAboutDetails")
    suspend fun getAboutDetails(
        @Header("authorization") token: String
    ): Response<GetAboutDetailsDto>

    @POST("api/app/v1/user/updateAboutDetails")
    suspend fun updateAboutDetails(
        @Header("authorization") token: String, @Body body: UpdateAboutDetailsRequestData
    ): Response<GetAboutDetailsDto>

    @GET("api/app/v1/user/getBasicDetails")
    suspend fun getBasicDetails(
        @Header("authorization") token: String
    ): Response<GetBasicDetailsDto>

    @POST("api/app/v1/user/updateBasicDetails")
    suspend fun updateBasicDetails(
        @Header("authorization") token: String, @Body body: UpdateBasicDetailsRequestData
    ): Response<GetBasicDetailsDto>
    @POST("api/app/v1/user/logout")
    suspend fun logoutUser(@Header("authorization") token: String): Response<LogoutDto>

    @POST("api/app/v1/user/updateInterestsByType")
    suspend fun updateInterest(@Header("authorization") token: String,@Body body: InterestRequestData): Response<UpdateInterestDto>

    @GET("api/app/v1/user/getInterestsByType")
    suspend fun getInterestByType(@Header("authorization") token: String,@Query("type") interestType: String): Response<GetInterestDto>

    @GET("api/app/v1/user/getMyPhotos")
    suspend fun getMyPhotos(@Header("authorization") token: String): Response<GetMyPhotosDto>

    @POST("api/app/v1/user/setProfileFromGallery")
    suspend fun setProfilePhoto(
        @Header("authorization") token: String, @Body body: SetProfilePhotoRequestData
    ): Response<SetProfilePhotoDto>

    @HTTP(method = "DELETE", path = "api/app/v1/user/deletePhotoFromGallery", hasBody = true)
    suspend fun deletePhoto(
        @Header("authorization") token: String, @Body body: DeletePhotoRequestData
    ): Response<DeletePhotoDto>

    @Multipart
    @POST("api/app/v1/user/uploadFileByType")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Query("fileType") fileType: String,
        @Part image: MultipartBody.Part
    ): Response<UploadPhotoDto>

    @GET("api/app/v1/user/getKycDetails")
    suspend fun getKyc(@Header("authorization") token: String): Response<GetKycDto>

    @Multipart
    @POST("api/app/v1/user/uploadKycDocumentsByType")
    suspend fun uploadKycDocument(
        @Header("Authorization") token: String,
        @Query("fileType") fileType: String,
        @Part image: MultipartBody.Part
    ): Response<UploadPhotoDto>

    @GET("api/app/v1/user/getNotifications")
    suspend fun getNotification(
        @Header("authorization") token: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("perPage") perPage: Int
    ): Response<NotificationDto>

    @POST("api/app/v1/user/createNewTicket")
    suspend fun createNewTicket(
        @Header("authorization") token: String, @Body body: ContactSupportRequestData
    ): Response<ContactSupportDto>

    @GET("api/app/v1/user/getPlanDetails")
    suspend fun getPurchasePlanDetails(
        @Header("authorization") token: String,
    ): Response<GetPlanDtoDetails>
    @POST("api/app/v1/user/createInAppSubscription")
    suspend fun createInAppSubscription(
        @Header("authorization") token: String,
        @Body body: CreateInAppSubscriptionRequestData
    ): Response<CreateInAppSubscriptionDto>

    @POST("api/app/v1/user/verifyInAppSubscription")
    suspend fun verifyInAppSubscription(
        @Header("authorization") token: String,
        @Body body: VerifyInAppSubscriptionRequestData
    ): Response<VerifyInAppSubscriptionDto>

    @POST("api/app/v1/user/updateBenefitByType")
    suspend fun updateBenefitByType(
        @Header("authorization") token: String,
        @Body body: UpdateBenefitRequestData
    ): Response<UpdateBenefitsDto>

    @GET("api/app/v1/user/getMaster")
    suspend fun getMasterDataDetails(
        @Header("authorization") token: String
    ): Response<GetMasterDto>

    @GET("api/app/v1/user/regenerateChatToken")
    suspend fun getAgoraTokenDetails(
        @Header("authorization") token: String
    ): Response<GetAgoraTokenDto>

    @POST("api/app/v1/user/updateOnlineStatus")
    suspend fun updateOnlineStatus(
        @Header("authorization") token: String,
        @Body body: updateOnlineStatusRequestData
    ): Response<UpdateOnlineStatusDto>

    @POST("api/app/v1/user/updateLocation")
    suspend fun updateLocation(
        @Header("authorization") token: String,
        @Body body: updateLocationRequestData
    ): Response<UpdateLocationDto>

    @POST("api/app/v1/user/checkAndIncrementMessage")
    suspend fun checkFreeMessages(
        @Header("authorization") token: String,
        @Body body: messageLimitRequestData
    ): Response<messageLimitDto>

    @GET("api/app/v1/user/getOnlineUsersSorted")
    suspend fun getOnlineUsers(
        @Header("authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<GetOnlineUsersDto>

    @POST("api/app/v1/user/sendMessagePushNotification")
    suspend fun sendMessagePushNotification(
        @Header("authorization") token: String,
        @Body body: sendMessagePushNotificationData
    ): Response<SendMessagePushNotificationDto>

    @POST("api/app/v1/user/generateRtcTokenForCalling")
    suspend fun getRtcToken(
        @Header("authorization") token: String,
        @Body body: RtcTokenRequestDto
    ): Response<RtcTokenDto>

    @GET("api/app/v1/user/canSendCall")
    suspend fun canSendCall(
        @Header("authorization") token: String,
//        @Body body: canSendCallDto not needed
    ): Response<canSendCallDto>
}
