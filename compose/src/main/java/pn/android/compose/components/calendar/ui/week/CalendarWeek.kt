package pn.android.compose.components.calendar.ui.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarEvent
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.ui.day.CalendarDay
import pn.android.compose.components.calendar.ui.day.CalendarDaySelectionMode
import java.time.LocalDate

@Composable
fun CalendarWeek(
    week: List<LocalDate>,
    selectedDay: LocalDate,
    selector: CalendarSelector = CalendarSelector.AppSelector(),
    headerStyle: TextStyle,
    onCurrentDayClick: (LocalDate, CalendarEvent?) -> Unit,
    events: List<CalendarEvent> = emptyList()
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = selector.defaultColor)
    ) {
        week.forEach { date ->

            val isSelected = date == selectedDay

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = date.dayOfWeek.toString().take(2).uppercase(),
                    style = headerStyle
                )

                Spacer(modifier = Modifier.height(10.dp))

                CalendarDay(
                    date = date,
                    mode = if (isSelected) CalendarDaySelectionMode.SELECTION_IN else
                        CalendarDaySelectionMode.IDLE,
                    events = events,
                    selector = selector,
                    textStyle = headerStyle,
                    onDayClick = onCurrentDayClick
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }

}
