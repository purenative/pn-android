package pn.android.core.iap

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.util.Currency

class DataWrappers {

    @Parcelize
    data class ProductDetails(
        val title: String?,
        val description: String?,
        val price: String?,
        val priceAmount: Double?,
        val priceCurrencyCode: String?,
    ): Parcelable {
        fun getCurrencySymbol(): String {
            return try {
                if (priceCurrencyCode == null) return ""
                Currency.getInstance(priceCurrencyCode).symbol
            } catch (e: Exception) {
                Timber.e(e)
                ""
            }
        }
    }

    @Parcelize
    data class PurchaseInfo(
        val purchaseState: Int,
        val developerPayload: String,
        val isAcknowledged: Boolean,
        val isAutoRenewing: Boolean,
        val orderId: String,
        val originalJson: String,
        val packageName: String,
        val purchaseTime: Long,
        val purchaseToken: String,
        val signature: String,
        val sku: String
    ): Parcelable

}