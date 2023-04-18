package pn.android.core.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import pn.android.core.R

const val MARKET_DETAILS_ID = "market://details?id="
const val HTTPS_PLAY_GOOGLE_COM_ID = "https://play.google.com/store/apps/details?id="

inline fun <reified T : Activity> Context.open() =
    startActivity(Intent(this, T::class.java))

inline fun Context.startActivity(action: String, configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(action).apply(configIntent))
}

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

fun Context.openEmail(
    email: String,
    subject: String,
    body: String = ""
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email?&subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        showToast(getString(R.string.email_client_not_found))
    }
}

fun Context.openCall(number: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    } catch (e: Exception) {
        showToast(getString(R.string.dialer_not_found))
    }
}

fun Context.sendSms(
    number: String,
    text: String = ""
) {
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("sms:$number")
        ).apply {
            putExtra("sms_body", text)
        }
        startActivity(intent)
    } catch (e: Exception) {
        showToast(getString(R.string.sms_client_not_found))
    }
}


fun Context.copyToClipboard(content: String) {
    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.let {
        val clip = ClipData.newPlainText("clipboard", content)
        it.setPrimaryClip(clip)
    }
}

fun Context.showAlertDialog(
    positiveButtonLabel: String,
    title: String,
    message: String,
    actionOnPositiveButton: () -> Unit
) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(positiveButtonLabel) { dialog, id ->
            dialog.cancel()
            actionOnPositiveButton()
        }
    val alert = builder.create()
    alert?.show()
}

val Context.versionName: String?
    get() = try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo?.versionName
    } catch (e: Exception) {
        null
    }

val Context.versionCode: Long?
    get() = try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            pInfo?.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pInfo?.versionCode?.toLong()
        }
    } catch (e: Exception) {
        null
    }






