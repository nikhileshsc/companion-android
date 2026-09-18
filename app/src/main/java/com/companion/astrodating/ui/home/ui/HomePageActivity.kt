package com.companion.astrodating.ui.home.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.base.InAppAlertManager
import com.companion.astrodating.base.InAppEventBus
import com.companion.astrodating.base.InAppAlertEvent
import com.companion.astrodating.databinding.ActivityHomePageBinding
import com.companion.astrodating.ui.chat.ChatActivity
import com.companion.astrodating.ui.home.domain.model.InterestDomain
import com.companion.astrodating.ui.interests.ui.InterestsFragment
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.message.viewmodel.MessageViewModel
import com.companion.astrodating.ui.profile.ui.ProfileFragment
import com.companion.astrodating.ui.profile.viewmodel.ProfileViewModel
import com.companion.astrodating.ui.purchasePlans.GooglePlayPurchaseReconciler
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.states.MessageUiState
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.NotificationTypeConstants
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.UpdateBenefitsConstant
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.setFullscreenWithNavigation
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.Conversation
import javax.inject.Inject

@AndroidEntryPoint
class HomePageActivity : BaseActivity() {
    @Inject lateinit var googlePlayPurchaseReconciler: GooglePlayPurchaseReconciler
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navHostController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private val profileViewModel: ProfileViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    var notificationBody = APP_EMPTY_STRING

    // Unread-message polling lives here (Activity level) rather than in
    // HomeFragment so it keeps running for as long as the app is
    // foregrounded, regardless of which bottom-nav tab is currently showing.
    // -1 means "not measured yet" so the very first check this app session
    // never fires a false "new message" popup before we know the real
    // baseline.
    private var previousUnreadCount = -1
    private val unreadPollHandler = Handler(Looper.getMainLooper())
    private val UNREAD_POLL_INTERVAL_MS = 15000L
    private val unreadPollRunnable = object : Runnable {
        override fun run() {
            refreshUnreadMessages(fireAlertOnIncrease = true)
            unreadPollHandler.postDelayed(this, UNREAD_POLL_INTERVAL_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        setFullscreenWithNavigation(this, window)
        checkForAppUpdates()
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        setupNavigationComponents()
        setupInAppAlerts()
        getIntentData()
        googlePlayPurchaseReconciler.reconcile()
    }

    private fun getIntentData() {

    }

    override fun initObservers() {
        profileViewModel.logoutUser.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
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
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    showLoggedOutDialog(title =  "Delete Data Received.",
                        description = notificationBody) {
                        clearCache()
                        launchScreenAndFinish<SplashActivity>()
                    }

                }
            }
        }
    }

    private fun setupNavigationComponents() {
        bottomNavView = binding.bottomNavigationView
        bottomNavView.itemIconTintList = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragNavHost) as NavHostFragment
        navHostController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(navView, navHostController)

        if (intent != null) {
            intent?.getStringExtra("targetFragment")?.let { targetFragment ->
                val type = intent?.getStringExtra(NotificationTypeConstants.type)
                val bundle = Bundle()
                bundle.putString(NotificationTypeConstants.type, type)
                Log.e(TAG, "NotificationTypeConstants type-$type")
                when (targetFragment) {
                    InterestsFragment::class.java.name -> {
                        if (type == NotificationTypeConstants.receivedInterest) {
                            val interestDomain =
                                InterestDomain(
                                    image = R.drawable.ic_interest_receivedinterest,
                                    title = R.string.text_interest_receivedinterest,
                                    interestType = InterestTypeConstant.receivedInterest
                                )
                            bundle.putString(
                                InterestTypeConstant.interestData,
                                Gson().toJson(interestDomain)
                            )
                            binding.bottomNavigationView.selectedItemId =
                                R.id.interestsFragment
                            navHostController.navigate(R.id.interestUserFragment, bundle)
                        } else if (type == NotificationTypeConstants.declineInterest) {
                            val interestDomain =
                                InterestDomain(
                                    image = R.drawable.ic_interest_declined,
                                    title = R.string.text_interest_declinedlist,
                                    interestType = InterestTypeConstant.declineInterest
                                )
                            bundle.putString(
                                InterestTypeConstant.interestData,
                                Gson().toJson(interestDomain)
                            )
                            binding.bottomNavigationView.selectedItemId =
                                R.id.interestsFragment
                            navHostController.navigate(R.id.interestUserFragment, bundle)
                        }
                    }

                    NotificationTypeConstants.chatMessage -> {
                        // App was killed/backgrounded and the user tapped the
                        // chat push notification. Land on Home and queue the
                        // same in-app alert card shown when the app is
                        // already open, so tapping it opens the exact chat.
                        binding.bottomNavigationView.selectedItemId = R.id.homeFragment
                        val senderId = intent?.getStringExtra("senderId")
                        if (!senderId.isNullOrEmpty()) {
                            InAppEventBus.postAlert(
                                InAppAlertEvent(
                                    type = InAppEventBus.TYPE_CHAT_MESSAGE,
                                    title = intent?.getStringExtra("title") ?: "You have a new message!",
                                    body = intent?.getStringExtra("body") ?: "",
                                    conversationId = senderId,
                                    senderName = intent?.getStringExtra("senderName"),
                                    senderAvatarUrl = intent?.getStringExtra("profileUrl")
                                )
                            )
                        }
                    }

                    ProfileFragment::class.java.name -> launchScreen<ManagePhotosActivity>()
                    NotificationTypeConstants.deleteMyAccount ->{
                        notificationBody = intent?.getStringExtra("body")!!
                        if (isInternetConnection()) {
                            profileViewModel.logoutUser()
                        } else {
                            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                        }
                    }
                    // Handle other fragments as needed
                }
            }
        }
    }

    /**
     * Shows a transient in-app alert card (new message / new interest) while
     * this activity is on screen, and keeps the Interests bottom-nav badge
     * in sync with InAppEventBus. Alerts are queued one at a time - tapping
     * the current one dismisses it, runs its deep link, and advances to the
     * next queued alert (if any).
     */
    private fun setupInAppAlerts() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.inAppAlertContainer) { v, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.setPadding(v.paddingLeft, topInset, v.paddingRight, v.paddingBottom)
            insets
        }

        InAppEventBus.currentAlert.observe(this) { event ->
            event ?: return@observe

            val iconRes = when (event.type) {
                NotificationTypeConstants.receivedInterest -> R.drawable.ic_interest_receivedinterest
                NotificationTypeConstants.declineInterest -> R.drawable.ic_interest_declined
                else -> R.drawable.ic_interest_receivedinterest
            }

            InAppAlertManager.show(
                container = binding.inAppAlertContainer,
                title = event.title,
                body = event.body,
                iconRes = iconRes
            ) {
                handleAlertTap(event)
                // Dismiss already happened (InAppAlertManager does this on
                // click); advance() shows the next queued alert, if any.
                InAppEventBus.advance()
            }
        }

        InAppEventBus.interestBadgeCount.observe(this) { count ->
            val badgeCount = count ?: 0
            if (badgeCount > 0) {
                val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.interestsFragment)
                badge.isVisible = true
                badge.number = badgeCount
            } else {
                binding.bottomNavigationView.removeBadge(R.id.interestsFragment)
            }
        }

        observeChatUnlock()
    }

    /**
     * Recomputes the total unread message count from Agora Chat and updates
     * the Message bottom-nav badge. When [fireAlertOnIncrease] is true and
     * the count has gone up since the last check, also surfaces an in-app
     * "You have a new message!" pop-up. Runs at the Activity level so it
     * keeps working no matter which bottom-nav tab is currently active -
     * this used to live in HomeFragment and only ran while the Home tab
     * itself was resumed.
     */
    private fun refreshUnreadMessages(fireAlertOnIncrease: Boolean) {
        try {
            val chatClient = ChatClient.getInstance()

            if (!chatClient.isLoggedInBefore) {
                Log.d(TAG, "⚠️ User not logged in to Agora Chat yet")
                return
            }

            val chatManager = chatClient.chatManager()
            val conversations = chatManager.allConversations

            var totalUnreadCount = 0
            var latestConversationId: String? = null
            for ((conversationId, conversation) in conversations) {
                totalUnreadCount += conversation.unreadMsgCount
                if (conversation.unreadMsgCount > 0) {
                    latestConversationId = conversationId
                }
            }

            Log.d(TAG, "✅ Total unread messages: $totalUnreadCount from ${conversations.size} conversations")

            updateBottomNavUnreadBadge(totalUnreadCount)

            if (fireAlertOnIncrease && previousUnreadCount in 0 until totalUnreadCount && latestConversationId != null) {
                postNewMessageAlert(chatClient, latestConversationId, conversations[latestConversationId])
            }
            previousUnreadCount = totalUnreadCount

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching unread badge: ${e.message}", e)
        }
    }

    /**
     * Resolves the sender's nickname (same lookup MessageFragment uses to
     * populate the Message list) and posts an in-app alert carrying the
     * conversation id, so tapping it can open that exact chat.
     */
    private fun postNewMessageAlert(chatClient: ChatClient, conversationId: String, conversation: Conversation?) {
        val lastMessageText = try {
            (conversation?.lastMessage?.body as? io.agora.chat.TextMessageBody)?.message
        } catch (e: Exception) {
            null
        } ?: "You have a new message"

        chatClient.userInfoManager().fetchUserInfoByUserId(
            arrayOf(conversationId),
            object : ValueCallBack<Map<String?, io.agora.chat.UserInfo?>?> {
                override fun onSuccess(result: Map<String?, io.agora.chat.UserInfo?>?) {
                    val userInfo = result?.get(conversationId)
                    val senderName = userInfo?.nickname?.takeIf { it.isNotBlank() } ?: conversationId
                    InAppEventBus.postAlert(
                        InAppAlertEvent(
                            type = InAppEventBus.TYPE_CHAT_MESSAGE,
                            title = "You have a new message!",
                            body = "$senderName: $lastMessageText",
                            conversationId = conversationId,
                            senderName = senderName,
                            senderAvatarUrl = userInfo?.avatarUrl
                        )
                    )
                }

                override fun onError(code: Int, error: String?) {
                    InAppEventBus.postAlert(
                        InAppAlertEvent(
                            type = InAppEventBus.TYPE_CHAT_MESSAGE,
                            title = "You have a new message!",
                            body = "$conversationId: $lastMessageText",
                            conversationId = conversationId,
                            senderName = conversationId
                        )
                    )
                }
            }
        )
    }

    private fun updateBottomNavUnreadBadge(unreadCount: Int) {
        Log.d("UNREADCOUNTDEBUG", "Updating badge with count: $unreadCount")

        if (unreadCount > 0) {
            val badge: BadgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.messageFragment)
            badge.isVisible = true
            badge.badgeGravity = BadgeDrawable.TOP_END
            badge.number = unreadCount
            Log.d("UNREADCOUNTDEBUG", "Badge set to: $unreadCount")
        } else {
            if (binding.bottomNavigationView.getBadge(R.id.messageFragment) != null) {
                binding.bottomNavigationView.removeBadge(R.id.messageFragment)
                Log.d("UNREADCOUNTDEBUG", "Badge removed")
            }
        }
    }

    /**
     * Runs the deep link for a tapped alert: opens the exact chat with the
     * sender for a message alert, or the actual received/declined interests
     * list (not just the Interests tab landing screen) for an interest alert.
     */
    private fun handleAlertTap(event: InAppAlertEvent) {
        when (event.type) {
            NotificationTypeConstants.receivedInterest, NotificationTypeConstants.declineInterest -> {
                val interestDomain = if (event.type == NotificationTypeConstants.receivedInterest) {
                    InterestDomain(
                        image = R.drawable.ic_interest_receivedinterest,
                        title = R.string.text_interest_receivedinterest,
                        interestType = InterestTypeConstant.receivedInterest
                    )
                } else {
                    InterestDomain(
                        image = R.drawable.ic_interest_declined,
                        title = R.string.text_interest_declinedlist,
                        interestType = InterestTypeConstant.declineInterest
                    )
                }
                val bundle = Bundle()
                bundle.putString(InterestTypeConstant.interestData, Gson().toJson(interestDomain))
                binding.bottomNavigationView.selectedItemId = R.id.interestsFragment
                navHostController.navigate(R.id.interestUserFragment, bundle)
            }

            InAppEventBus.TYPE_CHAT_MESSAGE -> {
                val conversationId = event.conversationId ?: return
                binding.bottomNavigationView.selectedItemId = R.id.messageFragment
                openChatWith(conversationId, event.senderName ?: conversationId, event.senderAvatarUrl ?: "")
            }
        }
    }

    /**
     * Same "check chat credit, then open ChatActivity" flow MessageFragment
     * uses for a tap in the Message list - reused here so opening a chat
     * from the pop-up behaves identically (paywall / free-chat handling
     * included) instead of bypassing that check.
     */
    private fun openChatWith(userId: String, userName: String, profileUrl: String) {
        val authToken = StorePreferences.getAuthToken().orEmpty()
        if (!isInternetConnection()) {
            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
            return
        }
        messageViewModel.updateBenefitByType(
            authToken,
            UpdateBenefitRequestData(UpdateBenefitsConstant.chatProfiles, userId),
            userId,
            userName,
            profileUrl
        )
    }

    private fun observeChatUnlock() {
        messageViewModel.state.observe(this) { state ->
            when (state) {
                is MessageUiState.Success -> {
                    launchScreen<ChatActivity> {
                        putExtra(SEC_USER_ID, messageViewModel.userId)
                        putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                        putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                    }
                    messageViewModel.resetState()
                }

                is MessageUiState.Error -> {
                    when (state.errorCode) {
                        403 -> {
                            launchScreen<ChatActivity> {
                                putExtra(SEC_USER_ID, messageViewModel.userId)
                                putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                                putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                                putExtra("isFreeUser", true)
                            }
                            messageViewModel.resetState()
                        }
                        402 -> {
                            launchScreen<ChatActivity> {
                                putExtra(SEC_USER_ID, messageViewModel.userId)
                                putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                                putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                                putExtra("isFreeUser", false)
                            }
                            messageViewModel.resetState()
                        }
                        else -> {
                            showErrorDialog(state.error + "")
                            messageViewModel.resetState()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    //    override fun onBackPressed() {
//        // Handle back press to ensure the bottom navigation reflects the current fragment
//        if (navHostController.currentDestination?.id == R.id.homeFragment) {
//            super.onBackPressed() // Exit the app if on home
//        } else {
//            navHostController.popBackStack() // Navigate back in the NavController
//        }
//    }
    override fun onResume() {
        super.onResume()
        googlePlayPurchaseReconciler.reconcile()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
        unreadPollHandler.removeCallbacks(unreadPollRunnable)
        unreadPollHandler.post(unreadPollRunnable)
    }

    override fun onPause() {
        super.onPause()
        unreadPollHandler.removeCallbacks(unreadPollRunnable)
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isImmediateUpdateAllowed
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info, activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: " + result.resultCode);
                // If the update is canceled or fails,
                // you can request to start the update again.
            }

        }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            // Check if the current fragment is HomeFragment
            val currentFragment = fragmentManager.findFragmentById(R.id.fragNavHost)
            if (currentFragment is HomeFragment) {
                binding.bottomNavigationView.selectedItemId =
                    R.id.homeFragment // Set home as selected
            }
        } else {
            super.onBackPressed() // Exit the app if no fragments are in the back stack
        }
    }
}
