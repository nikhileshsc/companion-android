package com.companion.astrodating.ui.chat

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityChatBinding
import com.companion.astrodating.ui.call.ui.VideoCallActivity
import com.companion.astrodating.ui.call.ui.VoiceCallActivity
import com.companion.astrodating.ui.chat.viewHolder.ReceiverMessageHolder
import com.companion.astrodating.ui.chat.viewHolder.SenderMessageHolder
import com.companion.astrodating.ui.chat.viewmodel.ChatViewModel
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.viewmodel.InterestsViewModel
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.message.viewmodel.GetAgoraTokenDetailsViewModel
import com.companion.astrodating.ui.profileDetails.ui.ProfileDetailsActivity
import com.companion.astrodating.ui.purchasePlans.ui.PurchasePlansActivity
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryUserDetailsDomainEntity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.AGORA_TAG
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.USER_ID
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.currentTimeToLong
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showCommonDialogWithButtons
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationToast
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.MessageListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatOptions
import io.agora.chat.Conversation
import io.agora.chat.CustomMessageBody
import io.agora.chat.FetchMessageOption
import io.agora.chat.TextMessageBody
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar


@AndroidEntryPoint
class ChatActivity : BaseActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    private var isFreeUser: Boolean = false
    private var loadingDialog: LoadingDialog? = null
    private var agoraChatClient: ChatClient? = null
    private val interestViewModel: InterestsViewModel by viewModels()
    private val getAgoraTokenDetailsViewModel: GetAgoraTokenDetailsViewModel by viewModels()
    private var primaryUserAgoraChatToken: String = APP_EMPTY_STRING
    private var primaryAgoraUserId: String = APP_EMPTY_STRING
    private var interestType: String = APP_EMPTY_STRING
    private var secondaryUserId: String = APP_EMPTY_STRING
    private var secondaryUserFullName: String = APP_EMPTY_STRING
    private var secondaryUserProfile: String = APP_EMPTY_STRING
    private var primaryUserDetails: GetMandatoryUserDetailsDomainEntity? = null
    private var appKey = "611228573#1419625"
    private val chatAdapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }
    private var localTime: Long = 0L
    private val chatViewModel: ChatViewModel by viewModels()
    private var authToken: String = APP_EMPTY_STRING

    companion object {
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private const val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        getIntentData()
        initRecyclerView()
        initChatSdk()
        handleClickEvents()

        Log.d("UNREADCOUNTDEBUG", secondaryUserId)

        val conversationId = intent.getStringExtra(SEC_USER_ID)
        if (conversationId != null) {
            Log.d("UNREADCOUNTDEBUG", "Marking conversation as read for: $conversationId")
            // Give a small delay to ensure messages are loaded first
            Handler(Looper.getMainLooper()).postDelayed({
                markConversationAsRead(conversationId)
            }, 500)
        }

        if (isFreeUser) {
            MaterialAlertDialogBuilder(this@ChatActivity, R.style.RoundedAlertDialog)
                .setTitle("Free Message Limit")
                .setMessage("You have 3 free messages to send to this user. To remove this limit, please upgrade your plan.")
                .setCancelable(false)
                .setPositiveButton("Upgrade") { _, _ ->
                    val intent = Intent(this@ChatActivity, PurchasePlansActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Close", null)
                .show()
        }
    }

    private fun markConversationAsRead(conversationId: String) {
        val chatClient = ChatClient.getInstance()

        if(chatClient.isLoggedInBefore){
            try {
                val chatManager = chatClient.chatManager()
                // markAllConversationsAsRead marks all messages in all conversations as read
                val conversation = chatManager.getConversation(conversationId)
                conversation?.markAllMessagesAsRead()
                chatManager.ackConversationRead(conversationId)
//            chatManager.markAllConversationsAsRead()
                Log.d("UNREADCOUNTDEBUG", "✅ Conversation $conversationId marked as read")
            } catch (e: Exception) {
                Log.e("UNREADCOUNTDEBUG", "Exception marking conversations as read", e)
            }
        }
    }

    private fun showAlert(
        title: String? = null,
        message: String,
        positiveText: String? = null,
        negativeText: String? = null,
        cancelable: Boolean = true,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(this@ChatActivity, R.style.RoundedAlertDialog)
            .apply {
                if (!title.isNullOrBlank()) setTitle(title)
                setMessage(message)
                setCancelable(cancelable)
                if (!positiveText.isNullOrBlank()) {
                    setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
                }
                if (!negativeText.isNullOrBlank()) {
                    setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
                }
            }
            .show()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.more_menu_options, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_block -> {
                    showCommonDialogWithButtons(
                        title = getString(R.string.dialog_block_user_title),
                        description = getString(R.string.dialog_block_user_desc),
                        btnPositiveText = getString(R.string.text_yes),
                        btnNegativeText = getString(R.string.text_no),
                        actionPositive = {
                            if (isInternetConnection()) {
                                interestType = InterestTypeConstant.block
                                interestViewModel.updateInterestDetails(
                                    InterestRequestData(
                                        InterestTypeConstant.block,
                                        secondaryUserId
                                    )
                                )
                            } else {
                                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                            }
                        }
                    )
                    true
                }                      // Handle edit action
                else -> false
            }
        }
        popup.show() // Show the popup menu
    }

    private fun initData() {
        initChatMessagesListener()
        loginForChat()

    }

    private fun handleClickEvents() {
        binding.ivSend.setOnClickListener(this)
        binding.tvTitle.setOnClickListener(this)
        binding.ivToolbarMore.setOnClickListener(this)
        binding.ivToolbarBack.setOnClickListener(this)
        binding.callVideo.setOnClickListener(this)
        binding.callVoice.setOnClickListener(this)
//        binding.
    }

    private fun initRecyclerView() {
        binding.rvChatMsgs.adapter = chatAdapter
    }

    private fun getIntentData() {
//        StorePreferences.getAgoraChatAppKey()?.let {
//            appKey = it
//        }
        StorePreferences.getPrimaryUserAgoraChatToken()?.let {
            primaryUserAgoraChatToken = it
        }
        StorePreferences.getPrimaryUserAgoraUserName()?.let {
            primaryAgoraUserId = it
        }
        StorePreferences.getUserDetails().let {
            primaryUserDetails = it
        }
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (intent != null) {
            secondaryUserId = intent.getStringExtra(SEC_USER_ID) ?: APP_EMPTY_STRING
            secondaryUserFullName = intent.getStringExtra(SEC_USER_FULL_NAME) ?: APP_EMPTY_STRING
            secondaryUserProfile = intent.getStringExtra(SEC_USER_PROFILE) ?: APP_EMPTY_STRING
            isFreeUser = intent.getBooleanExtra("isFreeUser", false)
            if (secondaryUserFullName.isNotEmpty()) {
                binding.tvTitle.text = secondaryUserFullName
            }
        }
    }

    private fun launchProfileActivity(profileUrl: String?) {
        launchScreen<ProfileDetailsActivity> {
            putExtra(USER_ID, secondaryUserId)
        }
    }

    override fun initObservers() {
        chatViewModel.getAllChatConversationHistory.observe(this) { chatMessages ->
            loadingDialog?.hideDialog()
            Log.e(
                AGORA_TAG,
                "initObservers: chat message size ${chatMessages.size} , $chatMessages"
            )
            if (chatMessages.isNotEmpty()) {
                if (chatMessages != null) {
                    for (message in chatMessages) {
                        if (message.from == primaryAgoraUserId && message.type == ChatMessage.Type.TXT) {
                            //ChatMessage.Type.VOICE
                            val url = primaryUserDetails?.profileUrl ?: ""
                            chatAdapter.add(
                                SenderMessageHolder(
                                    (message.body as TextMessageBody).message,
                                    message.msgTime,
                                    url
                                )
                            )
                        } else if (message.from == secondaryUserId && message.type == ChatMessage.Type.TXT) {
                            chatAdapter.add(
                                ReceiverMessageHolder(
                                    (message.body as TextMessageBody).message,
                                    message.msgTime,
                                    secondaryUserProfile,
                                ) { profileUrl ->
                                    launchProfileActivity(profileUrl)
                                }
                            )
                        }/*else if (message.type == ChatMessage.Type.IMAGE) {
                            chatAdapter.add(
                                ImageSenderMessageHolder(
                                    (message.body as ImageMessageBody).remoteUrl,
                                    message.msgTime,
                                    this@ChatHistoryDetailActivity
                                )
                            )
                        } */
                    }
                } else {
                    binding.tvNoChatConversation.showVisibility()
                }
            } else {
                binding.tvNoChatConversation.showVisibility()
            }
        }

        interestViewModel.updateInterestState.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog!!.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog!!.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                is UiState.Success -> {
                    loadingDialog!!.hideDialog()
                    if (interestType == InterestTypeConstant.sendInterest) {
                        showLongDurationToast(getString(R.string.text_sent_interest_success_msg))
                    } else if (interestType == InterestTypeConstant.shortlist) {
                        showLongDurationToast(getString(R.string.text_shortlist_profile_success_msg))
                    } else if (interestType == InterestTypeConstant.block) {
                        showLongDurationToast(getString(R.string.text_block_profile_success_msg))
                    }
                }
            }
        }
        getAgoraTokenDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog!!.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog!!.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog!!.hideDialog()

                    primaryAgoraUserId = it.data.user.userName
                    primaryUserAgoraChatToken = it.data.user.chatToken
                    StorePreferences.savePrimaryUserAgoraUserName(primaryAgoraUserId)
                    StorePreferences.savePrimaryUserAgoraChatToken(primaryUserAgoraChatToken)
                    loginForChat()

                }
            }
        }


    }


    private fun initChatSdk() {
        val options = ChatOptions()
        if (appKey.isEmpty()) {
            showShortDurationToast("You need to set your AppKey")
            return
        }
        options.appKey = appKey// Set your app key in options
        options.requireAck = true
        agoraChatClient = ChatClient.getInstance()
        agoraChatClient!!.init(this, options) // Initialize the ChatClient
        agoraChatClient!!.setDebugMode(true) // Enable debug info output
        initData()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivSend -> {
                val msg = binding.etMsgContent.text.toString()
                if (msg.isNotEmpty()) {
                    sendMessage(msg)
                }
                binding.etMsgContent.text.clear()
            }

            binding.ivToolbarBack -> {
                logoutFromChat()
            }

            binding.ivToolbarBack -> {
                showPopupMenu(binding.ivToolbarBack)
            }

            binding.tvTitle -> {
                launchProfileActivity(secondaryUserProfile)
            }

            binding.callVideo -> {
                sendVideoCall()
//                launchVideoCall()
            }

            binding.callVoice -> {
                sendVoiceCall()
//                launchVoiceCall()
            }
        }
    }


    private fun showUpgradeDialog(msg: String) {
        showAlert(
            title = "Upgrade plan",
            message = msg,
            positiveText = "Upgrade",
            negativeText = "Cancel",
            cancelable = false,
            onPositive = {
                startActivity(Intent(this@ChatActivity, PurchasePlansActivity::class.java))
            }
        )
    }

    private fun showCannotSendDialog(msg: String?) {
        showAlert(
            title = "Cannot send call",
            message = msg ?: "Something went wrong",
            positiveText = "Upgrade",
            negativeText = "Cancel",
            cancelable = false,
            onPositive = {
                startActivity(Intent(this@ChatActivity, PurchasePlansActivity::class.java))
            }
        )
    }

    // --- Launchers (modified) ---
    private fun launchVideoCall() {
        launchScreen<VideoCallActivity> {
            putExtra(VideoCallActivity.EXTRA_FLOW, "out")
            putExtra(VideoCallActivity.EXTRA_CALLEE_ID, secondaryUserId)
            putExtra(VideoCallActivity.EXTRA_PRIMARY_ID, primaryAgoraUserId)
            putExtra(VideoCallActivity.EXTRA_CALLEE_NAME, secondaryUserFullName)
        }
    }

    private fun launchVoiceCall() {
        launchScreen<VoiceCallActivity> {
            putExtra(VoiceCallActivity.EXTRA_FLOW, "out")
            putExtra(VoiceCallActivity.EXTRA_CALLEE_ID, secondaryUserId)
            putExtra(VoiceCallActivity.EXTRA_PRIMARY_ID, primaryAgoraUserId)
            putExtra(VoiceCallActivity.EXTRA_CALLEE_NAME, secondaryUserFullName)
            putExtra(VoiceCallActivity.EXTRA_CALLEE_PROFILE, secondaryUserProfile)
        }
    }

    // --- Your original entry points, simplified ---
// VIDEO
    private fun sendVideoCall() {
        chatViewModel.checkIfCallCanBeSent { allowed, message ->
            if (allowed) {
                launchVideoCall()
            } else {
                if (isFreeUser) {
                    showUpgradeDialog("Please upgrade to a paid plan to send audio and video calls.")
                } else {
                    showCannotSendDialog(message)
                }
            }
        }
    }

    // VOICE
    private fun sendVoiceCall() {
        chatViewModel.checkIfCallCanBeSent { allowed, message ->
            if (allowed) {
                launchVoiceCall()
            } else {
                if (isFreeUser) {
                    showUpgradeDialog("Please upgrade to a paid plan to send audio and video calls.")
                } else {
                    showCannotSendDialog(message)
                }
            }
        }
    }


    private fun containsContactInfo(message: String): Boolean {
        val phoneRegex =
            Regex("\\b(\\+?\\d{1,3}[-.\\s]?)?(\\(?\\d{3,5}\\)?[-.\\s]?\\d{3,5}[-.\\s]?\\d{3,5})\\b")
        val emailRegex = Regex("\\b[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}\\b")
        val linkRegex = Regex("https?://\\S+|www\\.\\S+")
        val social = Regex("#\"@[a-zA-Z0-9_]{2,}\"#,")

        return phoneRegex.containsMatchIn(message) ||
                emailRegex.containsMatchIn(message) ||
                linkRegex.containsMatchIn(message) ||
                social.containsMatchIn(message)
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    private val callPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

//    private val permissionsLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//            val granted = callPermissions.all { perm ->
//                result[perm] == true ||
//                        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
//            }
//            if (granted) {
//                launchCall()
//            } else {
//                // skip silently, or show a toast if you want
//                 Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    private fun ensurePermissionsThen(action: () -> Unit) {
//        val missing = callPermissions.filter {
//            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//        }
//        if (missing.isEmpty()) {
//            action()                // already granted → go
//        } else {
//            permissionsLauncher.launch(missing.toTypedArray())  // request → callback decides
//        }
//    }


    private fun sendMessage(message: String) {
        print("API Calling body: ")
        print(primaryAgoraUserId)
        print(",")
        print(secondaryUserId)

//        val chatMessage = ChatMessage.createTextSendMessage(message, secondaryUserId)
//
//        chatMessage.setMessageStatusCallback(object : CallBack {
//            override fun onSuccess() {
//                Log.e(AGORA_TAG, "Message Sent Successfully ")
//                runOnUiThread {
//                    val url = primaryUserDetails?.profileUrl ?: ""
//                    chatAdapter.add(
//                        SenderMessageHolder(message, currentTimeToLong(), url)
//                    )
//                    binding.rvChatMsgs.scrollToPosition(chatAdapter.itemCount - 1)
//                }
//            }
//
//            override fun onError(code: Int, error: String) {
//                Log.e(AGORA_TAG, "Error while sending message: $error")
//            }
//        })
//        Log.e(AGORA_TAG, "sendMessage to:  $secondaryUserId")
//        val extObject = JSONObject()
//        try {
//            extObject.put(
//                " em_push_title ",
//                "You have got a message"
//            ) // Custom push message title. This field is a built-in field and the field name cannot be modified.
//            extObject.put(
//                " em_push_content ",
//                "${primaryUserDetails!!.fullName} sent you a message on Companion AstroDating"
//            ) // Custom push message content. This field is a built-in field and the field name cannot be modified.
//            chatMessage.setAttribute("em_apns_ext", extObject)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        agoraChatClient!!.chatManager().sendMessage(chatMessage)
//
//        chatViewModel.sendMessagePushNotification(secondaryUserId, message)

        chatViewModel.checkIfMessageCanBeSent(primaryAgoraUserId, secondaryUserId) { allowed, msg ->
            if (allowed) {
                if (containsContactInfo(message) && isFreeUser) {
                    showAlert(
                        "Free Plan",
                        "Sharing phone numbers, emails, or links is restricted in free plan. Please upgrade to continue.",
                        "Upgrade",
                        "Close",
                        onPositive = {
                            startActivity(
                                Intent(
                                    this@ChatActivity,
                                    PurchasePlansActivity::class.java
                                )
                            )
                        }
                    )
//                    MaterialAlertDialogBuilder(this@ChatActivity, R.style.RoundedAlertDialog)
//                        .setTitle("Free Plan")
//                        .setMessage("Sharing phone numbers, emails, or links is restricted in free plan. Please upgrade to continue.")
//                        .setCancelable(false)
//                        .setPositiveButton("Upgrade") { _, _ ->
//                            val intent = Intent(this@ChatActivity, PurchasePlansActivity::class.java)
//                            startActivity(intent)
//                        }
//                        .setNegativeButton("Close", null)
//                        .show()
                    return@checkIfMessageCanBeSent
                }

                val chatMessage = ChatMessage.createTextSendMessage(message, secondaryUserId)

                chatMessage.setMessageStatusCallback(object : CallBack {
                    override fun onSuccess() {
                        Log.e(AGORA_TAG, "Message Sent Successfully ")
                        runOnUiThread {
                            val url = primaryUserDetails?.profileUrl ?: ""
                            chatAdapter.add(
                                SenderMessageHolder(message, currentTimeToLong(), url)
                            )
                            binding.rvChatMsgs.scrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }

                    override fun onError(code: Int, error: String) {
                        Log.e(AGORA_TAG, "Error while sending message: $error")
                    }
                })
                Log.e(AGORA_TAG, "sendMessage to:  $secondaryUserId")
                val extObject = JSONObject()
                try {
                    extObject.put(
                        " em_push_title ",
                        "You have got a message"
                    ) // Custom push message title. This field is a built-in field and the field name cannot be modified.
                    extObject.put(
                        " em_push_content ",
                        "${primaryUserDetails!!.fullName} sent you a message on Companion AstroDating"
                    ) // Custom push message content. This field is a built-in field and the field name cannot be modified.
                    chatMessage.setAttribute("em_apns_ext", extObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                agoraChatClient!!.chatManager().sendMessage(chatMessage)

                chatViewModel.sendMessagePushNotification(secondaryUserId, message)
            } else {
//                Toast.makeText(this, msg ?: "You can't send more messages", Toast.LENGTH_SHORT).show()
                if (isFreeUser) {
                    showAlert(
                        "Limit Reached",
                        "Since you are a free user, you can only send 3 messages to this user.",
                        "Upgrade",
                        "Close",
                        onPositive = {
                            startActivity(
                                Intent(
                                    this@ChatActivity,
                                    PurchasePlansActivity::class.java
                                )
                            )
                        }
                    )
//                    MaterialAlertDialogBuilder(this@ChatActivity, R.style.RoundedAlertDialog)
//                        .setTitle("Limit Reached")
//                        .setMessage("Since you are a free user, you can only send 3 messages to this user.")
//                        .setCancelable(false)
//                        .setPositiveButton("Upgrade") { _, _ ->
//                            val intent =
//                                Intent(this@ChatActivity, PurchasePlansActivity::class.java)
//                            startActivity(intent)
//                        }
//                        .setNegativeButton("Cancel", null)
//                        .show()
                } else {
                    showAlert(
                        "Limit Reached",
                        "All benefits have been exhausted. Please upgrade your plan to enjoy more benefits.",
                        "Upgrade",
                        "Close",
                        onPositive = {
                            startActivity(
                                Intent(
                                    this@ChatActivity,
                                    PurchasePlansActivity::class.java
                                )
                            )
                        }
                    )
//                    MaterialAlertDialogBuilder(this@ChatActivity, R.style.RoundedAlertDialog)
//                        .setTitle("Limit Reached")
//                        .setMessage("All benefits have been exhausted. Please upgrade your plan to enjoy more benefits.")
//                        .setCancelable(false)
//                        .setPositiveButton("Upgrade") { _, _ ->
//                            val intent =
//                                Intent(this@ChatActivity, PurchasePlansActivity::class.java)
//                            startActivity(intent)
//                        }
//                        .setNegativeButton("Cancel", null)
//                        .show()
                }
            }
        }


    }

    private fun loginForChat() {
        agoraChatClient!!.loginWithAgoraToken(
            primaryAgoraUserId,
            primaryUserAgoraChatToken,
            object : CallBack {
                override fun onSuccess() {
                    localTime = Calendar.getInstance().time.time
                    Log.e(AGORA_TAG, "onChatLoginSuccess")
                    getChatHistory(secondaryUserId)
//                    clearAllMessages()

                }

                override fun onError(code: Int, error: String) {
                    if (code == 200) {
                        // The user is already logged in
                        getChatHistory(secondaryUserId)
                    } else if (code == 108) {
                        // 108, Token expired
                        if (isInternetConnection()) {
                            getAgoraTokenDetailsViewModel.getAgoraTokenDetails(authToken)

                        } else {
                            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                        }
                    } else {
                        runOnUiThread {
                            loadingDialog?.hideDialog()
                            showErrorDialog(getString(R.string.dialog_chat_login_error_message)) {
//                            chatRequestTimer.cancel()
//                            finish()
                            }
                        }
                    }
                }
            })
    }

    /* private fun getMessage() {
         val cursor = ""
 //        limit: The number of conversations that you expect to get on each page. The value range is [1,50].
         val limit = 40
         val conversations = ArrayList<Conversation>();
         doAsyncFetchConversationsFromServer(limit, cursor, conversations);
         Log.e("message list", "===---------$conversations")


     }

     private fun doAsyncFetchConversationsFromServer(
         limit: Int,
         cursor: String,
         conversations: MutableList<Conversation>
     ) {
         ChatClient.getInstance().chatManager().asyncFetchConversationsFromServer(
             limit,
             cursor,
             object : ValueCallBack<CursorResult<Conversation>?> {
                 override fun onSuccess(value: CursorResult<Conversation>?) {
                     if (value != null) {
                         val list = value.data
                         if (list != null && list.size > 0) {
                             conversations.addAll(list)
                         }
                         Log.e("message list", "===$conversations")
                         val newCursor = value.cursor
                         if (!TextUtils.isEmpty(newCursor)) {
                             doAsyncFetchConversationsFromServer(limit, newCursor, conversations)
                         }
                     }
                 }

                 override fun onError(error: Int, errorMsg: String) {}
             })
     }
 */

    private fun getChatHistory(conversationId: String?) {
        val type = Conversation.ConversationType.Chat
        val option = FetchMessageOption().apply {
            setDirection(Conversation.SearchDirection.DOWN)
        }
        val pageSize = 40
        val cursor = ""
        val messages: MutableList<ChatMessage> = ArrayList()
        chatViewModel.fetchChatHistoryMessages(
            agoraChatClient!!,
            conversationId!!,
            type,
            pageSize,
            cursor,
            option
        )
    }

    private fun initChatMessagesListener() {
        agoraChatClient!!.chatManager().addMessageListener(messageListener)

        agoraChatClient!!.addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                Log.e(AGORA_TAG, "onConnected")
            }

            override fun onDisconnected(error: Int) {
                Log.e(AGORA_TAG, "onDisconnected: $error")
            }

            override fun onLogout(errorCode: Int) {
                Log.e(AGORA_TAG, "User needs to log out: $errorCode")
                ChatClient.getInstance().logout(false, null)
            }

            override fun onTokenExpired() {
                Log.e(AGORA_TAG, "ConnectionListener onTokenExpired")
                if (isInternetConnection()) {
                    getAgoraTokenDetailsViewModel.getAgoraTokenDetails(authToken)

                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }
            }

            override fun onTokenWillExpire() {
                Log.e(AGORA_TAG, "ConnectionListener onTokenWillExpire")

                if (isInternetConnection()) {
                    getAgoraTokenDetailsViewModel.getAgoraTokenDetails(authToken)

                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }
            }
        })
    }

    private val messageListener = object : MessageListener {

        override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
            Log.e(AGORA_TAG, "ChatActivity → onMessageReceived")

            messages?.forEach { message ->
                runOnUiThread {
                    val body = message.body
                    if (body is TextMessageBody) {
                        val text = body.message
                        val time = message.msgTime

                        if (message.from == primaryAgoraUserId) {
                            val url = primaryUserDetails?.profileUrl ?: ""
                            chatAdapter.add(SenderMessageHolder(text, time, url))
                        } else {
                            chatAdapter.add(ReceiverMessageHolder(text, time, secondaryUserProfile))
                        }

                        binding.rvChatMsgs.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        }

        override fun onCmdMessageReceived(messages: MutableList<ChatMessage>?) {
            Log.e(AGORA_TAG, "ChatActivity → onCmdMessageReceived")

            messages?.forEach { message ->
                val body = message.body
                if (body is CustomMessageBody) {
                    val action = body.event()

                    Log.d(AGORA_TAG, "CMD Event: $action")

                    val diff = localTime - message.msgTime
                    if (diff >= 0) {
                        Log.d(AGORA_TAG, "Old CMD message received, ignored")
                        return@forEach
                    }

                    when (action) {
                        "userOnOtherSideLeftChat" -> {
                            runOnUiThread {
                                if (!this@ChatActivity.isFinishing) {
                                    logoutFromChat()
                                    // Optionally trigger next screen
                                }
                            }
                        }

                        "astrologerDidJoinChat" -> {
                            runOnUiThread {
                                Log.d(AGORA_TAG, "Astrologer joined")
                                loadingDialog?.hideDialog()
                            }
                        }

                        else -> {
                            Log.w(AGORA_TAG, "Unknown CMD event: $action")
                        }
                    }

                } else {
                    Log.w(
                        AGORA_TAG,
                        "Received CMD message with unexpected body: ${body.javaClass.simpleName}"
                    )
                }
            }
        }
    }


    private fun logoutFromChat() {
        if (agoraChatClient!!.isLoggedInBefore) {
            agoraChatClient!!.logout(false, object : CallBack {
                override fun onSuccess() {
                    Log.e(AGORA_TAG, "Chat -> onLogoutSuccess ")
                }

                override fun onError(code: Int, error: String) {
                    Log.e(AGORA_TAG, "Chat -> onLogoutError: $error")
                }
            })
        } else {
            Log.e(AGORA_TAG, "You were not logged in")
        }
        finish()
    }

    private fun clearAllMessages() {
        val clearServerData = true
        agoraChatClient!!.chatManager()
            .asyncDeleteAllMsgsAndConversations(clearServerData, object : CallBack {
                override fun onSuccess() {
                    Log.e(AGORA_TAG, "conversation deleted")
                }

                override fun onError(code: Int, error: String) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutFromChat()
    }


    /*   private fun setupListeners() {
           // Add message event callbacks
           agoraChatClient!!.chatManager().addMessageListener { messages ->
               for (message in messages) {
                   runOnUiThread {
                       displayMessage(
                           (message.body as TextMessageBody).message,
                           false
                       )
                   }
                   showLog(
                       "Received a " + message.type.name
                               + " message from " + message.from
                   )
               }
           }

           // Add connection event callbacks
           agoraChatClient!!.addConnectionListener(object : ConnectionListener {
               override fun onConnected() {
                   showLog("Connected")
               }

               override fun onDisconnected(error: Int) {
                   if (isJoined) {
                       showLog("Disconnected: $error")
                       isJoined = false
                   }
               }

               override fun onLogout(errorCode: Int) {
                   showLog("User logging out: $errorCode")
               }

               override fun onTokenExpired() {
                   // The token has expired
               }

               override fun onTokenWillExpire() {
                   // The token is about to expire. Get a new token
                   // from the token server and renew the token.
               }
           })
       }


       fun joinLeave(view: View?) {
   //        val button = binding.btnJoinLeave
           if (isJoined) {
               agoraChatClient!!.logout(true, object : CallBack {
                   override fun onSuccess() {
                       showLog("Sign out success!")
   //                    runOnUiThread { button.text = "Join" }
                       isJoined = false
                   }

                   override fun onError(code: Int, error: String) {
                       showLog(error)
                   }
               })
           } else {
               agoraChatClient!!.loginWithAgoraToken(primaryUserName, token, object : CallBack {
                   override fun onSuccess() {
                       showLog("Signed in")
                       isJoined = true
                       getChatHistory(secondaryUserName)
   //                    runOnUiThread { button.text = "Leave" }
                   }

                   override fun onError(code: Int, error: String) {
                       if (code == 200) { // Already joined
                           isJoined = true
   //                        runOnUiThread { button.text = "Leave" }
                       } else {
                           showLog(error)
                       }
                   }
               })
           }
       }

       fun sendMessage(view: View?) {
           // Read the recipient name from the EditText box
   //        val toSendName =
   //            (binding.etRecipient as EditText).text.toString().trim { it <= ' ' }
           val content = binding.etMsgContent!!.text.toString().trim { it <= ' ' }
   //        if (toSendName.isEmpty() || content.isEmpty()) {
   //            showLog("Enter a recipient name and a message")
   //            return
   //        }

           // Create a ChatMessage
           val message = ChatMessage.createTextSendMessage(content, secondaryUserName)

           // Set the message callback before sending the message
           message.setMessageStatusCallback(object : CallBack {
               override fun onSuccess() {
                   showLog("Message sent")
                   runOnUiThread {
                       displayMessage(content, true)
                       // Clear the box and hide the keyboard after sending the message
                       binding.etMsgContent.setText("")
                       val inputMethodManager =
                           getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                       inputMethodManager.hideSoftInputFromWindow(
                           binding.etMsgContent.applicationWindowToken,
                           0
                       )
                   }
               }

               override fun onError(code: Int, error: String) {
                   showLog(error)
               }
           })

           // Send the message
           agoraChatClient!!.chatManager().sendMessage(message)
       }

       fun displayMessage(messageText: String?, isSentMessage: Boolean) {
           // Create a new TextView
           val messageTextView = TextView(this)
           messageTextView.text = messageText
           messageTextView.setPadding(10, 10, 10, 10)

           // Set formatting
           val messageList = binding.messageList
           val params = LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
           )
           if (isSentMessage) {
               params.gravity = Gravity.END
               messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"))
               params.setMargins(100, 25, 15, 5)
           } else {
               messageTextView.setBackgroundColor(Color.parseColor("white"))
               params.setMargins(15, 25, 100, 5)
           }

           // Add the message TextView to the LinearLayout
           messageList.addView(messageTextView, params)
       }

       private fun showLog(text: String) {
           // Show a toast message
           runOnUiThread {
               Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
           }

           // Write log
           Log.d("AgoraChatQuickStart", text)
       }

       private fun getChatHistory(conversationId: String?) {
           val type = Conversation.ConversationType.Chat
           val option = FetchMessageOption().apply {
               setDirection(Conversation.SearchDirection.DOWN)
           }
           val pageSize = 40
           val cursor = ""
           val messages: MutableList<ChatMessage> = ArrayList()
           fetchChatHistoryMessages(conversationId!!, type, pageSize, cursor, option)
       }

       fun fetchChatHistoryMessages(
           conversationId: String,
           type: Conversation.ConversationType,
           pageSize: Int, cursor: String,
           option: FetchMessageOption
       ) {
           ChatClient.getInstance().chatManager().asyncFetchHistoryMessages(
               conversationId,
               type,
               pageSize,
               cursor,
               option,
               object : ValueCallBack<CursorResult<ChatMessage>?> {
                   override fun onSuccess(value: CursorResult<ChatMessage>?) {
                       if (value != null) {
                           val list = value.data
                           showLog("chat history list -$list")
                           if (list != null && list.size > 0) {
                               chatHistoryMessages.addAll(list)
                           }
                           showLog("chat history-$chatHistoryMessages")
                           val newCursor = value.cursor
                           if (!cursor.equals("undefined", true)) {
                               if (!TextUtils.isEmpty(newCursor)) {
                                   fetchChatHistoryMessages(
                                       conversationId,
                                       type,
                                       pageSize,
                                       newCursor,
                                       option
                                   )
                               }
                           }
                       }

                   }

                   override fun onError(error: Int, errorMsg: String) {

                   }
               })
       }
       */

}