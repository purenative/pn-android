package pn.android.core.iap

interface BillingClientConnectionListener {
    fun onConnected(status: Boolean, billingResponseCode: Int)
    fun onError(error: String, fatal: Boolean)
}