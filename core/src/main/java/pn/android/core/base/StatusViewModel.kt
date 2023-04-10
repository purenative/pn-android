package pn.android.core.base

import android.annotation.SuppressLint
import android.content.Context
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
abstract class StatusViewModel<STATE : Statusable, SIDE_EFFECT : Any>(
    private val context: Context
) :
    BaseViewModel<STATE, SIDE_EFFECT>() {

    abstract suspend fun SimpleSyntax<STATE, SIDE_EFFECT>.onLoading()
    abstract suspend fun SimpleSyntax<STATE, SIDE_EFFECT>.onError(status: Status)

    fun statusIntent(
        transformer: suspend SimpleSyntax<STATE, SIDE_EFFECT>.() -> Unit
    ) {
        intent {
            try {
                onLoading()
                transformer()
            } catch (e: Exception) {
                Timber.e(e)
                onError(Status.UnknownError)
            }
        }
    }
}