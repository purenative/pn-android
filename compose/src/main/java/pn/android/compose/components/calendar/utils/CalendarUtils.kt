package pn.android.compose.components.calendar.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.ui.day.CalendarDaySelectionMode

fun getBackgroundBrush(
    mode: CalendarDaySelectionMode,
    selector: CalendarSelector
): Brush = when (mode) {
    CalendarDaySelectionMode.IDLE -> selector.defaultBrush
    CalendarDaySelectionMode.SELECTION_START -> selector.selectedStartBrush
    CalendarDaySelectionMode.SELECTION_IN -> selector.defaultBrush
    CalendarDaySelectionMode.SELECTION_END -> selector.selectedEndBrush
    CalendarDaySelectionMode.RANGE_START -> selector.rangeBrush
    CalendarDaySelectionMode.RANGE_IN -> selector.rangeBrush
    CalendarDaySelectionMode.RANGE_END -> selector.rangeBrush
}

fun getColor(
    mode: CalendarDaySelectionMode,
    selector: CalendarSelector
): Color = when (mode) {
    CalendarDaySelectionMode.IDLE -> Color.Transparent
    CalendarDaySelectionMode.SELECTION_START -> selector.selectedColor
    CalendarDaySelectionMode.SELECTION_IN -> selector.selectedColor
    CalendarDaySelectionMode.SELECTION_END -> selector.selectedColor
    CalendarDaySelectionMode.RANGE_START -> Color.Transparent
    CalendarDaySelectionMode.RANGE_IN -> Color.Transparent
    CalendarDaySelectionMode.RANGE_END -> Color.Transparent
}

fun getTextColor(
    mode: CalendarDaySelectionMode,
    selector: CalendarSelector
): Color = when (mode) {
    CalendarDaySelectionMode.IDLE -> selector.defaultTextColor
    CalendarDaySelectionMode.SELECTION_START -> selector.selectedTextColor
    CalendarDaySelectionMode.SELECTION_IN -> selector.selectedTextColor
    CalendarDaySelectionMode.SELECTION_END -> selector.selectedTextColor
    CalendarDaySelectionMode.RANGE_START -> selector.defaultTextColor
    CalendarDaySelectionMode.RANGE_IN -> selector.defaultTextColor
    CalendarDaySelectionMode.RANGE_END -> selector.defaultTextColor
}

fun getBackgroundShape(
    mode: CalendarDaySelectionMode,
    selector: CalendarSelector
): Shape = when (mode) {
    CalendarDaySelectionMode.IDLE -> RectangleShape

    CalendarDaySelectionMode.SELECTION_START -> RoundedCornerShape(
        topStartPercent = 50,
        bottomStartPercent = 50
    )

    CalendarDaySelectionMode.SELECTION_IN -> RectangleShape

    CalendarDaySelectionMode.SELECTION_END -> RoundedCornerShape(
        topEndPercent = 50,
        bottomEndPercent = 50
    )

    CalendarDaySelectionMode.RANGE_START -> RoundedCornerShape(
        topStartPercent = 50,
        bottomStartPercent = 50
    )

    CalendarDaySelectionMode.RANGE_IN -> RectangleShape

    CalendarDaySelectionMode.RANGE_END -> RoundedCornerShape(
        topEndPercent = 50,
        bottomEndPercent = 50
    )

}

fun getShape(
    mode: CalendarDaySelectionMode,
    selector: CalendarSelector
): Shape = when (mode) {
    CalendarDaySelectionMode.IDLE -> selector.shape
    CalendarDaySelectionMode.SELECTION_START -> selector.shape
    CalendarDaySelectionMode.SELECTION_IN -> selector.shape
    CalendarDaySelectionMode.SELECTION_END -> selector.shape
    CalendarDaySelectionMode.RANGE_START -> RoundedCornerShape(
        topStartPercent = 50,
        bottomStartPercent = 50
    )
    CalendarDaySelectionMode.RANGE_IN -> RectangleShape
    CalendarDaySelectionMode.RANGE_END -> RoundedCornerShape(
        topEndPercent = 50,
        bottomEndPercent = 50
    )
}