package pn.android.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import pn.android.core.R

const val MARKET_DETAILS_ID = "market://details?id="
const val HTTPS_PLAY_GOOGLE_COM_ID = "https://play.google.com/store/apps/details?id="

inline fun <reified T : Activity> Context.open() =
    startActivity(Intent(this, T::class.java))

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Context.rateUs() {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("$MARKET_DETAILS_ID$packageName")
            )
        )
    } catch (e: Exception) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("$HTTPS_PLAY_GOOGLE_COM_ID$packageName")
                )
            )
        } catch (e: Exception) {
            showToast(getString(R.string.play_market_not_found))
        }
    }
}

fun Context.shareApp() {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.share)
        )
        intent.putExtra(Intent.EXTRA_TEXT, "$HTTPS_PLAY_GOOGLE_COM_ID$packageName")
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.share)
            )
        )
    } catch (e: Exception) {
        showToast(getString(R.string.sharing_app_not_found))
    }
}

fun Context.openUrl(url: String) {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    } catch (e: Exception) {
        showToast(getString(R.string.browser_not_found))
    }
}
