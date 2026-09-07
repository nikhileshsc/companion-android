package com.companion.astrodating.ui.purchasePlans

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePlayPurchaseReconciler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: CompanionApi,
) {
    private val reconciling = AtomicBoolean(false)
    private val processingTokens = ConcurrentHashMap.newKeySet<String>()

    fun reconcile() {
        val authToken = StorePreferences.getAuthToken() ?: return
        if (!reconciling.compareAndSet(false, true)) return
        val billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener { _, _ -> }
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode != BillingClient.BillingResponseCode.OK) return finish(billingClient)
                billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
                ) { queryResult, purchases ->
                    if (queryResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }.forEach { purchase ->
                            if (!processingTokens.add(purchase.purchaseToken)) return@forEach
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response = api.verifyInAppSubscription(
                                        authToken,
                                        VerifyInAppSubscriptionRequestData(
                                            purchaseToken = purchase.purchaseToken,
                                            productId = purchase.products.firstOrNull(),
                                        )
                                    )
                                    if (response.isSuccessful) StorePreferences.saveSubscriptionData(null)
                                    Log.d(TAG, "Google Play restore: code=${response.code()}, token=${purchase.purchaseToken.take(6)}…")
                                } catch (error: Exception) {
                                    Log.w(TAG, "Google Play restore failed: ${error.javaClass.simpleName}")
                                } finally {
                                    processingTokens.remove(purchase.purchaseToken)
                                }
                            }
                        }
                    }
                    finish(billingClient)
                }
            }

            override fun onBillingServiceDisconnected() = finish(billingClient)
        })
    }

    private fun finish(billingClient: BillingClient) {
        reconciling.set(false)
        if (billingClient.isReady) billingClient.endConnection()
    }

    private companion object { const val TAG = "PlayPurchaseReconcile" }
}
