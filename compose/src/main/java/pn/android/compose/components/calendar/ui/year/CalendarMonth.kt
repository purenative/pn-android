package pn.android.compose.components.calendar.ui.year

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarEvent
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.ui.day.CalendarDay
import pn.android.compose.components.calendar.ui.day.CalendarDaySelectionMode
import pn.android.compose.components.calendar.ui.day.CalendarEmptyDay
import pn.android.compose.components.calendar.utils.DAYS_IN_WEEK
import pn.android.compose.components.calendar.utils.getDays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarMonth(
    selectionMode: CalendarSelectionMode,
    month: YearMonth = YearMonth.now(),
    events: List<CalendarEvent>,
    headerTextStyle: TextStyle,
    selector: CalendarSelector,
    onDayClick: (LocalDate, CalendarEvent?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CalendarHeader(
            month = month.month.toString(),
            year = month.year,
            selector = selector,
            monthTextStyle = headerTextStyle,
            yearTextStyle = headerTextStyle
        )

        month.getDays().chunked(DAYS_IN_WEEK).forEach { weekDays ->

            Row(modifier = Modifier.fillMaxWidth()) {

                weekDays.forEach { localDate ->

                    val isFromCurrentMonth = YearMonth.from(localDate) == month

                    if (isFromCurrentMonth) {

                        val mode = when (selectionMode) {
                            is CalendarSelectionMode.Range -> {

                                val isRangeSelected = selectionMode.startDate != null &&
                                        selectionMode.endDate != null

                                val isFirstDaySelected =
                                    month.month == selectionMode.startDate?.month &&
                                            month.year == selectionMode.startDate?.year &&
                                            localDate == selectionMode.startDate

                                val isLastDaySelected =
                                    month.month == selectionMode.endDate?.month &&
                                            month.year == selectionMode.endDate?.year &&
                                            localDate == selectionMode.endDate

                                val inRangeSelected = if (isRangeSelected)
                                    (localDate.isAfter(selectionMode.startDate) &&
                                            localDate.isBefore(selectionMode.endDate)) else false

                                if (isFirstDaySelected) {
                                    if (isRangeSelected) {
                                        if (selectionMode.startDate?.dayOfWeek == DayOfWeek.SUNDAY) {
                                            CalendarDaySelectionMode.SELECTION_IN
                                        } else {
                                            CalendarDaySelectionMode.SELECTION_START
                                        }
                                    } else {
                                        CalendarDaySelectionMode.SELECTION_IN
                                    }
                                } else if (isLastDaySelected) {
                                    if (isRangeSelected) {
                                        if (selectionMode.endDate?.dayOfWeek == DayOfWeek.MONDAY) {
                                            CalendarDaySelectionMode.SELECTION_IN
                                        } else {
                                            CalendarDaySelectionMode.SELECTION_END
                                        }
                                    } else {
                                        CalendarDaySelectionMode.SELECTION_IN
                                    }
                                } else if (inRangeSelected) {

                                    if (localDate.dayOfWeek == DayOfWeek.MONDAY) {
                                        CalendarDaySelectionMode.RANGE_START
                                    } else if (localDate.dayOfWeek == DayOfWeek.SUNDAY) {
                                        CalendarDaySelectionMode.RANGE_END
                                    } else if (localDate.dayOfMonth == 1) {
                                        CalendarDaySelectionMode.RANGE_START
                                    } else if (localDate.dayOfMonth == localDate.lengthOfMonth()) {
                                        CalendarDaySelectionMode.RANGE_END
                                    } else {
                                        CalendarDaySelectionMode.RANGE_IN
                                    }

                                } else {
                                    CalendarDaySelectionMode.IDLE
                                }
                            }
                            is CalendarSelectionMode.Single -> {

                                if (selectionMode.date == null) {
                                    CalendarDaySelectionMode.IDLE
                                } else {
                                    val isDaySelected =
                                        month.month == selectionMode.date.month &&
                                                month.year == selectionMode.date.year &&
                                                localDate == selectionMode.date

                                    if (isDaySelected) CalendarDaySelectionMode.SELECTION_IN else
                                        CalendarDaySelectionMode.IDLE
                                }

                            }
                        }

                        CalendarDay(
                            modifier = Modifier.weight(1f),
                            date = localDate,
                            mode = mode,
                            events = events,
                            selector = selector,
                            textStyle = headerTextStyle,
                            onDayClick = { date, event ->
                                onDayClick(date, event)
                            }
                        )

                    } else {
                        CalendarEmptyDay(
                            modifier = Modifier.weight(1f),
                            selector = selector,
                            textStyle = headerTextStyle
                        )
                    }
                }

            }

        }

    }

}


