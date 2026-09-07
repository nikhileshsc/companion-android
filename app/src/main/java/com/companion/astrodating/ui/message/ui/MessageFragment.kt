package com.companion.astrodating.ui.message.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentMessageBinding
import com.companion.astrodating.ui.chat.ChatActivity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.message.adapter.MessageAdapter
import com.companion.astrodating.ui.message.domain.model.MessageInfo
import com.companion.astrodating.ui.message.viewmodel.GetAgoraTokenDetailsViewModel
import com.companion.astrodating.ui.message.viewmodel.MessageViewModel
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.viewmodel.UpdateBenefitsDetailsViewModel
import com.companion.astrodating.ui.states.MessageUiState
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.AGORA_TAG
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.UpdateBenefitsConstant
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.agora.CallBack
import io.agora.ValueCallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions
import io.agora.chat.Conversation
import io.agora.chat.TextMessageBody
import io.agora.chat.UserInfo
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay



@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var unreadRefreshJob: Job? = null

    lateinit var binding: FragmentMessageBinding

    private val updateBenefitsDetailsViewModel: UpdateBenefitsDetailsViewModel by viewModels()

    private var agoraChatClient: ChatClient? = null
    private var appKey = "611228573#1419625"
    private var primaryUserAgoraChatToken: String = APP_EMPTY_STRING
    private var primaryUserName: String = APP_EMPTY_STRING
    private var secondaryUserName: String = APP_EMPTY_STRING
    private val messageViewModel: MessageViewModel by viewModels()
    private val getAgoraTokenDetailsViewModel: GetAgoraTokenDetailsViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null
    private var localTime: Long = 0L
    private var mConversationMap: Map<String?, Conversation>? = null
    private val messageAdapter by lazy {
        MessageAdapter()
    }
    private var conversationArrayList: ArrayList<Conversation>? = null
    private var messageInfoList: ArrayList<MessageInfo>? = null
    var layoutManager: LinearLayoutManager? = null
    private var authToken: String = APP_EMPTY_STRING
    var userInfoList = arrayListOf<UserInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        conversationArrayList = ArrayList()
        messageInfoList = ArrayList()
        getIntentData()
        initChatSdk()
        initData()
        initObserver()
        handleClickEvents()
        binding.layoutToolbar.ivMessageMore.setOnClickListener { anchor ->
            showMessageMenu(anchor)
        }
    }

    private fun handleClickEvents() {
        Log.d(TAG, "📌 handleClickEvents() called")

        messageAdapter.onItemClick = { item ->
            val userId = item.userId
            val userName = item.userName
            val profileUrl = item.profileUrl

            // Mark conversation as read before launching ChatActivity
            markConversationAsRead(userId)

            if (requireActivity().isInternetConnection()) {
                messageViewModel.updateBenefitByType(
                    authToken,
                    UpdateBenefitRequestData(
                        UpdateBenefitsConstant.chatProfiles,
                        userId
                    ),
                    userId,
                    userName,
                    profileUrl
                )
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
            }
        }
    }

    private fun scheduleServerUnreadRefresh() {
        unreadRefreshJob?.cancel()
        unreadRefreshJob = viewLifecycleOwner.lifecycleScope.launch {
            // 1st refresh (most cases fixed here)
            delay(600)
            fetchConversations()

            // 2nd refresh (covers slow server ack propagation)
            delay(900)
            fetchConversations()
        }
    }


    private fun markConversationAsRead(conversationId: String) {
        val chatClient = agoraChatClient ?: return

        try {
            val chatManager = chatClient.chatManager()

            // ✅ LOCAL FIRST (instant UI)
            chatManager.getConversation(conversationId)?.markAllMessagesAsRead()

            // update local ui model (so list + badge update immediately)
            messageInfoList = messageInfoList?.map { info ->
                if (info.userId == conversationId) info.copy(isUnread = false, unreadMsgCount = "0")
                else info
            }?.let { ArrayList(it) }

            messageInfoList?.let { messageAdapter.submitData(ArrayList(it)) }

            // update badge immediately from local state
            updateBottomNavUnreadBadge(getTotalUnreadCount())

            // ✅ SERVER ACK (async)
            chatManager.ackConversationRead(conversationId)

            // ✅ DROP-IN FIX: delay before fetching unread count from server again
            scheduleServerUnreadRefresh()

            Log.d(TAG, "✅ Marked conversation read (local + server ack): $conversationId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to mark conversation as read", e)
        }
    }


    private fun initChatSdk() {
        loadingDialog!!.showDialog()
        val options = ChatOptions()
        if (appKey.isEmpty()) {
            requireActivity().showShortDurationToast("You need to set your AppKey")
            return
        }
        options.appKey = appKey
        options.requireAck = true
        agoraChatClient = ChatClient.getInstance()
        agoraChatClient!!.init(requireContext(), options)
        agoraChatClient!!.setDebugMode(true)
        loginForChat()
    }

    private fun getIntentData() {
        StorePreferences.getPrimaryUserAgoraChatToken()?.let {
            primaryUserAgoraChatToken = it
        }
        StorePreferences.getPrimaryUserAgoraUserName()?.let {
            primaryUserName = it
        }
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
    }

    private fun initObserver() {
        messageViewModel.allChatConversationList.observe(requireActivity()) { conversationList ->
            Log.e(TAG, "initObservers: chat conversationList size ${conversationList.size}")
            conversationArrayList!!.clear()
            if (conversationList.isNotEmpty()) {
                conversationArrayList!!.addAll(conversationList)
                if (conversationArrayList!!.isEmpty()) {
                    loadingDialog?.hideDialog()
                    binding.rvMessage.hideVisibility()
                    binding.tvNoData.showVisibility()
                    updateBottomNavUnreadBadge(0)
                } else {
                    binding.rvMessage.showVisibility()
                    binding.tvNoData.hideVisibility()
                    updateBottomNavUnreadBadge(getTotalUnreadCount())
                    setMessageData()
                }
            } else {
                loadingDialog?.hideDialog()
                binding.rvMessage.hideVisibility()
                binding.tvNoData.showVisibility()
                updateBottomNavUnreadBadge(0)
            }
        }

        getAgoraTokenDetailsViewModel.state.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog!!.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog!!.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog!!.hideDialog()

                    primaryUserName = it.data.user.userName
                    primaryUserAgoraChatToken = it.data.user.chatToken
                    StorePreferences.savePrimaryUserAgoraUserName(primaryUserName)
                    StorePreferences.savePrimaryUserAgoraChatToken(primaryUserAgoraChatToken)
                    loginForChat()
                }
            }
        }

        messageViewModel.state.observe(viewLifecycleOwner) { it ->
            Log.d(TAG, "🎯 State observer triggered: $it")
            when (it) {
                is MessageUiState.Idle -> {
                    updateBottomNavUnreadBadge(getTotalUnreadCount())
                }
                is MessageUiState.Loading -> {
                    Log.d(TAG, "Loading state")
                }
                is MessageUiState.Success -> {
                    Log.d(TAG, "Success state")
                    requireActivity().launchScreen<ChatActivity> {
                        putExtra(SEC_USER_ID, messageViewModel.userId)
                        putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                        putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                    }
                    messageViewModel.resetState()
                }
                is MessageUiState.Error -> {
                    Log.d(TAG, "Error state: ${it.errorCode}")
                    if (it.errorCode == 403) {
                        requireActivity().launchScreen<ChatActivity> {
                            putExtra(SEC_USER_ID, messageViewModel.userId)
                            putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                            putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                            putExtra("isFreeUser", true)
                        }
                        messageViewModel.resetState()
                    } else if (it.errorCode == 402) {
                        requireActivity().launchScreen<ChatActivity> {
                            putExtra(SEC_USER_ID, messageViewModel.userId)
                            putExtra(SEC_USER_FULL_NAME, messageViewModel.userName)
                            putExtra(SEC_USER_PROFILE, messageViewModel.profileUrl)
                            putExtra("isFreeUser", false)
                        }
                    } else {
                        messageViewModel.resetState()
                    }
                }
            }
        }
    }

    private fun setMessageData() {
        lifecycleScope.launch {
            val userId = arrayOfNulls<String>(conversationArrayList!!.size)

            userInfoList = arrayListOf<UserInfo>()
            for (i in 0..<conversationArrayList!!.size) {
                userId[i] = conversationArrayList!![i].lastMessage.userName
            }

            Log.e(TAG, "userInfoList-${userId.size} , conversationList-${conversationArrayList!!.size}")

            agoraChatClient!!.userInfoManager()
                .fetchUserInfoByUserId(userId, object : ValueCallBack<Map<String?, UserInfo?>?> {
                    override fun onSuccess(p0: Map<String?, UserInfo?>?) {
                        if (p0 != null) {
                            userInfoList = p0.values.filterNotNull().toMutableList() as ArrayList<UserInfo>
                            Log.e(TAG, "conversationList.size-${conversationArrayList!!.size} , userInfoList.size-${userInfoList.size}")

                            if (conversationArrayList!!.size == userInfoList.size) {
                                loadingDialog?.hideDialog()
                                messageInfoList!!.clear()

                                // Use a Set to track already added users to prevent duplicates
                                val addedUserIds = mutableSetOf<String>()

                                for (i in 0..<conversationArrayList!!.size) {
                                    val conversation = conversationArrayList!![i]
                                    val conversationUserId = conversation.lastMessage.userName

                                    // Skip if already added
                                    if (addedUserIds.contains(conversationUserId)) {
                                        Log.d(TAG, "⚠️ Skipping duplicate user: $conversationUserId")
                                        continue
                                    }

                                    // Find matching user info
                                    val userInfo = userInfoList.find { it.userId == conversationUserId }

                                    if (userInfo != null) {
                                        addedUserIds.add(conversationUserId)

                                        Log.e(TAG, "userIs-${userInfo.userId} , conversationList-${conversation.lastMessage.userName}")

                                        messageInfoList!!.add(
                                            MessageInfo(
                                                userInfo.userId,
                                                userInfo.nickname,
                                                (conversation.lastMessage.body as TextMessageBody).message,
                                                conversation.lastMessage.msgTime,
                                                conversation.lastMessage.isUnread,
                                                conversation.unreadMsgCount.toString(),
                                                userInfo.avatarUrl
                                            )
                                        )
                                    }
                                }

                                Log.e(TAG, "messageInfoList-${messageInfoList!!.size} items")

                                if (isAdded) {
                                    requireActivity().runOnUiThread {
                                        messageAdapter.submitData(messageInfoList!!)
                                    }
                                }
                            }
                        }
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e(AGORA_TAG, "onError $p0, $p1")
                    }
                })
        }
    }

    private fun initData() {
        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_dashboard_message)
        binding.layoutToolbar.ivToolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMessage.layoutManager = layoutManager
        binding.rvMessage.adapter = messageAdapter

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                messageAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                messageAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun showMessageMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.message_more_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_mark_all_read -> {
                    markAllConversationsAsRead()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun markAllConversationsAsRead() {
        val chatClient = agoraChatClient ?: return

        try {
            val chatManager = chatClient.chatManager()
            chatManager.markAllConversationsAsRead()

            conversationArrayList = conversationArrayList?.map { conversation ->
                conversation.markAllMessagesAsRead()
                conversation
            }?.let { ArrayList(it) }

            messageInfoList = messageInfoList?.map { info ->
                info.copy(isUnread = false, unreadMsgCount = "0")
            }?.let { ArrayList(it) }

            messageInfoList?.let { updatedList ->
                messageAdapter.submitData(ArrayList(updatedList))
            }

            updateBottomNavUnreadBadge(0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mark all conversations as read", e)
        }
    }

    private fun getTotalUnreadCount(): Int {
        return conversationArrayList?.sumOf { conversation ->
            conversation.unreadMsgCount
        } ?: 0
    }

    private fun updateBottomNavUnreadBadge(unreadCount: Int) {
        if (!isAdded) return

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            ?: return

        Log.d(TAG, "Updating badge with count: $unreadCount")

        if (unreadCount > 0) {
            val badge: BadgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.messageFragment)
            badge.isVisible = true
            badge.badgeGravity = BadgeDrawable.TOP_END

//            badge.setBadgeWithoutTextShapeAppearanceOverlay(R.style.BadgeStyle)
//            val badgePadding = resources.getDimensionPixelSize(R.dimen.badge_dot_padding)
//            badge.horizontalPadding = badgePadding
//            badge.verticalPadding = badgePadding

            badge.number = unreadCount
            Log.d(TAG, "Badge set to: $unreadCount")
//            badge.clearNumber()
//            Log.d("UNREADCOUNTDEBUG", "Badge dot shown for unread messages")
        } else {
            if (bottomNavigationView.getBadge(R.id.messageFragment) != null) {
                bottomNavigationView.removeBadge(R.id.messageFragment)
                Log.d(TAG, "Badge removed")
            }
        }
    }

    private fun loginForChat() {
        if (!agoraChatClient!!.isLoggedInBefore) {
            agoraChatClient!!.loginWithAgoraToken(
                primaryUserName,
                primaryUserAgoraChatToken,
                object : CallBack {
                    override fun onSuccess() {
                        localTime = Calendar.getInstance().time.time
                        Log.e(AGORA_TAG, "onChatLoginSuccess")
                        fetchConversations()
                    }

                    override fun onError(code: Int, error: String) {
                        Log.e(AGORA_TAG, "onError $code, $error")
                        if (code == 200) {
                            fetchConversations()
                        } else if (code == 108) {
                            if (requireContext().isInternetConnection()) {
                                getAgoraTokenDetailsViewModel.getAgoraTokenDetails(authToken)
                            } else {
                                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                            }
                        } else if (code == 303) {
                            loginForChat()
                        } else {
                            requireActivity().runOnUiThread {
                                loadingDialog?.hideDialog()
                                requireActivity().showErrorDialog(getString(R.string.dialog_chat_login_error_message))
                            }
                        }
                    }
                })
        } else {
            Log.e(TAG, "onSuccess inlogin")
            fetchConversations()
        }
    }

    private fun fetchConversations() {
        val cursor = ""
        val limit = 40
        Log.e(TAG, "doAsyncFetchConversationsFromServer api call")
        messageViewModel.doAsyncFetchConversationsFromServer(agoraChatClient!!, limit, cursor)
    }

    fun logoutFromChat() {
        if (agoraChatClient!!.isLoggedInBefore) {
            agoraChatClient!!.logout(false, object : CallBack {
                override fun onSuccess() {
                    Log.e(TAG, "Chat -> onLogoutSuccess ")
                }

                override fun onError(code: Int, error: String) {
                    Log.e(TAG, "Chat -> onLogoutError: $error")
                }
            })
        } else {
            Log.e(TAG, "You were not logged in")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Refreshing badge count")

        if (agoraChatClient != null && agoraChatClient!!.isLoggedInBefore) {
            updateBottomNavUnreadBadge(getTotalUnreadCount())
        }
    }
}
