package pn.android.core.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Status : Parcelable {

    @Parcelize
    object Loading : Status()

    @Parcelize
    object Success : Status()

    @Parcelize
    data class ApiError(val error: String) : Status()

    @Parcelize
    data class Unauthenticated(val error: String) : Status()

    @Parcelize
    object NetworkError : Status()

    @Parcelize
    object UnknownError : Status()
}