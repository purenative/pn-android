package pn.android.compose.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A side effect of composition that must run for lifeCycleOwner and must be reversed or cleaned up if
 * lifeCycleOwner changes or if the DisposableEffect leaves the composition.
 *
 * [lifeCycleOwner] - a parameter that contains class that has an Android lifecycle.
 * These events can be used by custom components to handle lifecycle changes without implementing any code inside the Activity or the Fragment.
 * [onEvent] - The logic that happens when the compose function's lifecycle changes
 * */
@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}