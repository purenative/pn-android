package pn.android.core.extensions

import android.view.View
import androidx.annotation.StringRes

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.getString(@StringRes resId: Int): String = resources.getString(resId)