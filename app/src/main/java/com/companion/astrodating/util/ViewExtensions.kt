package com.companion.astrodating.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.WindowCompat
import com.android.billingclient.api.BillingClient
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import io.agora.chat.ChatClient

internal fun View.showVisibility() {
    this.visibility = View.VISIBLE
}

internal fun View.hideVisibility() {
    this.visibility = View.GONE
}

internal fun View.invisible() {
    this.visibility = View.INVISIBLE
}

internal fun Context.isInternetConnection(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
    } else {
        val activeNetworkInfo = manager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * Centralized image loading. Every Glide call in this app previously had no
 * placeholder (a blank/gray flash was visible until the network image
 * finished downloading) and no target size (full-resolution originals were
 * decoded even for small list thumbnails, adding to app-wide scroll
 * jank/slowness). This fixes both in one place.
 *
 * sizePx caps the decoded bitmap's largest dimension - pass a size roughly
 * matching the ImageView's real on-screen size (small avatars ~150-200,
 * card-sized photos ~500-600, full detail/gallery photos ~900). Existing
 * scaleType/transformations (centerCrop, circleCrop, etc.) on the target
 * ImageView or an existing Glide chain are unaffected - this only adds a
 * placeholder and caps decode size.
 */
fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholderRes: Int = R.drawable.ic_default_profile,
    @DrawableRes errorRes: Int = placeholderRes,
    sizePx: Int = 500
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholderRes)
        .error(errorRes)
        .override(sizePx, sizePx)
        .into(this)
}

/*
internal fun Context.showWarningDialog(
    warningMessage: String,
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_warning, null)
    builder.setCancelable(false)
    val tvWarningDesc: TextView = view.findViewById(R.id.tvWarningDesc)
    tvWarningDesc.text = buildString {
        append(warningMessage)
    }
    val btnWarning: MaterialButton = view.findViewById(R.id.btnWarning)
    btnWarning.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showCommonDialog(
    title: String = "",
    description: String = "",
    btnOkayTitle: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_logged_out, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)

    if(title.isNotEmpty() && description.isNotEmpty()) {
        tvTitle.text = title
        tvDesc.text = description
    }
    if (btnOkayTitle.isNotEmpty()){
        tvOkay.text = btnOkayTitle
    }

    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showKickedOutDialog(
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_kicked_out, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showChatNotAcceptedDialog(
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_chat_not_accepted, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showCoHostRequestDeclinedDialog(
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_co_host_req_declined, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}



internal fun Context.showUnfollowDialog(
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_unfollow, null)
    builder.setCancelable(false)
    val tvUnfollow: TextView = view.findViewById(R.id.tvUnFollow)
    val tvCancel: TextView = view.findViewById(R.id.tvCancel)

    tvUnfollow.setOnClickListener {
        builder.dismiss()
        action()
    }

    tvCancel.setOnClickListener {
        builder.dismiss()
    }

    builder.setView(view)
    builder.show()
}

internal fun Context.showAstrologerLeftDialog(
    title: String = getString(R.string.text_chat_ended),
    descriptionMessage: String = getString(R.string.text_chat_ended_desc),
    action: () -> Unit = {}
) {
    Log.e(TAG, "showAstrologerLeftDialog: ")
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_astrologer_left, null)
    builder.setCancelable(false)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    tvTitle.text = title
    tvDesc.text = descriptionMessage
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showWantToLeftChatDialog(
    actionYes: () -> Unit = {},
    actionNo: () -> Unit = {},
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view =
        LayoutInflater.from(this).inflate(R.layout.dialog_layout_want_to_left_current_chat, null)
    builder.setCancelable(false)
    val tvYes: TextView = view.findViewById(R.id.tvYes)
    val tvNo: TextView = view.findViewById(R.id.tvNo)
    tvYes.setOnClickListener {
        builder.dismiss()
        actionYes()
    }
    tvNo.setOnClickListener {
        builder.dismiss()
        actionNo()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showFreeChatPopup(
    freeChatImageUrl: String,
    onPopupClick: () -> Unit = {},
    onCloseButtonClicked: () -> Unit= {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_free_chat, null)
    val ivFreeChat: ImageView = view.findViewById(R.id.ivFreeChat)
    val ivClose: ImageView = view.findViewById(R.id.ivCloseFreeChat)
    Glide.with(this).load(freeChatImageUrl).error(R.drawable.ic_user_placeholder_solid).into(ivFreeChat)
    builder.setCancelable(false)
    ivFreeChat.setOnClickListener {
        builder.dismiss()
        onPopupClick()
    }

    ivClose.setOnClickListener {
        builder.dismiss()
        onCloseButtonClicked()
    }
    builder.setView(view)
    builder.show()
}



internal fun Context.showProvidedRemedyDialog(
    remedyText: String,
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_show_provided_remedy, null)
    builder.setCancelable(false)
    val tvRemedy: TextView = view.findViewById(R.id.tvRemedy)
    tvRemedy.text = remedyText
    val btnOkay: MaterialButton = view.findViewById(R.id.btnOkay)

    btnOkay.setOnClickListener {
        builder.dismiss()
        action()
    }

    builder.setView(view)
    builder.show()
}

internal fun Context.showLiveWaitListDialog(
    typeOfCall: String,
    title: String = "",
    userProfile: String = "",
    userName: String = "",
    astrologerProfile: String = "",
    astrologerName: String = "",
    waitingTime: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog).create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_live_waitlist, null)
    builder.setCancelable(false)
    val dialogTitle: TextView = view.findViewById(R.id.tvTitle)
    val ivUser: ImageView = view.findViewById(R.id.ivUserProfile)
    val ivWaitListType: ImageView = view.findViewById(R.id.ivWaitListType)
    val ivAstrologer: ImageView = view.findViewById(R.id.ivAstrologerProfile)
    val tvUserName: TextView = view.findViewById(R.id.tvUserName)
    val tvAstrologerName: TextView = view.findViewById(R.id.tvAstrologerName)
    val tvWaitListTime: TextView = view.findViewById(R.id.tvWaitTime)
    val tvConnectingWith: TextView = view.findViewById(R.id.tvConnectingWith)

    if (typeOfCall == LiveViewModel.LIVE_TYPE_OF_CALL_VIDEO) {
        dialogTitle.text = getString(R.string.text_livevideo_call_with_astrologer)
        ivWaitListType.setImageResource(R.drawable.ic_live_wait_list_video_rounded)
    } else {
        dialogTitle.text = getString(R.string.text_livecall_with_astrologer)
        ivWaitListType.setImageResource(R.drawable.ic_live_wait_list_audio_rounded)
    }

    dialogTitle.text = title
    tvUserName.text = userName
    tvAstrologerName.text = astrologerName
    tvConnectingWith.text = buildString {
        append(getString(R.string.text_astrologer_connected_message))
    }
    Glide.with(this).load(userProfile).error(R.drawable.ic_user_placeholder_solid).into(ivUser)
    Glide.with(this).load(astrologerProfile).error(R.drawable.ic_user_placeholder_solid).into(ivAstrologer)
    tvWaitListTime.text = waitingTime
    val btnWait: MaterialButton = view.findViewById(R.id.btnWait)
    btnWait.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}
*/


internal fun View.showShortDurationSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

internal fun View.showLongDurationSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

internal fun Context.showShortDurationToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}

internal fun Context.showLongDurationToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
}

internal inline fun <reified T : Activity> Activity.launchScreen(
    block: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    intent.apply(block)
    startActivity(intent)
}

internal inline fun <reified T : Activity> Activity.launchScreenAndFinish(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
    finish()
}

internal fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

internal fun Context.clearCache() {
    // The Agora Chat SDK keeps its own session + locally cached conversation
    // data independent of our SharedPreferences. If we don't explicitly log
    // it out here, that session/cache from the account that's signing out
    // can still be active when the next account logs into chat on the same
    // device - which is what caused messages appearing to leak between
    // accounts when testing with two logins on one device. unbindToken=true
    // because the account is fully signing out, so its FCM token shouldn't
    // stay bound to this Agora chat user either.
    try {
        if (ChatClient.getInstance().isLoggedInBefore) {
            ChatClient.getInstance().logout(true, null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).apply {
        edit().clear().apply()
    }
    this.cacheDir.deleteRecursively()
}

internal fun Context.showErrorDialog(
    errorMessage: String,
    title: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_error, null)
    builder.setCancelable(false)
    val tvErrorTitle: TextView = view.findViewById(R.id.tvError)
    val tvErrorDesc: TextView = view.findViewById(R.id.tvErrorDesc)
    tvErrorTitle.text = title
    tvErrorDesc.text = buildString {
        append(errorMessage)
    }
    val btnError: MaterialButton = view.findViewById(R.id.btnError)
    btnError.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showSuccessDialog(
    title: String = "",
    successMessage: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_success, null)
    builder.setCancelable(false)
    val tvSuccessLabel: TextView = view.findViewById(R.id.tvSuccessLabel)
    val tvSuccessDesc: TextView = view.findViewById(R.id.tvSuccessDesc)
    tvSuccessDesc.text = successMessage
    tvSuccessLabel.text = buildString {
        append(title)
    }

    val btnOkay: MaterialButton = view.findViewById(R.id.btnOkay)
    btnOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

internal fun Context.showLoggedOutDialog(
    title: String = "",
    description: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_logged_out, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)

    if (title.isNotEmpty() && description.isNotEmpty()) {
        tvTitle.text = title
        tvDesc.text = description
    }

    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

fun setFullscreenWithNavigation(
    context: Context,
    window: Window,
) {
// This is full screen mode for all version
//    window.apply {
//        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        } else {
//            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        }
//        statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
//
//    }

    WindowCompat.setDecorFitsSystemWindows(
        window,
        false
    )

}
internal fun Context.showCommonDialogWithButtons(
    title: String = APP_EMPTY_STRING,
    description: String = APP_EMPTY_STRING,
    btnPositiveText: String = APP_EMPTY_STRING,
    btnNegativeText: String = APP_EMPTY_STRING,
    actionNegative: () -> Unit = {},
    actionPositive: () -> Unit = {},
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_common, null)
    builder.setCancelable(false)

    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    tvTitle.text = title
    tvDesc.text = description

    val tvYes: TextView = view.findViewById(R.id.tvPositive)
    tvYes.text = btnPositiveText
    val tvNo: TextView = view.findViewById(R.id.tvNegative)
    tvNo.text = btnNegativeText
    tvYes.setOnClickListener {
        builder.dismiss()
        actionPositive()
    }
    tvNo.setOnClickListener {
        builder.dismiss()
        actionNegative()
    }
    builder.setView(view)
    builder.show()
}
internal fun Context.showDialog(
    title: String = "",
    description: String = "",
    action: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this, R.style.ThemCustomDialog)
        .create()
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_logged_out, null)
    builder.setCancelable(false)
    val tvOkay: TextView = view.findViewById(R.id.tvOkay)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)

    if (title.isNotEmpty() && description.isNotEmpty()) {
        tvTitle.text = title
        tvDesc.text = description
    }

    tvOkay.setOnClickListener {
        builder.dismiss()
        action()
    }
    builder.setView(view)
    builder.show()
}

fun Int.displayBiilingError(): String{
    if (this == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
        return "BILLING_UNAVAILABLE"
    }else if (this == BillingClient.BillingResponseCode.NETWORK_ERROR){
        return "NETWORK_ERROR"
    }else if (this == BillingClient.BillingResponseCode.ERROR){
        return "ERROR"
    }else if (this == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE){
        return "The billing service is currently unavailable. Please try again later."
    }else if (this == BillingClient.BillingResponseCode.DEVELOPER_ERROR){
        return "There was an error with the purchase. Please contact support."
    }else if (this == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){
        return "SERVICE_DISCONNECTED"
    }else if (this == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE){
        return "ITEM_UNAVAILABLE"
    }else if (this == BillingClient.BillingResponseCode.ITEM_NOT_OWNED){
        return "ITEM_NOT_OWNED"
    }else if (this == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
        return "You already own this item."
    }else if (this == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED){
        return "FEATURE_NOT_SUPPORTED"
    }else if (this == BillingClient.BillingResponseCode.USER_CANCELED){
        return "You canceled the purchase. Please try again."
    }else {
        return "An unknown error occurred. Please try again."
    }
}