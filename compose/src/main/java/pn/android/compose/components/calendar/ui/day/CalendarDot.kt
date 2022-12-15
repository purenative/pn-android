package pn.android.compose.components.calendar.ui.day

import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarSelector

@Composable
fun CalendarDot(
    modifier: Modifier = Modifier,
    selector: CalendarSelector
) {

    Surface(
        modifier = modifier.size(6.dp),
        shape = selector.shape,
        color = selector.selectedColor,
    ) {
    }

}