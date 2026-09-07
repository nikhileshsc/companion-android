package com.companion.astrodating.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.ui.filter.model.FilterDomainEntity
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryUserDetailsDomainEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object StorePreferences {

    private const val PREF_NAME = "COMPANION_PREF"
    private const val SKIP_ON_BOARDING = "SKIP_ONBOARDING"
    private const val SELECTED_LANGUAGE = "LANGUAGE"
    private const val AUTH_TOKEN = "AUTH_TOKEN"
    private const val IS_REGISTERED = "IS_REGISTERED"
    private const val IS_UPDATE_PROFILE = "IS_UPDATE_PROFILE"
    private const val DEVICE_TOKEN = "DEVICE_TOKEN"
    private const val USER_DATA = "USER_DATA"
    private const val COMMUNITY_LIST = "COMMUNITY_LIST"
    private const val DIVINE_API_KEY = "DIVINE_API_KEY"
    private const val DIVINE_API_ACCESS_TOKEN = "DIVINE_API_ACCESS_TOKEN"
    private const val AGORA_CHAT_APP_KEY = "AGORA_CHAT_APP_KEY"
    private const val ASHTAKOOT_REQ_DATA = "ASHTAKOOT_REQ_DATA"
    private const val REQUEST_OTP_REQ_DATA = "REQUEST_OTP_REQ_DATA"
    private const val SUBSCRIPTION_DATA = "SUBSCRIPTION_DATA"
    private const val EMPTY_STRING = ""
    private const val FILTER_COUNTRY_LIST = "FILTER_COUNTRY_LIST"
    private const val FILTER_COMMUNITY_LIST = "FILTER_COMMUNITY_LIST"
    private const val FILTER_EDUCATION_LIST = "FILTER_EDUCATION_LIST"
    private const val FILTER_HEIGHT_LIST = "FILTER_HEIGHT_LIST"
    private const val FILTER_AGE_LIST = "FILTER_AGE_LIST"
    private const val FILTER_MARITAL_STATUS_LIST = "FILTER_MARITAL_STATUS_LIST"
    private const val FILTER_PROFESSION_LIST = "FILTER_PROFESSION_LIST"
    private const val FILTER_RELIGION_LIST = "FILTER_RELIGION_LIST"

    private lateinit var preferences: SharedPreferences


    fun initPreferences(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }

    fun saveOnboardingStatus(shouldShow: Boolean) {
        preferences.edit().apply {
            putBoolean(SKIP_ON_BOARDING, shouldShow)
        }.apply()
    }

    fun getOnboardingStatus(): Boolean {
        return preferences.getBoolean(SKIP_ON_BOARDING, false)
    }


    fun saveSelectedLanguage(language: String) {
        preferences.edit().apply {
            putString(SELECTED_LANGUAGE, language)
        }.apply()
    }

    fun getSelectedLanguage(): String? {
        return preferences.getString(SELECTED_LANGUAGE, EMPTY_STRING)
    }

    fun saveAuthToken(token: String) {
        preferences.edit().apply {
            putString(AUTH_TOKEN, token)
        }.apply()
    }

    fun getAuthToken(): String? {
        return preferences.getString(AUTH_TOKEN, EMPTY_STRING)
    }

    fun saveRegistrationStatus(isRegistered: Boolean) {
        preferences.edit().apply {
            putBoolean(IS_REGISTERED, isRegistered)
        }.apply()
    }

    fun isRegistrationCompleted(): Boolean {
        return preferences.getBoolean(IS_REGISTERED, false)
    }

    fun saveUpdateProfileStatus(isRegistered: Boolean) {
        preferences.edit().apply {
            putBoolean(IS_UPDATE_PROFILE, isRegistered)
        }.apply()
    }

    fun isUpdateProfileCompleted(): Boolean {
        return preferences.getBoolean(IS_UPDATE_PROFILE, false)
    }

    fun saveDeviceToken(deviceToken: String) {
        preferences.edit().apply{
            putString(DEVICE_TOKEN, deviceToken)
        }.apply()
    }

    fun getDeviceToken(): String? {
        return preferences.getString(DEVICE_TOKEN, EMPTY_STRING)
    }

    fun saveCommunityList(communityList: List<String>) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(communityList)
            putString(COMMUNITY_LIST, json)
            apply()
        }
    }
    fun getCommunityList(): List<String>? {
        val gson = Gson()
        val json = preferences.getString(COMMUNITY_LIST, EMPTY_STRING)
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveAgoraChatAppKey(apiKey: String) {
        preferences.edit().apply{
            putString(AGORA_CHAT_APP_KEY, apiKey)
        }.apply()
    }

    fun getAgoraChatAppKey(): String? {
        return preferences.getString(AGORA_CHAT_APP_KEY, EMPTY_STRING)
    }
    fun saveDivineApiKey(apiKey: String) {
        preferences.edit().apply{
            putString(DIVINE_API_KEY, apiKey)
        }.apply()
    }

    fun getDivineApiKey(): String? {
        return preferences.getString(DIVINE_API_KEY, EMPTY_STRING)
    }

    fun saveDivineApiAccessToken(token: String) {
        preferences.edit().apply{
            putString(DIVINE_API_ACCESS_TOKEN, token)
        }.apply()
    }

    fun getDivineApiAccessToken(): String? {
        return preferences.getString(DIVINE_API_ACCESS_TOKEN, EMPTY_STRING)
    }

    fun saveUserDetails(userDetails: GetMandatoryUserDetailsDomainEntity) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(userDetails)
            putString(USER_DATA, json)
            apply()
        }
    }
    fun getUserDetails(): GetMandatoryUserDetailsDomainEntity {
        val gson = Gson()
        val json = preferences.getString(USER_DATA, EMPTY_STRING)
        return gson.fromJson(json, GetMandatoryUserDetailsDomainEntity::class.java)
    }
    fun saveProfileUrl(url: String) {
        preferences.edit().apply {
            putString(USER_PROFILE_URL, url)
        }.apply()
    }

    fun getProfileUrl(): String? {
        return preferences.getString(USER_PROFILE_URL, EMPTY_STRING)
    }

    fun saveAshtakootRequestData(ashtakootMilanRequestData: AshtakootMilanRequestData) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(ashtakootMilanRequestData)
            putString(ASHTAKOOT_REQ_DATA, json)
            apply()
        }
    }
    fun getAshtakootRequestData(): AshtakootMilanRequestData {
        val gson = Gson()
        val json = preferences.getString(ASHTAKOOT_REQ_DATA, EMPTY_STRING)
        return gson.fromJson(json, AshtakootMilanRequestData::class.java)
    }

    fun savePrimaryUserAgoraUserName(url: String) {
        preferences.edit().apply {
            putString(AGORA_USER_NAME, url)
        }.apply()
    }

    fun getPrimaryUserAgoraUserName(): String? {
        return preferences.getString(AGORA_USER_NAME, EMPTY_STRING)
    }

    fun savePrimaryUserAgoraChatToken(url: String) {
        preferences.edit().apply {
            putString(AGORA_CHAT_TOKEN, url)
        }.apply()
    }

    fun getPrimaryUserAgoraChatToken(): String? {
        return preferences.getString(AGORA_CHAT_TOKEN, EMPTY_STRING)
    }

    fun saveRequestOtpRequestData(requestData: RequestOtpRequestData) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(requestData)
            putString(REQUEST_OTP_REQ_DATA, json)
            apply()
        }
    }
    fun getRequestOtpRequestData(): RequestOtpRequestData {
        val gson = Gson()
        val json = preferences.getString(REQUEST_OTP_REQ_DATA, EMPTY_STRING)
        return gson.fromJson(json, RequestOtpRequestData::class.java)
    }

    fun saveSubscriptionData(subscriptionDomain: GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity?) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(subscriptionDomain)
            putString(SUBSCRIPTION_DATA, json)
            apply()
        }
    }
    fun getSubscriptionData(): GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity? {
        val gson = Gson()
        val json = preferences.getString(SUBSCRIPTION_DATA, EMPTY_STRING)
        return gson.fromJson(json, GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity::class.java)
    }
    fun saveFilterCountryList(list: ArrayList<FilterDomainEntity>) {
        preferences.edit().apply {
            val gson = Gson()
            val json = gson.toJson(list)
            putString(FILTER_COUNTRY_LIST, json)
            apply()
        }
    }
    fun getFilterCountryList():  ArrayList<FilterDomainEntity>? {
        val gson = Gson()
        val json = preferences.getString(FILTER_COUNTRY_LIST, EMPTY_STRING)
        val type = object : TypeToken<List<FilterDomainEntity>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveFilterMinHeight(height: Int) {
        preferences.edit().apply {
            putInt(FILTER_MIN_HEIGHT, height)
        }.apply()
    }

    fun getFilterMinHeight(): Int {
        return preferences.getInt(FILTER_MIN_HEIGHT, 4)
    }
    fun saveFilterMaxHeight(height: Int) {
        preferences.edit().apply {
            putInt(FILTER_MAX_HEIGHT, height)
        }.apply()
    }

    fun getFilterMaxHeight(): Int {
        return preferences.getInt(FILTER_MAX_HEIGHT, 8)
    }

    fun saveFilterMinAge(age: Int) {
        preferences.edit().apply {
            putInt(FILTER_MIN_AGE, age)
        }.apply()
    }

    fun getFilterMinAge(): Int {
        return preferences.getInt(FILTER_MIN_AGE, 18)
    }
    fun saveFilterMaxAge(height: Int) {
        preferences.edit().apply {
            putInt(FILTER_MAX_AGE, height)
        }.apply()
    }
    fun getFilterMaxAge(): Int {
        return preferences.getInt(FILTER_MAX_AGE, 100)
    }
    fun clearSharedPreferences() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}
