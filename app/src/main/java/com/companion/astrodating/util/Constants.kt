package com.companion.astrodating.util

const val TAG = "LOG"
const val AGORA_TAG = "AGORA_LOG"
const val COMMON_AUTH = "Companion@1006"
const val KEY_OTP_DOMAIN_DATA = "otpDomaniData"
const val MOBILE = "mobile"
const val EMAIL = "email"
const val FROM = "from"
const val USER_ID = "userId"
const val SEC_USER_ID = "secondaryUserId"
const val SEC_USER_FULL_NAME = "secondaryUserFullName"
const val SEC_USER_PROFILE = "secondaryUserProfile"
const val TIME_ZONE = 5.5
const val ASHTAGUNA_MILAN_DATA = "ashtagunaMillanData"
const val DASHAGUNA_MILAN_DATA = "dashagunaMillanData"
const val ASTROLOGY_DETAILS_P1_DATA = "astrologyDetailsP1Data"
const val ASTROLOGY_DETAILS_P2_DATA = "astrologyDetailsP2Data"
const val MANGLIK_DOSHA_DATA = "manglikDoshaData"
const val USER_1 = "user1"
const val USER_2 = "user2"
const val USER_PROFILE_URL = "userProfileUrl"
const val AGORA_USER_NAME = "agoraUserName"
const val AGORA_CHAT_TOKEN = "agoraChatToken"
const val FILTER_MIN_HEIGHT = "filterMinHeight"
const val FILTER_MAX_HEIGHT = "filterMaxHeight"
const val FILTER_MIN_AGE = "filterMinAge"
const val FILTER_MAX_AGE = "filterMaxAge"
const val LANG_ENGLISH = "en"
const val PRODUCT_ID_CompanionSubscription = "companion_subscriptions"
const val PERMISSION_REQUEST_CODE = 1
const val CAMERA_PERMISSION_CODE = 2
const val fromChat = "fromChat"
const val fromUnlockCompleteAnalysis = "fromUnlockCompleteAnalysis"
const val FILTER_REQUEST_CODE = 100
const val RESET_FILTER_REQUEST_CODE = 101
const val REGISTRATION = "Registration"




const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val DATE_FORMAT1 = "yyyy-MM-dd"
const val TIME_FORMAT = "HH:mm"
const val REQUIRED_DATE_FORMAT = "dd/MMM/yyyy"
const val MONEY_ADDED_DATE_FORMAT = "dd MMMM yyyy"
const val FREE_KUNDALI_DATE_FORMAT = "dd MMM yyyy"
const val VIEW_KUNDALI_DATE_FORMAT = "dd MM yyyy"
const val APP_EMPTY_STRING = ""
const val APP_EMPTY_STRING_WITH_SPACE = " "

const val APP_ID = "b8e0284385e94e0cae21d5bb1e67d1d8"
const val API_KEY_ID = "rzp_test_EV29T9KR7MPktt"
const val FORMAT_MINUTES_AND_SECONDS = "%02dm : %02ds"
const val PER_PAGE_COUNT = 50
const val PAGE_NUMBER = 1
const val ERROR_CODE_LOGOUT = 401
const val ERROR_CODE_400 = 400
const val PREF_NAME = "COMPANION_PREF"
const val HOST_ID = 1001
const val FORMAT_HOURS_MINUTES_AND_SECONDS = "%02dh : %02dm: %02ds"
const val COUNT_DOWN_INTERVAL = 1000L
const val EDIT_FORMAT_DATE = "yyyy - MM - dd"
const val PAYMENT_SUCCESS_URL = "https://api.s1tara.com/api/v1/user/success"
const val PAYMENT_FAILURE_URL = "https://api.s1tara.com/api/v1/user/error"
const val SHOP_URL = "https://seetara.in/collections/all"
const val URL_PRIVACY_POLICY = "https://astrocompanion.com/privacy.html"
const val URL_TERMS_AND_CONDITION = "https://astrocompanion.com/terms.html"
const val CONTACT_SUPPORT_MESSAGE_URL = "https://api.whatsapp.com/send?phone=918080252281"
const val ASTROLOGER_PACKAGE_NAME = "com.astreus.astrologer"
const val GOOGLE_PLAYSTORE_URL = "https://play.google.com/store/apps/details?id="
const val MARKET_PLAYSTORE_URL = "market://details?id="
const val SUPPORT_DATE_FORMAT = "dd MMM yyyy hh:mm a"
const val CHAT_DATE_FORMAT = "dd MMM yyyy 'at' h:mm a"
const val MESSAGE_CHAT_DATE_FORMAT = "dd/MM/yyyy h:mm a"
const val YOUTUBE_PACKAGE = "com.google.android.youtube"
const val BLOG_WEBVIEW_USERAGENT ="Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13"


object CountryCodeConstant{
    const val COUNTRY_IN ="IN"
}

object CallStatusConstant{
    const val PENDING = "Pending"
}

object InterestTypeConstant{
    const val interestData = "interestData"
    const val shortlist = "shortlist"
    const val unshortlist = "unshortlist"
    const val sendInterest = "sendInterest"
    const val unsendInterest = "unsendInterest"
    const val block = "block"
    const val unblock = "unblock"
    const val declineInterest = "declineInterest"
    const val receivedInterest = "receivedInterest"
}
// [shortlist, unshortlist, sendInterest, unsendInterest, block, unblock, declineInterest]"

object InAppSubscriptionTypeConstant{
    const val newPurchase ="newPurchase"
    const val upgrade = "upgrade"
    const val downgrade = "downgrade"
    const val resubscribe = "resubscribe"
}

object PurchasePlanNameConstant{
    const val weekly = "Weekly"
    const val monthly = "Monthly"
    const val yearly = "Yearly"
}

object SubscriptionStatusConstants{
    const val active = "Active"
    const val cancelled = "Cancelled"
    const val expired = "Expired"
}

object UpdateBenefitsConstant{
    const val matchMakingReport = "matchMakingReport"
    const val chatProfiles = "chatProfiles"
}

object FilterConstants{
    const val community = "community"
    const val education = "education"
    const val profession = "profession"
    const val status = "status"
    const val height = "height"
    const val age = "age"
    const val religion = "religion"
    const val minAge = "minAge"
    const val maxAge = "maxAge"
    const val minHeight = "minHeight"
    const val maxHeight = "maxHeight"
    const val country = "country"
    const val city = "city"

}
object NotificationTypeConstants{
    const val type = "type"
    const val receivedInterest = "received-interest"
    const val declineInterest = "decline-interest"
    const val deleteMyAccount = "delete-my-account"
    const val photoRejected = "photo-rejected"
    const val photoApproved = "photo-approved"
    const val support = "support"
    const val appUpdate = "app-update"
    const val gallery = "gallery"
    const val subscription = "subscription"
    const val server = "server"
}
object RegexConstants{
    val nickNameRegex = "[a-zA-Z' ]+"
}

object FacebookAppEvent{
    const val login = "login"
    const val subscription = "subscription"
    const val unlock_chat = "unlock_chat"
    const val unlock_analysis = "unlock_analysis"
}