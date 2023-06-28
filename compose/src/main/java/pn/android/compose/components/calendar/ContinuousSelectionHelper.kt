package pn.android.compose.components.calendar

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * data class that calculate time between two days after checking them for nullity.
 * The result will be negative if the endDate is before the startDate.
 * [startDate] - first date without a time-zone in the ISO-8601 calendar system
 * [endDate] - second date without a time-zone in the ISO-8601 calendar system
 * */

data class DateSelection(val startDate: LocalDate? = null, val endDate: LocalDate? = null) {
    val daysBetween by lazy(NONE) {
        if (startDate == null || endDate == null) null else {
            ChronoUnit.DAYS.between(startDate, endDate)
        }
    }
}

private val rangeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

/**
 * fun that return string with two dates in "d MMMM yyyy" format.
 * [startDate] - first date without a time-zone in the ISO-8601 calendar system
 * [endDate] - second date without a time-zone in the ISO-8601 calendar system
 * */
fun dateRangeDisplayText(startDate: LocalDate, endDate: LocalDate): String {
    return "Selected: ${rangeFormatter.format(startDate)} - ${rangeFormatter.format(endDate)}"
}

/**
 * object for work with calendar selection
 * */
object ContinuousSelectionHelper {
    /**
     * a function that returns the date you selected in the calendar in DateSelection format
     * [clickedDate] - selected in the calendar date
     * [dateSelection] - contains the start and end dates, in the circumstances in which they have already been selected, otherwise null
     * */
    fun getSelection(
        clickedDate: LocalDate,
        dateSelection: DateSelection,
    ): DateSelection {
        val (selectionStartDate, selectionEndDate) = dateSelection
        return if (selectionStartDate != null) {
            if (clickedDate < selectionStartDate || selectionEndDate != null) {
                DateSelection(startDate = clickedDate, endDate = null)
            } else if (clickedDate != selectionStartDate) {
                DateSelection(startDate = selectionStartDate, endDate = clickedDate)
            } else {
                DateSelection(startDate = clickedDate, endDate = null)
            }
        } else {
            DateSelection(startDate = clickedDate, endDate = null)
        }
    }

    /**
     * a function that checks if the desired date is between two other dates
     * [inDate] - compared date
     * [startDate] - first date without a time-zone in the ISO-8601 calendar system
     * [endDate] - second date without a time-zone in the ISO-8601 calendar system
     * */
    fun isInDateBetweenSelection(
        inDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (inDate.yearMonth == startDate.yearMonth) return true
        val firstDateInThisMonth = inDate.yearMonth.nextMonth.atStartOfMonth()
        return firstDateInThisMonth in startDate..endDate && startDate != firstDateInThisMonth
    }

    /**
     * a function that checks if the desired date is outside the interval between two other dates
     * [outDate] - compared date
     * [startDate] - first date without a time-zone in the ISO-8601 calendar system
     * [endDate] - second date without a time-zone in the ISO-8601 calendar system
     * */
    fun isOutDateBetweenSelection(
        outDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Boolean {
        if (startDate.yearMonth == endDate.yearMonth) return false
        if (outDate.yearMonth == endDate.yearMonth) return true
        val lastDateInThisMonth = outDate.yearMonth.previousMonth.atEndOfMonth()
        return lastDateInThisMonth in startDate..endDate && endDate != lastDateInThisMonth
    }
}

/**
 * a function that returns string information about month and year
 * [short] - style of text, if true, then November -> Nov.
 * */
fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

/**
 * a function that returns the textual name used to identify the month-of-year in short or long style,
 * suitable for presentation to the user
 *
 * [short] - style of text, if true, then November -> Nov.
 * */
fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

/**
 * a function that returns the textual name used to identify the month-of-year in uppercase or not uppercase style,
 * suitable for presentation to the user
 *
 * [uppercase] - style of text, if true, then November -> NOVEMBER
 * */
fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

/**
 * a function that returns a modifier with changed clickable logic
 *
 * [enabled] - Controls the enabled state. When false, onClick, and this modifier will appear disabled for accessibility services
 * [showRipple] - the field that will be responsible for the indication style, if true,
 * then LocalIndication.current, else null
 * [onClickLabel] - semantic / accessibility label for the onClick action
 * [role] - the type of user interface element. Accessibility services might use this to describe the element or do customizations
 * [onClick] - will be called when user clicks on the element
 * */
fun Modifier.clickable(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) LocalIndication.current else null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}
