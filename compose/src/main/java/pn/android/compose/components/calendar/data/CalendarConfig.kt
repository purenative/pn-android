package pn.android.compose.components.calendar.data

data class CalendarConfig(
    val selector: CalendarSelector = CalendarSelector.AppSelector(),
    val event: List<CalendarEvent> = emptyList()
)