package pn.android.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

inline fun <reified T : Activity> Context.open() =
    startActivity(Intent(this, T::class.java))
