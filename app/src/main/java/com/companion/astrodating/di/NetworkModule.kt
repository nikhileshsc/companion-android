package com.companion.astrodating.di

import android.util.Base64
import com.android.volley.BuildConfig.DEBUG
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.mapper.DivineApiGsonProvider
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton





@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private external fun getBaseUrl(): String
    private external fun getProdUrl(): String
    private external fun getDivineAPIBaseUrl(): String

    val BASE_URL: String = String(Base64.decode(getBaseUrl(), Base64.DEFAULT))
//    val BASE_URL: String = "http://apicompaniontest.ap-south-1.elasticbeanstalk.com/"
//    val BASE_URL: String = "http://192.168.0.108:3000/"

    val PROD_URL: String = String(Base64.decode(getProdUrl(), Base64.DEFAULT))
    val DIVINE_API_BASE_URL: String = String(Base64.decode(getDivineAPIBaseUrl(), Base64.DEFAULT))


    @Singleton
    @Provides
    fun provideCompanionApiService(): CompanionApi {
        val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level =  if (DEBUG) {
                HttpLoggingInterceptor.Level.BODY // Log in debug mode
            } else {
            HttpLoggingInterceptor.Level.NONE // Disable logging in release mode
        }
        }
        val client = OkHttpClient.Builder().apply {
            addInterceptor(headerInterceptor)
            addInterceptor(loggingInterceptor)
        }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CompanionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDivineApiService(): DivineApi {
        val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            addInterceptor(headerInterceptor)
            addInterceptor(loggingInterceptor)
        }.build()

//        val client = OkHttpClient.Builder().apply {
//            addInterceptor(headerInterceptor)
//
//            // Custom logging interceptor
//            addInterceptor { chain ->
//                val request = chain.request()
//
//                // Clone request body for logging
//                val requestBody = request.body
//                val buffer = okio.Buffer()
//                requestBody?.writeTo(buffer)
//                val bodyString = buffer.readUtf8()
//
//                android.util.Log.d("API_CALL", "URL: ${request.url}")
//                android.util.Log.d("API_CALL", "Method: ${request.method}")
//                android.util.Log.d("API_CALL", "Request Body: $bodyString")
//
//                val response = chain.proceed(request)
//
//                android.util.Log.d("API_CALL", "Response Code: ${response.code}")
//
//                response
//            }
//
//            addInterceptor(loggingInterceptor)
//        }.build()


        return Retrofit.Builder()
            .baseUrl(DIVINE_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(DivineApiGsonProvider.create()))
            .build()
            .create(DivineApi::class.java)
    }
}
