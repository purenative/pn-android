package pn.android.features.calendar

import java.time.LocalDate

sealed class CalendarAction {
    data class DateSelected(val date: LocalDate): CalendarAction()
}