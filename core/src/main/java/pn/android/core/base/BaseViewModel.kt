package pn.android.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any> :
    ContainerHost<STATE, SIDE_EFFECT>, ViewModel()

fun <P1> debounce(
    waitMs: Long = 500L,
    coroutineScope: CoroutineScope,
    destinationFunction: (P1) -> Unit
): (P1) -> Unit {
    var debounceJob: Job? = null
    return { p1 ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(p1)
        }
    }
}