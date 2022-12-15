package pn.android.compose.components.calendar.ui.year

import java.time.LocalDate

sealed class CalendarSelectionMode {
    data class Single(val date: LocalDate?) : CalendarSelectionMode()
    data class Range(val startDate: LocalDate?, val endDate: LocalDate?) : CalendarSelectionMode()
}