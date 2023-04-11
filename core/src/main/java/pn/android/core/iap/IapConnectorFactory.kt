package pn.android.core.iap

import android.content.Context
import timber.log.Timber

object IapConnectorFactory {

    fun createIapConnector(
        context: Context,
        subscriptionKeys: List<String>,
        onConnected: (Boolean, Int) -> Unit,
        onError: (String, Boolean) -> Unit,
        onProductPricesUpdated: (Map<String, DataWrappers.ProductDetails>) -> Unit,
        onProductPurchased: (DataWrappers.PurchaseInfo) -> Unit,
        onProductRestored: (DataWrappers.PurchaseInfo) -> Unit,
        onSubscriptionPricesUpdated: (Map<String, DataWrappers.ProductDetails>) -> Unit,
        onSubscriptionPurchased: (DataWrappers.PurchaseInfo) -> Unit,
        onSubscriptionRestored: (DataWrappers.PurchaseInfo) -> Unit,
        onSubscriptionAcknowledged: () -> Unit,
        onSubscriptionEnded: () -> Unit
    ): IapConnector {

        val iapConnector = IapConnector(
            context = context,
            subscriptionKeys = subscriptionKeys,
            enableLogging = true
        )

        iapConnector.addBillingClientConnectionListener(object : BillingClientConnectionListener {

            override fun onConnected(status: Boolean, billingResponseCode: Int) {
                Timber.d("onConnected: $status and response code is: $billingResponseCode")
                onConnected(status, billingResponseCode)
            }

            override fun onError(error: String, fatal: Boolean) {
                Timber.e("onError: $error: $fatal")
                onError(error, fatal)
            }

        })

        iapConnector.addPurchaseListener(object : PurchaseServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.ProductDetails>) {
                Timber.d("products onPricesUpdated")
                onProductPricesUpdated(iapKeyPrices)
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                Timber.d("onProductPurchased")
                onProductPurchased(purchaseInfo)
            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                Timber.d("onProductRestored")
                onProductRestored(purchaseInfo)
            }
        })

        iapConnector.addSubscriptionListener(object : SubscriptionServiceListener {

            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.ProductDetails>) {
                Timber.d("subscriptions onPricesUpdated: ${iapKeyPrices.size}")
                onSubscriptionPricesUpdated(iapKeyPrices)
            }

            override fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                Timber.d("onSubscriptionPurchased")
                onSubscriptionPurchased(purchaseInfo)
            }

            override fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                Timber.d("onSubscriptionRestored")
                onSubscriptionRestored(purchaseInfo)
            }

            override fun onSubscriptionAcknowledged() {
                Timber.d("onSubscriptionAcknowledged")
                onSubscriptionAcknowledged()
            }

            override fun onSubscriptionEnded() {
                Timber.d("onSubscriptionEnded")
                onSubscriptionEnded()
            }

        })

        return iapConnector

    }

}