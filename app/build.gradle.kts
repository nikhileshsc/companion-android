plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = ProjectConfig.applicationId
    compileSdk = ProjectConfig.compileSdkVersion

    defaultConfig {
        applicationId = ProjectConfig.applicationId
        minSdk = ProjectConfig.minSdkVersion
        targetSdk = ProjectConfig.targetSdkVersion
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
       signingConfigs{
        create("release"){
            val releaseKeystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
            if (releaseKeystorePath != null) {
                storeFile = file(releaseKeystorePath)
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (System.getenv("RELEASE_KEYSTORE_PATH") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // Uses Android's default auto-generated debug keystore - no external
            // file needed. The release signing config above points to a keystore
            // that only exists on one developer's local machine, so debug builds
            // (including CI) must not depend on it.
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
//            applicationIdSuffix = ".debug"
//            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    ndkVersion = "28.2.13676358"//""26.1.10909125"
    externalNativeBuild {
        ndkBuild {
            path =File("src/main/jni/Android.mk") //path of Android.mk file
        }
    }
}

dependencies {
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.activityX)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.viewModelLifecycle)
    implementation(Dependencies.liveDataLifecycle)
    implementation(Dependencies.circularImageView)
    implementation(Dependencies.hilt)
    implementation("androidx.lifecycle:lifecycle-process:2.9.2")
    implementation("androidx.lifecycle:lifecycle-process:2.10.0")
    kapt (Dependencies.hiltCompiler)
    implementation(Dependencies.navigationUi)
    implementation(Dependencies.navigationFragment)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation(Dependencies.retrofit)
    implementation(Dependencies.gson)
    implementation(Dependencies.glide)
    implementation(Dependencies.okHttpLoggingInterceptor)
    implementation(Dependencies.dotsIndicator)
    implementation(Dependencies.dataStorePref)
    implementation(Dependencies.dataStorePrefCore)
    implementation(Dependencies.rtc)
    implementation(Dependencies.sdp)
    implementation(Dependencies.shimmer)
    implementation(Dependencies.swipeToRefresh)
    implementation(Dependencies.places)
    implementation(Dependencies.groupie)
    implementation(Dependencies.groupieViewBinding)
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
//    implementation (Dependencies.firebaseAnalytics)
    implementation (Dependencies.pinView)
    implementation (Dependencies.chatSdk)
    implementation("com.google.firebase:firebase-analytics")
    implementation (Dependencies.firebaseMessaging)
    implementation (Dependencies.billingClient)
    implementation (Dependencies.appUpdate)
    implementation (Dependencies.appUpdateKtx)
    implementation (Dependencies.facebookSdk)
    testImplementation(Testing.junit)
    androidTestImplementation(Testing.junitExt)
    androidTestImplementation(Testing.espressoCore)
    implementation("io.agora.rtc:chat-sdk:1.2.3") // use the latest version
//    implementation("io.agora.rtc:chat-callkit:1.1.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")



}
