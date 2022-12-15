package pn.android.compose.components.calendar.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.math.abs

const val DAYS_IN_WEEK = 7
val WEEK_LIST = listOf(7, 1, 2, 3, 4, 5, 6)

fun YearMonth.getDays(dayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    return mutableListOf<LocalDate>().apply {

        val firstDay = atDay(1)
        val moves = firstDay.dayOfWeek.value - dayOfWeek.value

        val firstDayOfWeek = if (firstDay.dayOfWeek == dayOfWeek) {
            firstDay
        } else {
            firstDay.minusDays(firstDay.dayOfWeek.value - dayOfWeek.value.toLong())
        }

        when {
            firstDay.dayOfWeek.value > dayOfWeek.value -> {
                Collections.rotate(WEEK_LIST, moves)
            }
            firstDay.dayOfWeek.value > dayOfWeek.value -> {
                Collections.rotate(WEEK_LIST, abs(moves))
            }
        }

        repeat(6) { weekIndex ->
            (0..6).forEach { dayIndex ->
                add(firstDayOfWeek.plusDays((7.times(weekIndex).plus(dayIndex)).toLong()))
            }
        }

    }
}