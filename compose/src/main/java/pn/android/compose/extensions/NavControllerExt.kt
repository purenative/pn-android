package pn.android.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.destination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Finds the [Destination] correspondent to this [NavBackStackEntry].
 * Some [NavBackStackEntry] are not [Destination], but are [NavGraph] instead.
 * If you want a method that works for both, use [route] extension function instead.
 *
 * Use this ONLY if you're sure your [NavBackStackEntry] corresponds to a [Destination],
 * for example when converting from "current NavBackStackEntry", since a [NavGraph] is never
 * the "current destination" shown on screen.
 */
public fun NavBackStackEntry.appDestination(): DestinationSpec<*> {
    return destination()
}

/**
 * Emits the currently active [Destination] whenever it changes. If
 * there is no active [Destination], no item will be emitted.
 */
public val NavController.appCurrentDestinationFlow: Flow<DestinationSpec<*>>
    get() = currentBackStackEntryFlow.map { it.appDestination() }

/**
 * Gets the current [Destination] as a [State].
 */
@Composable
public fun NavController.appCurrentDestinationAsState(): State<DestinationSpec<*>?> {
    return appCurrentDestinationFlow.collectAsState(initial = null)
}