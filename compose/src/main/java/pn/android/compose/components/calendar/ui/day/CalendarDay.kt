package pn.android.compose.components.calendar.ui.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarEvent
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.utils.*
import java.time.LocalDate

@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    date: LocalDate,
    mode: CalendarDaySelectionMode,
    events: List<CalendarEvent>,
    selector: CalendarSelector,
    textStyle: TextStyle,
    onDayClick: (LocalDate, CalendarEvent?) -> Unit
) {

    val event: CalendarEvent? = events.find { it.date == date }

    Column(
        modifier = modifier
            .clip(
                getBackgroundShape(
                    mode = mode,
                    selector = selector
                )
            )
            .background(
                getBackgroundBrush(
                    mode = mode,
                    selector = selector
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(
                    getShape(
                        mode = mode,
                        selector = selector
                    )
                )
                .background(
                    getColor(
                        mode = mode,
                        selector = selector
                    )
                )
                .clickable {
                    onDayClick(date, event)
                }
        )
        {

            Text(
                text = date.dayOfMonth.toString(),
                style = textStyle,
                color = getTextColor(mode, selector),
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }

        if (event != null) {
            Spacer(modifier = Modifier.height(1.dp))
            CalendarDot(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selector = selector
            )
        }

    }

}
