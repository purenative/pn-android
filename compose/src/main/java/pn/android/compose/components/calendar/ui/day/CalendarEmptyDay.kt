package pn.android.compose.components.calendar.ui.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarSelector

@Composable
fun CalendarEmptyDay(
    modifier: Modifier = Modifier,
    selector: CalendarSelector,
    textStyle: TextStyle
) {

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(selector.shape)
            .background(selector.defaultColor)
    )
    {

        Text(
            text = "",
            style = textStyle,
            color = selector.defaultColor,
            modifier = Modifier
                .align(Alignment.Center),
        )

    }

}