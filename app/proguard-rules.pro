# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
## Keep all log messages
#-keep class android.util.Log {
#    public static *** d(...);
#    public static *** i(...);
#    public static *** w(...);
#    public static *** e(...);
#    public static *** v(...);
#}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.companion.astrodating.**domain.**{ *; }
-keep class com.companion.astrodating.**dto.**{ *; }
-keep class com.companion.astrodating.**mapper.**{ *; }
-keep class com.companion.astrodating.**data.**{ *; }
-keep class com.companion.astrodating.base.**{ *; }

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-optimizations !method/inlining/*

# Retrofit
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Webview
-dontwarn android.webkit.WebView
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-keep class io.agora.**{*;}
-dontwarn io.agora.**

-dontwarn com.heytap.msp.push.HeytapPushManager
-dontwarn com.heytap.msp.push.callback.ICallBackResultService
-dontwarn com.meizu.cloud.pushsdk.PushManager
-dontwarn com.meizu.cloud.pushsdk.util.MzSystemUtils
-dontwarn com.vivo.push.IPushActionListener
-dontwarn com.vivo.push.PushClient
-dontwarn com.vivo.push.util.VivoPushException
-dontwarn com.xiaomi.mipush.sdk.MiPushClient
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.google.devtools.build.android.desugar.runtime.ThrowableExtension
-dontwarn com.meizu.cloud.pushsdk.MzPushMessageReceiver
-dontwarn com.meizu.cloud.pushsdk.handler.MzPushMessage
-dontwarn com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus
-dontwarn com.meizu.cloud.pushsdk.platform.message.RegisterStatus
-dontwarn com.meizu.cloud.pushsdk.platform.message.SubAliasStatus
-dontwarn com.meizu.cloud.pushsdk.platform.message.SubTagsStatus
-dontwarn com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus
-dontwarn com.vivo.push.PushConfig$Builder
-dontwarn com.vivo.push.PushConfig
-dontwarn com.vivo.push.listener.IPushQueryActionListener
-dontwarn com.vivo.push.model.UPSNotificationMessage
-dontwarn com.vivo.push.sdk.OpenClientPushMessageReceiver
-dontwarn com.xiaomi.mipush.sdk.MiPushCommandMessage
-dontwarn com.xiaomi.mipush.sdk.MiPushMessage
-dontwarn com.xiaomi.mipush.sdk.PushMessageReceiver

# Never inline methods, but allow shrinking and obfuscation.
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.ViewCompat$Api* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.WindowInsetsCompat$*Impl* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.app.NotificationCompat$*$Api*Impl {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.os.UserHandleCompat$Api*Impl {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.widget.EdgeEffectCompat$Api*Impl {
  <methods>;
}
# Firebase
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.location.**

-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.** { *; }
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Remove logging calls
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#    public static *** i(...);
#    public static *** w(...);
#    public static *** e(...);
#}

















#-keep class io.agora.** {*;}
#-dontwarn  io.agora.**
#

#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}
#
#-keep class com.companion.astrodating.**domain.**{ *; }
#-keep class com.companion.astrodating.**dto.**{ *; }
#-keep class com.companion.astrodating.**mapper.**{ *; }
#-keep class com.companion.astrodating.**model.**{ *; }
#-keep class com.companion.astrodating.**repository.**{ *; }
#-keep class com.companion.astrodating.**di.**{ *; }
#-keep class com.companion.astrodating.**data.**{ *; }
#-keep class com.companion.astrodating.base.**{ *; }
#
#-keepattributes JavascriptInterface
#-keepattributes *Annotation*
#
#-optimizations !method/inlining/*
#
## Retrofit
#-dontwarn retrofit2.**
#-dontwarn org.codehaus.mojo.**
#-keep class retrofit2.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#-keepattributes *Annotation*
#-keepattributes RuntimeVisibleAnnotations
#-keepattributes RuntimeInvisibleAnnotations
#-keepattributes RuntimeVisibleParameterAnnotations
#-keepattributes RuntimeInvisibleParameterAnnotations
#-keepattributes EnclosingMethod
#-keepclasseswithmembers class * {
#    @retrofit2.* <methods>;
#}
#-keepclasseswithmembers interface * {
#    @retrofit2.* <methods>;
#}
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}
#-keepclassmembers,allowobfuscation class * {
#  @com.google.gson.annotations.SerializedName <fields>;
#}
#
## Webview
#-dontwarn android.webkit.WebView
#-keep public class android.net.http.SslError
#-keep public class android.webkit.WebViewClient
#-dontwarn android.net.http.SslError
#-dontwarn android.webkit.WebViewClient
#
#-keep class io.agora.**{*;}
#
#-dontwarn com.heytap.msp.push.HeytapPushManager
#-dontwarn com.heytap.msp.push.callback.ICallBackResultService
#-dontwarn com.meizu.cloud.pushsdk.PushManager
#-dontwarn com.meizu.cloud.pushsdk.util.MzSystemUtils
#-dontwarn com.vivo.push.IPushActionListener
#-dontwarn com.vivo.push.PushClient
#-dontwarn com.vivo.push.util.VivoPushException
#-dontwarn com.xiaomi.mipush.sdk.MiPushClient
#-dontwarn org.bouncycastle.jsse.BCSSLParameters
#-dontwarn org.bouncycastle.jsse.BCSSLSocket
#-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
#-dontwarn org.conscrypt.Conscrypt$Version
#-dontwarn org.conscrypt.Conscrypt
#-dontwarn org.conscrypt.ConscryptHostnameVerifier
#-dontwarn org.openjsse.javax.net.ssl.SSLParameters
#-dontwarn org.openjsse.javax.net.ssl.SSLSocket
#-dontwarn org.openjsse.net.ssl.OpenJSSE
#
## Never inline methods, but allow shrinking and obfuscation.
#-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.ViewCompat$Api* {
#  <methods>;
#}
#-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.WindowInsetsCompat$*Impl* {
#  <methods>;
#}
#-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.app.NotificationCompat$*$Api*Impl {
#  <methods>;
#}
#-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.os.UserHandleCompat$Api*Impl {
#  <methods>;
#}
#-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.widget.EdgeEffectCompat$Api*Impl {
#  <methods>;
#}
## Firebase
#-keep class com.google.android.gms.** { *; }
#-keep class com.google.firebase.** { *; }
#
#-keep class com.android.vending.billing.**
#
## Keep all classes in androidx.core
#-keep class androidx.core.** { *; }
#
## Keep Kotlin metadata
#-keep class kotlin.Metadata { *; }
#
## Keep specific classes if needed
#-keep class androidx.core.app.** { *; }
#-keep class androidx.core.content.** { *; }
#
## If using coroutines, keep coroutine-related classes
#-keep class kotlinx.coroutines.** { *; }
#
## Keep all classes in androidx.activity
#-keep class androidx.activity.** { *; }
#
## Keep specific classes if needed
#-keep class androidx.activity.ComponentActivity { *; }
#-keep class androidx.activity.result.** { *; }
#
## Keep all classes in androidx.appcompat
#-keep class androidx.appcompat.** { *; }
#
## Keep specific classes if needed
#-keep class androidx.appcompat.app.** { *; }
#-keep class androidx.appcompat.widget.** { *; }
#
## Keep View Binding and Data Binding classes
#-keep class **Binding { *; }
#
## Keep annotations
#-keep @androidx.annotation.** class * { *; }
#
## Keep all classes in com.google.android.material
#-keep class com.google.android.material.** { *; }
#
## Keep all classes in androidx.constraintlayout
#-keep class androidx.constraintlayout.** { *; }
#
## Keep specific classes if needed
#-keep class androidx.constraintlayout.widget.** { *; }
#
## Keep all classes in androidx.lifecycle
#-keep class androidx.lifecycle.** { *; }
#
## Keep ViewModel classes
#-keep class * extends androidx.lifecycle.ViewModel { *; }
#
## Keep LiveData classes
#-keep class androidx.lifecycle.LiveData { *; }
#
## Keep specific lifecycle classes
#-keep class androidx.lifecycle.ViewModelProvider { *; }
#-keep class androidx.lifecycle.ViewModelStore { *; }
#
#
## Keep LiveData classes
#-keep class androidx.lifecycle.MutableLiveData { *; }
#
## Keep specific lifecycle classes
#-keep class androidx.lifecycle.Observer { *; }
#-keep class androidx.lifecycle.LiveData$ObserverWrapper { *; }
#
## Keep all classes in de.hdodenhof.circleimageview
#-keep class de.hdodenhof.circleimageview.** { *; }
#
## Keep CircleImageView class
#-keep class de.hdodenhof.circleimageview.CircleImageView { *; }
#
## Keep Hilt classes
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }
#
## Keep generated Hilt classes
#-keep class **_HiltComponents* { *; }
#-keep class **_HiltModules* { *; }
#-keep class **_HiltWrapper* { *; }
#
## Keep annotations
#-keep @dagger.** class * { *; }
#-keep @javax.inject.** class * { *; }
#
## Keep ViewModel and Activity classes
#-keep class * extends androidx.appcompat.app.AppCompatActivity { *; }
#
## Keep all classes in androidx.navigation
#-keep class androidx.navigation.** { *; }
#
## Keep navigation annotations
#-keep @androidx.navigation.** class * { *; }
#
## Keep specific navigation classes
#-keep class androidx.navigation.NavController { *; }
#-keep class androidx.navigation.NavHostFragment { *; }
#-keep class androidx.navigation.fragment.NavHostFragment { *; }
#
## Keep Gson classes
#-keep class com.google.gson.** { *; }
#
## Keep Gson annotations
#-keep @com.google.gson.annotations.** class * { *; }
#
## Keep Retrofit call adapter classes
#-keep class retrofit2.CallAdapterFactory { *; }
#
## Keep all classes in Glide
#-keep class com.bumptech.glide.** { *; }
#
## Keep Glide annotations
#-keep @com.bumptech.glide.annotation.GlideModule class * { *; }
#
## Keep generated Glide API
#-keep class **GlideModule { *; }
#-keep class * extends com.bumptech.glide.module.GlideModule { *; }
#
## Keep Bitmap and Drawable classes
#-keep class android.graphics.Bitmap { *; }
#-keep class android.graphics.drawable.** { *; }
#
## Keep Glide's RequestOptions
#-keep class com.bumptech.glide.request.RequestOptions { *; }
#
## Keep all classes in okhttp3
#-keep class okhttp3.** { *; }
#
## Keep logging interceptor classes
#-keep class okhttp3.logging.** { *; }
#
## Keep annotations
#-keep @okhttp3.logging.** class * { *; }
#
## Keep all classes in com.tbuonomo.dotsindicator
#-keep class com.tbuonomo.dotsindicator.** { *; }
#
## Keep annotations
#-keep @com.tbuonomo.dotsindicator.** class * { *; }
#
## Keep specific DotsIndicator class
#-keep class com.tbuonomo.dotsindicator.DotsIndicator { *; }
#
## Keep all classes in io.agora
#-keep class io.agora.** { *; }
#
## Keep annotations
#-keep @io.agora.** class * { *; }
#
## Keep specific Agora RTC classes
#-keep class io.agora.rtc.** { *; }
#
## Keep interfaces
#-keep interface io.agora.** { *; }
#
## Keep reflection classes
#-keep class io.agora.rtc.internal.** { *; }
#
## Keep all classes in com.intuit.sdp
#-keep class com.intuit.sdp.** { *; }
#
## Keep annotations
#-keep @com.intuit.sdp.** class * { *; }
#
## Keep specific SDP class
#-keep class com.intuit.sdp.SDP { *; }
#
## Keep all classes in com.facebook.shimmer
#-keep class com.facebook.shimmer.** { *; }
#
## Keep annotations
#-keep @com.facebook.shimmer.** class * { *; }
#
## Keep specific Shimmer classes
#-keep class com.facebook.shimmer.Shimmer { *; }
#-keep class com.facebook.shimmer.ShimmerFrameLayout { *; }
#
## Keep all classes in androidx.swiperefreshlayout
#-keep class androidx.swiperefreshlayout.** { *; }
#
## Keep annotations
#-keep @androidx.annotation.** class * { *; }
#
## Keep specific SwipeRefreshLayout class
#-keep class androidx.swiperefreshlayout.widget.SwipeRefreshLayout { *; }
#
## Keep all classes in com.google.android.libraries.places
#-keep class com.google.android.libraries.places.** { *; }
#
## Keep Parcelable classes
#-keep public class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## Keep annotations
#-keep @com.google.android.gms.common.annotation.Keep class * { *; }
#
## Keep specific Places API classes
#-keep class com.google.android.libraries.places.api.** { *; }
#
## Keep all classes in com.github.lisawray.groupie
#-keep class com.github.lisawray.groupie.** { *; }
#
## Keep Groupie ViewHolder classes
#-keep class com.github.lisawray.groupie.viewholder.** { *; }
#
## Keep annotations
#-keep @com.github.lisawray.groupie.** class * { *; }
#
## Keep Parcelable classes
#-keep public class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## Keep Groupie ViewBinding classes
#-keep class com.github.lisawray.groupie.viewbinding.** { *; }
#
## Keep attributes necessary for Firebase
#-keepattributes Signature
#-keepattributes *Annotation*
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses
#
## Keep all Firebase classes
#-keep class com.google.firebase.** { *; }
#
## Keep model classes
#-keepclassmembers class com.yourcompany.models.** { *; }
#
## Keep Parcelable classes
#-keep public class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## Keep all classes in com.github.GoodieBag.pinview
#-keep class com.github.GoodieBag.pinview.** { *; }
#
## Keep annotations
#-keep @com.github.GoodieBag.pinview.** class * { *; }
#
## Keep specific PinView class
#-keep class com.github.GoodieBag.pinview.PinView { *; }
#
## Keep all classes in io.agora
#-keep class io.agora.** { *; }
#
## Keep annotations
#-keep @io.agora.** class * { *; }
#
## Keep specific Agora Chat SDK classes
#-keep class io.agora.rtc.chat.** { *; }
#
## Keep interfaces
#-keep interface io.agora.** { *; }
#
## Keep reflection classes
#-keep class io.agora.rtc.chat.internal.** { *; }
#
## Keep all Firebase classes
#-keep class com.google.firebase.** { *; }
#
## Keep Firebase Messaging classes
#-keep class com.google.firebase.messaging.** { *; }
#
## Keep annotations
#-keep @com.google.firebase.** class * { *; }
#-keep @com.google.android.gms.common.annotation.Keep class * { *; }
#
## Keep Parcelable classes
#-keep public class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## Keep specific Firebase Messaging classes
#-keep class com.google.firebase.messaging.FirebaseMessaging { *; }
#-keep class com.google.firebase.messaging.RemoteMessage { *; }
#
## Keep all classes in com.android.billingclient
#-keep class com.android.billingclient.** { *; }
#
## Keep annotations
#-keep @com.android.billingclient.** class * { *; }
#
## Keep Parcelable classes
#-keep public class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## Keep specific Billing Client classes
#-keep class com.android.billingclient.api.** { *; }
#
## Keep reflection classes
#-keep class com.android.billingclient.internal.** { *; }
#
#-keep class com.companion.astrodating.** { *; }
#-keep class android.support.v7.widget.SearchView { *; }
## Keep AppCompatSpinner and its related classes
#-keep class androidx.appcompat.widget.AppCompatSpinner { *; }
#-keep class androidx.appcompat.widget.AppCompatSpinner$* { *; }
#
## Keep any custom adapter classes that extend from ArrayAdapter or BaseAdapter
#-keep class * extends androidx.appcompat.widget.AppCompatAdapter { *; }
#-keep class * extends android.widget.ArrayAdapter { *; }
#-keep class * extends android.widget.BaseAdapter { *; }
#
#
#-printmapping mapping.txt
#-printusage usage.txt
#
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#-keep class com.squareup.okhttp.** { *; }
