package pn.android.core.navigation

import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

fun DestinationsNavigator.navigate(
    screen: ScreenProvider,
    onlyIfResumed: Boolean = false,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(ScreenRegistry.get(screen), onlyIfResumed, builder)
}