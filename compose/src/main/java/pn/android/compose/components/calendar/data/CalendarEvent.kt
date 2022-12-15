package pn.android.compose.components.calendar.data

import java.time.LocalDate

data class CalendarEvent(
    val date: LocalDate? = null,
    val eventName: String? = null,
    val eventDescription: String? = null,
)