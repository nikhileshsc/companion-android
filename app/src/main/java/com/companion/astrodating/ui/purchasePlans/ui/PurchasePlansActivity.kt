package com.companion.astrodating.ui.purchasePlans.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1DomainEntity
import com.companion.astrodating.databinding.ActivityPurchasePlansBinding
import com.companion.astrodating.ui.chat.ChatActivity
import com.companion.astrodating.ui.completeanalysis.ui.CompleteAnalysisActivity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.policy.PrivacyPolicyActivity
import com.companion.astrodating.ui.purchasePlans.data.requestData.AndroidInAppPurchaseRes
import com.companion.astrodating.ui.purchasePlans.data.requestData.CreateInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.CreateInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.CurrentSubscriptionDomainEntity
import com.companion.astrodating.ui.purchasePlans.domain.model.PlanDomainEntity
import com.companion.astrodating.ui.purchasePlans.viewmodel.GetPurchasePlanDetailsViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.support.ui.ContactSupportActivity
import com.companion.astrodating.ui.termsofuse.TermsOfUseActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P1_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P2_DATA
import com.companion.astrodating.util.DASHAGUNA_MILAN_DATA
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.FacebookAppEvent
import com.companion.astrodating.util.InAppSubscriptionTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.MANGLIK_DOSHA_DATA
import com.companion.astrodating.util.PRODUCT_ID_CompanionSubscription
import com.companion.astrodating.util.PurchasePlanNameConstant
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.URL_PRIVACY_POLICY
import com.companion.astrodating.util.URL_TERMS_AND_CONDITION
import com.companion.astrodating.util.USER_1
import com.companion.astrodating.util.USER_2
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.displayBiilingError
import com.companion.astrodating.util.facebookLogEvent
import com.companion.astrodating.util.fromChat
import com.companion.astrodating.util.fromUnlockCompleteAnalysis
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showCommonDialogWithButtons
import com.companion.astrodating.util.showDialog
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showLongDurationToast
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class PurchasePlansActivity : BaseActivity(), BillingClientStateListener, PurchasesUpdatedListener,
    View.OnClickListener {

    private val binding by lazy {
        ActivityPurchasePlansBinding.inflate(layoutInflater)
    }
    private var billingClient: BillingClient? = null
    private var subscriptionBasePlanList: ArrayList<ProductDetails.SubscriptionOfferDetails>? = null

    private var subscriptionBasePlanMap: MutableMap<String, ProductDetails.SubscriptionOfferDetails> = mutableMapOf()

    private var productDetailList = mutableListOf<ProductDetails>()
    private var planDetailList: List<PlanDomainEntity>? = null
    private var currentSubscriptionDomainEntity: CurrentSubscriptionDomainEntity? = null
    private val getPurchasePlanDetailsViewModel: GetPurchasePlanDetailsViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private var authToken: String = APP_EMPTY_STRING
    private var planName: String = APP_EMPTY_STRING
    private var currentSubscriptionPlanName: String = APP_EMPTY_STRING
    private var BILLING_CLIENT_TAG = "BillingClient"
    private var createInAppSubscriptionDomainDetails: CreateInAppSubscriptionDomainDetails? = null
    private var planType = APP_EMPTY_STRING
    private var purchaseToken = APP_EMPTY_STRING
    private val processingPurchaseTokens = mutableSetOf<String>()
    private var userInitiatedPurchase = false
    private var secUserId = APP_EMPTY_STRING
    private var secUserFullName = APP_EMPTY_STRING
    private var secUserProfile = APP_EMPTY_STRING
    private var from = APP_EMPTY_STRING
    private var ashtakootMillanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    private var dashakootMilanDomainData: DashakootMilanDomainDetails.DashakootMilanDataDomain? =
        null
    private var p1DomainEntity: P1DomainEntity? = null
    private var p2DomainEntity: P1DomainEntity? = null
    private var manglikDoshaDomainEntity: ManglikDoshaDomainEntity? = null
    private var user2: String = APP_EMPTY_STRING
    private var user1: String = APP_EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        getIntentData()
        productDetailList = ArrayList()
        planDetailList = ArrayList()
        subscriptionBasePlanList = ArrayList()
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .setListener(this)
            .build()
        billingClient?.startConnection(this)
        initViewData()
        Log.e(BILLING_CLIENT_TAG, "Error setting up billing: ${billingClient!!.connectionState}")
        handleClickEvents()
        Log.e("PLANDEBUG", "PurchasePlansActivity CREATED")

    }

    private fun handleClickEvents() {
        binding.cvWeeklyPlan.setOnClickListener(this)
        binding.cvMonthlyPlan.setOnClickListener(this)
        binding.cvYearlyPlan.setOnClickListener(this)
        binding.ivAshtakootAnalysisInfo.setOnClickListener(this)
        binding.ivDashkootAnalysisInfo.setOnClickListener(this)
        binding.tvTermsOfUse.setOnClickListener(this)
        binding.tvPrivacyPolicy.setOnClickListener(this)
        binding.tvNeedHelp.setOnClickListener(this)
    }

    private fun getIntentData() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (intent != null) {
            from = intent.getStringExtra(FROM) ?: APP_EMPTY_STRING
            secUserId = intent.getStringExtra(SEC_USER_ID) ?: APP_EMPTY_STRING
            secUserFullName = intent.getStringExtra(SEC_USER_FULL_NAME) ?: APP_EMPTY_STRING
            secUserProfile = intent.getStringExtra(SEC_USER_PROFILE) ?: APP_EMPTY_STRING

            ashtakootMillanDomainData = Gson().fromJson(
                intent.getStringExtra(ASHTAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                AshtakootMilanDomainDetails.AshtakootMilanDomain::class.java
            )

            dashakootMilanDomainData = Gson().fromJson(
                intent.getStringExtra(DASHAGUNA_MILAN_DATA) ?: APP_EMPTY_STRING,
                DashakootMilanDomainDetails.DashakootMilanDataDomain::class.java
            )

            p1DomainEntity = Gson().fromJson(
                intent.getStringExtra(ASTROLOGY_DETAILS_P1_DATA) ?: APP_EMPTY_STRING,
                P1DomainEntity::class.java
            )

            p2DomainEntity = Gson().fromJson(
                intent.getStringExtra(ASTROLOGY_DETAILS_P2_DATA) ?: APP_EMPTY_STRING,
                P1DomainEntity::class.java
            )

            manglikDoshaDomainEntity = Gson().fromJson(
                intent.getStringExtra(MANGLIK_DOSHA_DATA) ?: APP_EMPTY_STRING,
                ManglikDoshaDomainEntity::class.java
            )

            user1 = intent.getStringExtra(USER_1) ?: APP_EMPTY_STRING
            user2 = intent.getStringExtra(USER_2) ?: APP_EMPTY_STRING

        }

    }

    private fun initViewData() {

        if (isInternetConnection()) {
            getPurchasePlanDetailsViewModel.getPurchasePlanDetails(authToken)
        } else {
            binding.root.showLongDurationSnackBar(
                resources.getString(
                    R.string.text_no_internet_connection
                )
            )

        }

    }

    override fun initObservers() {
        getPurchasePlanDetailsViewModel.state.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    processingPurchaseTokens.clear()
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
                    loadingDialog.hideDialog()
                    try {
                        planDetailList = it.data.plans
                        if (planDetailList != null && planDetailList!!.isNotEmpty()) {
                            if (planDetailList!![0] != null) {
                                val weeklyPlanDetails = planDetailList!![0]
                                print("PLANDEBUG, ${weeklyPlanDetails.planName}")
                                binding.tvWeeklyPlanTitle.text = weeklyPlanDetails.planName
                                if (weeklyPlanDetails.benefitsDescriptions[0] != null && weeklyPlanDetails.benefitsDescriptions[0].isNotEmpty()) {
                                    binding.tvWeeklyReport.text =
                                        weeklyPlanDetails.benefitsDescriptions[0]
                                }
                                if (weeklyPlanDetails.benefitsDescriptions[1] != null && weeklyPlanDetails.benefitsDescriptions[1].isNotEmpty()) {
                                    binding.tvWeeklyUnlockChat.text =
                                        weeklyPlanDetails.benefitsDescriptions[1]
                                }
//                                if (weeklyPlanDetails.benefitsDescriptions.size > 2){
//                                    binding.tvWeeklyAudioVideoCalls.text =
//                                        weeklyPlanDetails.benefitsDescriptions[2]
//                                }
                                binding.tvWeeklyPlanAmount.text = weeklyPlanDetails.price.toString()
                            }

                            if (planDetailList!![1] != null) {
                                val monthlyPlanDetails = planDetailList!![1]
                                print("PLANDEBUG, $monthlyPlanDetails")
                                binding.tvMonthlyPlanTitle.text = monthlyPlanDetails.planName
                                if (monthlyPlanDetails.benefitsDescriptions[0] != null && monthlyPlanDetails.benefitsDescriptions[0].isNotEmpty()) {
                                    binding.tvMonthlyReport.text =
                                        monthlyPlanDetails.benefitsDescriptions[0]
                                }
                                if (monthlyPlanDetails.benefitsDescriptions[1] != null && monthlyPlanDetails.benefitsDescriptions[1].isNotEmpty()) {
                                    binding.tvMonthlyUnlockChat.text =
                                        monthlyPlanDetails.benefitsDescriptions[1]
                                }
//                                if (monthlyPlanDetails.benefitsDescriptions.size > 2){
//                                    binding.tvMonthlyAudioVideoCalls.text =
//                                        monthlyPlanDetails.benefitsDescriptions[2]
//                                }
                                binding.tvMonthlyPlanAmount.text =
                                    monthlyPlanDetails.price.toString()
                            }

                            if (planDetailList!![2] != null) {
                                val yearlyPlanDetails = planDetailList!![2]
                                binding.tvYearlyPlanTitle.text = yearlyPlanDetails.planName
                                if (yearlyPlanDetails.benefitsDescriptions[0] != null && yearlyPlanDetails.benefitsDescriptions[0].isNotEmpty()) {
                                    binding.tvYearlyReport.text =
                                        yearlyPlanDetails.benefitsDescriptions[0]
                                }
                                if (yearlyPlanDetails.benefitsDescriptions[1] != null && yearlyPlanDetails.benefitsDescriptions[1].isNotEmpty()) {
                                    binding.tvYearlyUnlockChat.text =
                                        yearlyPlanDetails.benefitsDescriptions[1]
                                }
//                                if (yearlyPlanDetails.benefitsDescriptions.size > 2){
//                                    binding.tvYearlyAudioVideoCalls.text =
//                                        yearlyPlanDetails.benefitsDescriptions[2]
//                                }
                                binding.tvYearlyPlanAmount.text = yearlyPlanDetails.price.toString()
                            }
                        }
                        currentSubscriptionDomainEntity = it.data.currentSubcription
                        if (currentSubscriptionDomainEntity != null) {
                            if (currentSubscriptionDomainEntity!!.id.isNotEmpty()) {
                                purchaseToken =
                                    currentSubscriptionDomainEntity!!.androidInAppPurchaseRes.purchaseToken
                                for (plans in planDetailList!!) {
                                    if (plans.id == currentSubscriptionDomainEntity!!.subscribedPlan) {
                                        if (plans.id == currentSubscriptionDomainEntity!!.subscribedPlan) {
                                            currentSubscriptionPlanName = plans.planName
                                            if (plans.planName == PurchasePlanNameConstant.weekly) {
                                                binding.tvWeeklyPlanStatus.showVisibility()
                                                binding.tvMonthlyPlanStatus.hideVisibility()
                                                binding.tvYearlyPlanStatus.hideVisibility()

                                            } else if (plans.planName == PurchasePlanNameConstant.monthly) {
                                                binding.tvMonthlyPlanStatus.showVisibility()
                                                binding.tvWeeklyPlanStatus.hideVisibility()
                                                binding.tvYearlyPlanStatus.hideVisibility()

                                            } else if (plans.planName == PurchasePlanNameConstant.yearly) {
                                                binding.tvYearlyPlanStatus.showVisibility()
                                                binding.tvMonthlyPlanStatus.hideVisibility()
                                                binding.tvWeeklyPlanStatus.hideVisibility()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        getPurchasePlanDetailsViewModel.createInAppSubscriptionState.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

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

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    try {
                        createInAppSubscriptionDomainDetails = it.data
//                        if (planName == PurchasePlanNameConstant.weekly) {
//                            if (subscriptionBasePlanList != null && subscriptionBasePlanList!!.size > 0) {
//                                launchBillingFlow(
//                                    productDetailList[0],
//                                    subscriptionBasePlanList!![0]
//                                )
//                            }
//                        } else if (planName == PurchasePlanNameConstant.monthly) {
//                            if (subscriptionBasePlanList != null && subscriptionBasePlanList!!.size > 0) {
//                                launchBillingFlow(
//                                    productDetailList[0],
//                                    subscriptionBasePlanList!![1]
//                                )
//                            }
//                        } else if (planName == PurchasePlanNameConstant.yearly) {
//                            if (subscriptionBasePlanList != null && subscriptionBasePlanList!!.size > 0) {
//                                launchBillingFlow(
//                                    productDetailList[0],
//                                    subscriptionBasePlanList!![2]
//                                )
//                            }
//                        }
                        val basePlanId = when (planName) {
                            PurchasePlanNameConstant.weekly  -> "weekly"
                            PurchasePlanNameConstant.monthly -> "monthly"
                            PurchasePlanNameConstant.yearly  -> "yearly"
                            else -> null
                        }
                        basePlanId?.let { id ->
                            val offerDetails = subscriptionBasePlanMap[id]
                            if (offerDetails != null && productDetailList.isNotEmpty()) {
                                launchBillingFlow(productDetailList[0], offerDetails)
                            } else {
                                showShortDurationToast("Plan not found")
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        getPurchasePlanDetailsViewModel.verifyInAppSubscriptionState.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    processingPurchaseTokens.clear()
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
                    loadingDialog.hideDialog()
                    try {
                        if (it.data.statusCode == 200) {
                            processingPurchaseTokens.clear()
                            StorePreferences.saveSubscriptionData(null)
                            getPurchasePlanDetailsViewModel.getPurchasePlanDetails(authToken)
                            if (!userInitiatedPurchase) return@observe
                            userInitiatedPurchase = false
                            showDialog("Success", "Your membership is successful.", action = {
                                facebookLogEvent(this, FacebookAppEvent.subscription)
                                if (from == fromChat) {
                                    launchScreen<ChatActivity> {
                                        putExtra(SEC_USER_ID, secUserId)
                                        putExtra(SEC_USER_FULL_NAME, secUserFullName)
                                        putExtra(SEC_USER_PROFILE, secUserProfile)
                                    }
                                } else if (from == fromUnlockCompleteAnalysis) {
                                    launchScreen<CompleteAnalysisActivity> {
                                        putExtra(
                                            ASHTAGUNA_MILAN_DATA,
                                            Gson().toJson(ashtakootMillanDomainData)
                                        )
                                        putExtra(
                                            DASHAGUNA_MILAN_DATA,
                                            Gson().toJson(dashakootMilanDomainData)
                                        )
                                        putExtra(
                                            ASTROLOGY_DETAILS_P1_DATA,
                                            Gson().toJson(p1DomainEntity)
                                        )
                                        putExtra(
                                            ASTROLOGY_DETAILS_P2_DATA,
                                            Gson().toJson(p2DomainEntity)
                                        )
                                        putExtra(
                                            MANGLIK_DOSHA_DATA,
                                            Gson().toJson(manglikDoshaDomainEntity)
                                        )
                                        putExtra(USER_1, user1)
                                        putExtra(USER_2, user2)
                                    }


                                } else {
                                    finish()
                                }

                            })
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.e(BILLING_CLIENT_TAG, "purchase response code: ${billingResult.responseCode}")

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.e(BILLING_CLIENT_TAG, "purchase list: ${purchases}")
            for (purchase in purchases) {
                lifecycleScope.launch {
                    verifyPurchaseWithBackend(purchase)
                }

            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle user cancellation
        } else {
            // Handle other error codes
            showShortDurationToast(billingResult.responseCode.displayBiilingError())
        }

    }

    private suspend fun verifyPurchaseWithBackend(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            Log.w(BILLING_CLIENT_TAG, "purchase ignored: state=${purchase.purchaseState}")
            return
        }
        if (!processingPurchaseTokens.add(purchase.purchaseToken)) return
        if (!isInternetConnection()) {
            processingPurchaseTokens.remove(purchase.purchaseToken)
            binding.root.showLongDurationSnackBar(getString(R.string.text_no_internet_connection))
            return
        }
        val pending = createInAppSubscriptionDomainDetails?.subscription
        Log.d(BILLING_CLIENT_TAG, "purchase verification requested: product=${purchase.products.firstOrNull()}, token=${purchase.purchaseToken.take(6)}…")
        getPurchasePlanDetailsViewModel.verifyInAppSubscriptionDetails(
            authToken,
            VerifyInAppSubscriptionRequestData(
                subscribedPlan = pending?.subscribedPlan,
                subscriptionId = pending?.id,
                type = if (pending == null) "restore" else planType,
                purchaseToken = purchase.purchaseToken,
                productId = purchase.products.firstOrNull()
            )
        )
    }

    fun queryProducts() {
        lifecycleScope.launch {
            queryProducts(BillingClient.ProductType.SUBS, PRODUCT_ID_CompanionSubscription)
        }
    }

    private suspend fun queryProducts(productType: String, productId: String) {
        val queryProductDetailsParam = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            )
            .build()
        Log.e(BILLING_CLIENT_TAG, "queryProductDetailsParam: ${queryProductDetailsParam}")
        withContext(Dispatchers.IO) {
            billingClient?.queryProductDetailsAsync(queryProductDetailsParam) { billingResult, productDetailsList ->

                // call api
                Log.e(
                    BILLING_CLIENT_TAG,
                    "productDetailsList: ${productDetailsList}, size -${productDetailsList.size}, billingResult -${billingResult.responseCode}"
                )

                when (billingResult.responseCode) {

                    BillingClient.BillingResponseCode.OK -> {

                        if (productDetailsList?.isNotEmpty() == true) {
                            for (product in productDetailsList) {
                                if (product.subscriptionOfferDetails != null && product.subscriptionOfferDetails!!.size > 0) {
                                    for (subscriptionOfferDetails in product.subscriptionOfferDetails!!) {
                                        subscriptionBasePlanMap[subscriptionOfferDetails.basePlanId] = subscriptionOfferDetails
                                        subscriptionBasePlanList!!.add(subscriptionOfferDetails)
                                    }
                                }
                            }
                            productDetailList!!.addAll(productDetailsList)
                            Log.e(
                                BILLING_CLIENT_TAG,
                                "subscriptionBasePlanList: $subscriptionBasePlanList "
                            )
                        } else {
                            showShortDurationToast("No such products")
                        }
                    }

                    else -> {
                        showShortDurationToast(billingResult.responseCode.displayBiilingError())
                    }
                }
            }
        }
    }

    fun launchBillingFlow(
        productDetails: ProductDetails,
        subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails
    ) {
        Log.e(
            BILLING_CLIENT_TAG,
            "launchBillingFlow: productDetails ${productDetails?.productId}, offerToken-${subscriptionOfferDetails.offerToken}  "
        )

        val productDetailsParamsList = listOf(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(subscriptionOfferDetails.offerToken.toString())
                .build()
        )

        val flowParams: BillingFlowParams
        if (purchaseToken != null && purchaseToken.isNotEmpty()) {
            flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList) // Replace with the actual SkuDetails object
                .setSubscriptionUpdateParams(
                    BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                        .setOldPurchaseToken(purchaseToken)
                        .setSubscriptionReplacementMode(
                            CHARGE_FULL_PRICE
                        )
                        .build()
                ).build()
        } else {
            flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList) // Replace with the actual SkuDetails object
                .build()
        }
        if (billingClient?.isReady == false) {
            showShortDurationToast("Billing client is not ready. Please try again")
        }

        userInitiatedPurchase = true
        val billingResult = billingClient?.launchBillingFlow(this, flowParams)
        Log.e(BILLING_CLIENT_TAG, "launchBillingFlow: ${billingResult?.responseCode} ")

        when (val responseCode = billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // call back to server
                Log.e(
                    BILLING_CLIENT_TAG,
                    "launchBillingFlow ON SUCCESS: ${billingResult.responseCode} "
                )

            }

            else -> {
                userInitiatedPurchase = false
                showShortDurationToast(responseCode!!.displayBiilingError())
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        billingClient?.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        Log.e(BILLING_CLIENT_TAG, "onBillingSetupFinished: " + responseCode);

//        showShortDurationToast("onBillingSetupFinished -$responseCode")

        if (responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e(BILLING_CLIENT_TAG, "BillingResponseCode.OK " + billingResult);
            queryProducts()
            queryPurchases()
        } else {
            Log.e(
                BILLING_CLIENT_TAG,
                "Error setting up billing: " + billingResult.getDebugMessage()
            );

            showLongDurationToast(responseCode.displayBiilingError())
        }
    }

    private fun queryPurchases() {
        if (billingClient?.isReady == false) {
            showShortDurationToast("Billing client not ready")
            return
        }
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient?.queryPurchasesAsync(queryPurchaseParams) { billingResult, productDetailList ->
            when (val responseCode = billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (productDetailList.isNotEmpty()) {
                        for (purchase in productDetailList) {
                            lifecycleScope.launch {
                                verifyPurchaseWithBackend(purchase)
                            }
                        }
                    } else {
                        showShortDurationToast("No product found")
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingClient?.isReady == true) {
            billingClient?.endConnection()
        }
    }

    override fun onResume() {
        super.onResume()
        queryPurchases()
    }

    override fun onClick(p0: View?) {

        Log.d("PLANDEBUG","Click detected")
        when (p0) {
            binding.cvWeeklyPlan -> {
                try{
                    Log.d("PLANDEBUG", "planList size: ${planDetailList?.size}")
                    Log.d("PLANDEBUG", "currentSub: $currentSubscriptionDomainEntity")
                    Log.d("PLANDEBUG","Weekly plan click")
                    if (planDetailList!![0] != null && planDetailList!![0].planName == binding.tvWeeklyPlanTitle.text.toString()) {
                        planName = planDetailList!![0].planName
                        if (currentSubscriptionDomainEntity!!.subscribedPlan != null && currentSubscriptionDomainEntity!!.subscribedPlan.isNotEmpty()) {
                            if (currentSubscriptionDomainEntity!!.subscribedPlan == planDetailList!![0].id) {
                                // resubscribe
                                if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                    planType = InAppSubscriptionTypeConstant.resubscribe
                                    showCommonDialogWithButtons(
                                        title = getString(R.string.text_iap_resubscribe_title),
                                        description = getString(R.string.text_iap_resubscribe_desc),
                                        btnPositiveText = getString(R.string.text_yes),
                                        btnNegativeText = getString(R.string.text_no),
                                        actionPositive = {
                                            if (isInternetConnection()) {
                                                if (isInternetConnection()) {
                                                    val requestData =
                                                        CreateInAppSubscriptionRequestData(
                                                            planPurchasedFrom = "android",
                                                            promoCode = APP_EMPTY_STRING,
                                                            promocodeDiscount = 0,
                                                            subscribedPlan = planDetailList!![0].id,
                                                            subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                            type = planType
                                                        )
                                                    getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                        authToken,
                                                        requestData
                                                    )
                                                } else {
                                                    binding.root.showLongDurationSnackBar(
                                                        resources.getString(
                                                            R.string.text_no_internet_connection
                                                        )
                                                    )
                                                }
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }

                                        }
                                    )
                                }
                            } else {
                                // downgrade
//                                if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                    planType = InAppSubscriptionTypeConstant.downgrade
                                    showCommonDialogWithButtons(
                                        title = getString(R.string.text_iap_downgrade_title),
                                        description = getString(R.string.text_iap_downgrade_desc),
                                        btnPositiveText = getString(R.string.text_yes),
                                        btnNegativeText = getString(R.string.text_no),
                                        actionPositive = {
                                            if (isInternetConnection()) {
                                                if (isInternetConnection()) {
                                                    val requestData =
                                                        CreateInAppSubscriptionRequestData(
                                                            planPurchasedFrom = "android",
                                                            promoCode = APP_EMPTY_STRING,
                                                            promocodeDiscount = 0,
                                                            subscribedPlan = planDetailList!![0].id,
                                                            subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                            type = planType
                                                        )
                                                    getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                        authToken,
                                                        requestData
                                                    )
                                                } else {
                                                    binding.root.showLongDurationSnackBar(
                                                        resources.getString(
                                                            R.string.text_no_internet_connection
                                                        )
                                                    )
                                                }
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }

                                        }
                                    )
//                                }

                            }
                        } else {
                            // new purchase
                            if (isInternetConnection()) {
                                planType = InAppSubscriptionTypeConstant.newPurchase
                                val requestData = CreateInAppSubscriptionRequestData(
                                    planPurchasedFrom = "android",
                                    promoCode = APP_EMPTY_STRING,
                                    promocodeDiscount = 0,
                                    subscribedPlan = planDetailList!![0].id,
                                    subscriptionId = "",
                                    type = planType
                                )
                                getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                    authToken,
                                    requestData
                                )
                            } else {
                                binding.root.showLongDurationSnackBar(
                                    resources.getString(
                                        R.string.text_no_internet_connection
                                    )
                                )

                            }
                        }
                    }
                }catch (e: Exception) {
                    Log.e("PLANDEBUG", "Error in weekly click", e)
                }
            }

            binding.cvMonthlyPlan -> {
                if (planDetailList!![1] != null && planDetailList!![1].planName == binding.tvMonthlyPlanTitle.text.toString()) {
                    planName = planDetailList!![1].planName

                    if (currentSubscriptionPlanName == PurchasePlanNameConstant.weekly) {
                        // weekly to monthly - upgrade
                        if (currentSubscriptionDomainEntity!!.subscribedPlan != null && currentSubscriptionDomainEntity!!.subscribedPlan.isNotEmpty()) {
                            if (currentSubscriptionDomainEntity!!.subscribedPlan != planDetailList!![1].id) {
//                                if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                    planType = InAppSubscriptionTypeConstant.upgrade
                                    showCommonDialogWithButtons(
                                        title = getString(R.string.text_iap_upgrade_title),
                                        description = getString(R.string.text_iap_upgrade_desc),
                                        btnPositiveText = getString(R.string.text_yes),
                                        btnNegativeText = getString(R.string.text_no),
                                        actionPositive = {
                                            if (isInternetConnection()) {
                                                if (isInternetConnection()) {
                                                    val requestData =
                                                        CreateInAppSubscriptionRequestData(
                                                            planPurchasedFrom = "android",
                                                            promoCode = APP_EMPTY_STRING,
                                                            promocodeDiscount = 0,
                                                            subscribedPlan = planDetailList!![1].id,
                                                            subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                            type = planType
                                                        )
                                                    getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                        authToken,
                                                        requestData
                                                    )
                                                } else {
                                                    binding.root.showLongDurationSnackBar(
                                                        resources.getString(
                                                            R.string.text_no_internet_connection
                                                        )
                                                    )
                                                }
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }

                                        }
                                    )
//                                }
                            }
                        }


                    } else if (currentSubscriptionPlanName == PurchasePlanNameConstant.monthly) {
                        // monthly to monthly - resubscribe

                        if (currentSubscriptionDomainEntity!!.subscribedPlan != null && currentSubscriptionDomainEntity!!.subscribedPlan.isNotEmpty()) {
                            if (currentSubscriptionDomainEntity!!.subscribedPlan == planDetailList!![1].id) {
                                // resubscribe
//                                if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                    planType = InAppSubscriptionTypeConstant.resubscribe
                                    showCommonDialogWithButtons(
                                        title = getString(R.string.text_iap_resubscribe_title),
                                        description = getString(R.string.text_iap_resubscribe_desc),
                                        btnPositiveText = getString(R.string.text_yes),
                                        btnNegativeText = getString(R.string.text_no),
                                        actionPositive = {
                                            if (isInternetConnection()) {
                                                if (isInternetConnection()) {
                                                    val requestData =
                                                        CreateInAppSubscriptionRequestData(
                                                            planPurchasedFrom = "android",
                                                            promoCode = APP_EMPTY_STRING,
                                                            promocodeDiscount = 0,
                                                            subscribedPlan = planDetailList!![1].id,
                                                            subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                            type = planType
                                                        )
                                                    getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                        authToken,
                                                        requestData
                                                    )
                                                } else {
                                                    binding.root.showLongDurationSnackBar(
                                                        resources.getString(
                                                            R.string.text_no_internet_connection
                                                        )
                                                    )
                                                }
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }

                                        }
                                    )
//                                }
                            }
                        }


                    } else if (currentSubscriptionPlanName == PurchasePlanNameConstant.yearly) {
                        // yearly to monthly - downgrade
                        if (currentSubscriptionDomainEntity!!.subscribedPlan != null && currentSubscriptionDomainEntity!!.subscribedPlan.isNotEmpty()) {
                            if (currentSubscriptionDomainEntity!!.subscribedPlan != planDetailList!![1].id) {
//                                if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                    planType = InAppSubscriptionTypeConstant.downgrade
                                    showCommonDialogWithButtons(
                                        title = getString(R.string.text_iap_downgrade_title),
                                        description = getString(R.string.text_iap_downgrade_desc),
                                        btnPositiveText = getString(R.string.text_yes),
                                        btnNegativeText = getString(R.string.text_no),
                                        actionPositive = {
                                            if (isInternetConnection()) {
                                                if (isInternetConnection()) {
                                                    val requestData =
                                                        CreateInAppSubscriptionRequestData(
                                                            planPurchasedFrom = "android",
                                                            promoCode = APP_EMPTY_STRING,
                                                            promocodeDiscount = 0,
                                                            subscribedPlan = planDetailList!![1].id,
                                                            subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                            type = planType
                                                        )
                                                    getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                        authToken,
                                                        requestData
                                                    )
                                                } else {
                                                    binding.root.showLongDurationSnackBar(
                                                        resources.getString(
                                                            R.string.text_no_internet_connection
                                                        )
                                                    )
                                                }
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }

                                        }
                                    )
//                                }
                            }
                        }
                    } else {
                        // new purchase
                        if (isInternetConnection()) {
                            planType = InAppSubscriptionTypeConstant.newPurchase
                            val requestData = CreateInAppSubscriptionRequestData(
                                planPurchasedFrom = "android",
                                promoCode = APP_EMPTY_STRING,
                                promocodeDiscount = 0,
                                subscribedPlan = planDetailList!![1].id,
                                subscriptionId = "",
                                type = planType
                            )
                            getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                authToken,
                                requestData
                            )
                        } else {
                            binding.root.showLongDurationSnackBar(
                                resources.getString(
                                    R.string.text_no_internet_connection
                                )
                            )

                        }
                    }


                }
            }

            binding.cvYearlyPlan -> {
                if (planDetailList!![2] != null && planDetailList!![2].planName == binding.tvYearlyPlanTitle.text.toString()) {
                    planName = planDetailList!![2].planName
                    if (currentSubscriptionDomainEntity!!.subscribedPlan != null && currentSubscriptionDomainEntity!!.subscribedPlan.isNotEmpty()) {
                        if (currentSubscriptionDomainEntity!!.subscribedPlan == planDetailList!![2].id) {
                            // yearly to yearly - resubscribe
//                            if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                planType = InAppSubscriptionTypeConstant.resubscribe
                                showCommonDialogWithButtons(
                                    title = getString(R.string.text_iap_resubscribe_title),
                                    description = getString(R.string.text_iap_resubscribe_desc),
                                    btnPositiveText = getString(R.string.text_yes),
                                    btnNegativeText = getString(R.string.text_no),
                                    actionPositive = {
                                        if (isInternetConnection()) {
                                            if (isInternetConnection()) {
                                                val requestData =
                                                    CreateInAppSubscriptionRequestData(
                                                        planPurchasedFrom = "android",
                                                        promoCode = APP_EMPTY_STRING,
                                                        promocodeDiscount = 0,
                                                        subscribedPlan = planDetailList!![2].id,
                                                        subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                        type = planType
                                                    )
                                                getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                    authToken,
                                                    requestData
                                                )
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }
                                        } else {
                                            binding.root.showLongDurationSnackBar(
                                                resources.getString(
                                                    R.string.text_no_internet_connection
                                                )
                                            )
                                        }

                                    }
                                )
//                            }
                        } else {
                            // other to yearly - upgrade
//                            if (purchaseToken != null && purchaseToken.isNotEmpty()) {
                                planType = InAppSubscriptionTypeConstant.upgrade
                                showCommonDialogWithButtons(
                                    title = getString(R.string.text_iap_upgrade_title),
                                    description = getString(R.string.text_iap_upgrade_desc),
                                    btnPositiveText = getString(R.string.text_yes),
                                    btnNegativeText = getString(R.string.text_no),
                                    actionPositive = {
                                        if (isInternetConnection()) {
                                            if (isInternetConnection()) {
                                                val requestData =
                                                    CreateInAppSubscriptionRequestData(
                                                        planPurchasedFrom = "android",
                                                        promoCode = APP_EMPTY_STRING,
                                                        promocodeDiscount = 0,
                                                        subscribedPlan = planDetailList!![2].id,
                                                        subscriptionId = currentSubscriptionDomainEntity!!.id,
                                                        type = planType
                                                    )
                                                getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                                    authToken,
                                                    requestData
                                                )
                                            } else {
                                                binding.root.showLongDurationSnackBar(
                                                    resources.getString(
                                                        R.string.text_no_internet_connection
                                                    )
                                                )
                                            }
                                        } else {
                                            binding.root.showLongDurationSnackBar(
                                                resources.getString(
                                                    R.string.text_no_internet_connection
                                                )
                                            )
                                        }

                                    }
                                )
//                            }

                        }
                    } else {
                        // new purchase
                        if (isInternetConnection()) {
                            planType = InAppSubscriptionTypeConstant.newPurchase
                            val requestData = CreateInAppSubscriptionRequestData(
                                planPurchasedFrom = "android",
                                promoCode = APP_EMPTY_STRING,
                                promocodeDiscount = 0,
                                subscribedPlan = planDetailList!![2].id,
                                subscriptionId = "",
                                type = planType
                            )
                            getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                                authToken,
                                requestData
                            )
                        } else {
                            binding.root.showLongDurationSnackBar(
                                resources.getString(
                                    R.string.text_no_internet_connection
                                )
                            )

                        }
                    }

                    /* if (isInternetConnection()) {
                         val requestData = CreateInAppSubscriptionRequestData(
                             planPurchasedFrom = "android",
                             promoCode = APP_EMPTY_STRING,
                             promocodeDiscount = 0,
                             subscribedPlan = planDetailList!![2].id,
                             subscriptionId = "",
                             type = InAppSubscriptionTypeConstant.newPurchase
                         )
                         getPurchasePlanDetailsViewModel.createInAppSubscriptionDetails(
                             authToken,
                             requestData
                         )
                     } else {
                         binding.root.showLongDurationSnackBar(
                             resources.getString(
                                 R.string.text_no_internet_connection
                             )
                         )

                     }*/
                }
            }

            binding.ivAshtakootAnalysisInfo -> {
                showDialog(
                    getString(R.string.text_matchmakingreport_ashtakoot_title),
                    getString(R.string.text_matchmakingreport_ashtakoot_desc)
                )
            }

            binding.ivDashkootAnalysisInfo -> {
                showDialog(
                    getString(R.string.text_matchmakingreport_dashakoot_title),
                    getString(R.string.text_matchmakingreport_dashakoot_desc)
                )
            }

            binding.tvPrivacyPolicy -> {
                launchScreen<PrivacyPolicyActivity>() {
                    putExtra(
                        PrivacyPolicyActivity.PRIVACY_POLICY_URL,
                        URL_PRIVACY_POLICY
                    )
                }
            }

            binding.tvTermsOfUse -> {
                launchScreen<TermsOfUseActivity>() {
                    putExtra(
                        PrivacyPolicyActivity.KEY_URL_TERMS_OF_USE,
                        URL_TERMS_AND_CONDITION
                    )
                }
            }

            binding.tvNeedHelp -> {
                launchScreen<ContactSupportActivity>()
            }

        }
    }
}
