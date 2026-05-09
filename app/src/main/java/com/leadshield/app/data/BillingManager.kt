package com.leadshield.app.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.leadshield.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Play Billing for the app.
 * Includes server-side verification hooks for production security.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_PRO = "mctb_pro_monthly"
        const val PRODUCT_OPERATOR = "mctb_operator_monthly"
        const val PRODUCT_MASTER = "mctb_master_monthly"  // internal/developer
        const val PRODUCT_VOICE = "mctb_voice_monthly"
        const val PRODUCT_TEAM = "mctb_team_monthly"
    }

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _isOperator = MutableStateFlow(false)
    val isOperator: StateFlow<Boolean> = _isOperator.asStateFlow()

    private val _isMaster = MutableStateFlow(false)
    val isMaster: StateFlow<Boolean> = _isMaster.asStateFlow()

    private val _isVoice = MutableStateFlow(false)
    val isVoice: StateFlow<Boolean> = _isVoice.asStateFlow()

    private val _isTeam = MutableStateFlow(false)
    val isTeam: StateFlow<Boolean> = _isTeam.asStateFlow()

    private val _productDetailsList = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsList: StateFlow<List<ProductDetails>> = _productDetailsList.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun startConnection() {
        if (billingClient.isReady) return
        
        Log.d(TAG, "Starting billing connection...")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Billing setup finished")
                    queryPurchases()
                    queryProductDetails()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                // Re-connection logic could go here
            }
        })
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { result, purchasesBuffer ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                scope.launch { processPurchases(purchasesBuffer) }
            }
        }
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_PRO).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_OPERATOR).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_MASTER).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_VOICE).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_TEAM).setProductType(BillingClient.ProductType.SUBS).build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetailsList.value = productDetailsList
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val offerDetails = productDetails.subscriptionOfferDetails
        if (offerDetails.isNullOrEmpty()) {
            Log.e(TAG, "No offer details found for ${productDetails.productId}")
            return
        }
        
        val offerToken = offerDetails[0].offerToken
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        var pro = false
        var operator = false
        var master = false
        var voice = false
        var team = false

        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (verifyPurchaseOnServer(purchase)) {
                    for (product in purchase.products) {
                        when (product) {
                            PRODUCT_PRO -> pro = true
                            PRODUCT_OPERATOR -> operator = true
                            PRODUCT_MASTER -> master = true
                            PRODUCT_VOICE -> voice = true
                            PRODUCT_TEAM -> team = true
                        }
                    }

                    if (!purchase.isAcknowledged) {
                        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        billingClient.acknowledgePurchase(acknowledgeParams) { _ -> }
                    }
                }
            }
        }

        _isPro.value = pro
        _isOperator.value = operator
        _isMaster.value = master
        _isVoice.value = voice
        _isTeam.value = team
    }

    private suspend fun verifyPurchaseOnServer(purchase: Purchase): Boolean = withContext(Dispatchers.IO) {
        val endpoint = BuildConfig.BILLING_VERIFY_URL
        val strictMode = BuildConfig.ENABLE_STRICT_BILLING_VERIFICATION

        if (endpoint.isBlank()) {
            if (strictMode) {
                Log.e(TAG, "Strict billing verification is enabled, but BILLING_VERIFY_URL is empty.")
                return@withContext false
            }
            Log.w(TAG, "BILLING_VERIFY_URL not configured; allowing purchase in non-strict mode.")
            return@withContext true
        }

        try {
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val requestBody = JSONObject()
                .put("purchaseToken", purchase.purchaseToken)
                .put("products", purchase.products.joinToString(","))
                .put("packageName", context.packageName)
                .toString()

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestBody)
            }

            val responseCode = connection.responseCode
            val accepted = responseCode in 200..299
            if (!accepted) {
                Log.e(TAG, "Purchase verification failed with HTTP $responseCode")
            }
            accepted
        } catch (e: Exception) {
            Log.e(TAG, "Purchase verification request failed", e)
            !strictMode
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { processPurchases(purchases) }
        }
    }
}
