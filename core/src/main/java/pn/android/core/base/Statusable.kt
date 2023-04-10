package pn.android.core.base

import pn.android.core.base.Status

interface Statusable {
    val status: Status
    fun doOnSuccess(action: () -> Unit) {
        when (status) {
            is Status.Success -> {
                action()
            }
            else -> {}
        }
    }
}