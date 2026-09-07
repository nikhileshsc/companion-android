package com.companion.astrodating.ui.notification.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityNotificationBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.notification.adapter.NotificationAdapter
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainEntity
import com.companion.astrodating.ui.notification.viewmodel.NotificationViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.NotificationTypeConstants
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {
    private val binding by lazy {
        ActivityNotificationBinding.inflate(layoutInflater)
    }
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private val notificationAdapter by lazy {
        NotificationAdapter()
    }
    private var notificationList : ArrayList<NotificationDomainEntity>?=null
    var firstVisibleItem = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private var loading = true
    private var previousTotal = 0
    private val visibleThreshold = 5
    var page = 1
    var layoutManager: LinearLayoutManager? = null
    private var authToken: String = APP_EMPTY_STRING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        initData()
        handleClickEvents()

    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun initObservers() {
        notificationViewModel.notificationList.observe(this) {
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
                    notificationList!!.clear()
                    for (item in it.data.notifications){
                        val imageDrawable : Int
                        when(item.notificationType){
                            NotificationTypeConstants.receivedInterest,
                            NotificationTypeConstants.declineInterest->{
                                imageDrawable = R.drawable.ic_notification_interest
                            }
                            NotificationTypeConstants.support->{
                                imageDrawable = R.drawable.ic_notification_contactsupport
                            }
                            NotificationTypeConstants.appUpdate->{
                                imageDrawable = R.drawable.ic_notification_updateapp
                            }
                            NotificationTypeConstants.gallery->{
                                imageDrawable = R.drawable.ic_notification_photoapproved
                            }
                            NotificationTypeConstants.subscription->{
                                imageDrawable = R.drawable.ic_notification_membership
                            }
                            NotificationTypeConstants.server->{
                                imageDrawable = R.drawable.ic_notification_server_maintence
                            }
                            else ->{
                            imageDrawable = R.drawable.ic_notification_photoapproved
                            }
                        }
                        notificationList!!.add(
                            NotificationDomainEntity(
                                item.id,
                                item.body,
                                imageDrawable,
                                item.notificationNo,
                                item.notificationType,
                                item.title,
                                item.user,
                                item.userType
                            )
                        )
                    }
                    if (notificationList!!.isEmpty()) {
                        binding.rvNotification.hideVisibility()
                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvNotification.showVisibility()
                        binding.tvNoData.hideVisibility()
                        notificationAdapter.submitData(notificationList!!)
                    }
                }
            }
        }

        notificationViewModel.moreNotificationList.observe(this) {
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
                    for (item in it.data.notifications){
                        val imageDrawable : Int
                        when(item.notificationType){
                            NotificationTypeConstants.receivedInterest,
                            NotificationTypeConstants.declineInterest->{
                                imageDrawable = R.drawable.ic_notification_interest
                            }
                            NotificationTypeConstants.support->{
                                imageDrawable = R.drawable.ic_notification_contactsupport
                            }
                            NotificationTypeConstants.appUpdate->{
                                imageDrawable = R.drawable.ic_notification_updateapp
                            }
                            NotificationTypeConstants.gallery->{
                                imageDrawable = R.drawable.ic_notification_photoapproved
                            }
                            NotificationTypeConstants.subscription->{
                                imageDrawable = R.drawable.ic_notification_membership
                            }
                            NotificationTypeConstants.server->{
                                imageDrawable = R.drawable.ic_notification_server_maintence
                            }
                            else ->{
                                imageDrawable = R.drawable.ic_notification_photoapproved
                            }
                        }
                        notificationList!!.add(
                            NotificationDomainEntity(
                                item.id,
                                item.body,
                                imageDrawable,
                                item.notificationNo,
                                item.notificationType,
                                item.title,
                                item.user,
                                item.userType
                            )
                        )
                    }
                    if (notificationList!!.isEmpty()) {
//                        binding.rvNotifications.hideVisibility()
//                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvNotification.showVisibility()
                        binding.tvNoData.hideVisibility()
                        notificationAdapter.submitData(notificationList!!)
                    }
                }
            }
        }

    }
    private fun initData() {

        binding.layoutToolbar.ivBack.showVisibility()
        binding.layoutToolbar.toolbarTitle.text = getString(R.string.text_notification_title)

        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        notificationList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotification.layoutManager = layoutManager
        binding.rvNotification.adapter = notificationAdapter
        if (isInternetConnection()) {
            notificationViewModel.getNotificationDetails(page)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

        binding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = binding.rvNotification.childCount
                totalItemCount = layoutManager!!.itemCount
                firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    // End has been reached
                    runOnUiThread(Runnable {
                        if (isInternetConnection()) {
                            notificationViewModel.getMoreNotificationDetails(pageNumber = ++page)
                        }
                    })
                    // Do something
                    loading = true
                }
            }
        })
    }

}