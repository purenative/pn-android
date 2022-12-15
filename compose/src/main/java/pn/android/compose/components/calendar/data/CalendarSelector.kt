package pn.android.compose.components.calendar.data

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

sealed class CalendarSelector(
    open val shape: Shape,
    open val selectedColor: Color,
    open val rangeColor: Color,
    open val defaultColor: Color,

    open val selectedBrush: Brush,
    open val selectedStartBrush: Brush,
    open val selectedEndBrush: Brush,

    open val rangeBrush: Brush,
    open val rangeStartBrush: Brush,
    open val rangeEndBrush: Brush,

    open val defaultBrush: Brush,
    open val transparentBrush: Brush,

    open val selectedTextColor: Color,
    open val defaultTextColor: Color,
    open val eventTextColor: Color
) {
    data class AppSelector(
        override val selectedColor: Color = Color.Red,
        override val rangeColor: Color = Color.Green,
        override val defaultColor: Color = Color.White,

        override val selectedBrush: Brush = Brush.horizontalGradient(
            0.00f to selectedColor,
            1.00f to selectedColor
        ),
        override val selectedStartBrush: Brush = Brush.horizontalGradient(
            0.00f to defaultColor,
            0.50f to defaultColor,
            0.50f to rangeColor,
            1.00f to rangeColor
        ),
        override val selectedEndBrush: Brush = Brush.horizontalGradient(
            0.00f to rangeColor,
            0.50f to rangeColor,
            0.50f to defaultColor,
            1.00f to defaultColor
        ),

        override val rangeBrush: Brush = Brush.horizontalGradient(
            0.00f to rangeColor,
            1.00f to rangeColor
        ),
        override val rangeStartBrush: Brush = Brush.horizontalGradient(
            0.00f to rangeColor,
            0.50f to rangeColor,
            0.50f to Color.Transparent,
            1.00f to Color.Transparent
        ),
        override val rangeEndBrush: Brush = Brush.horizontalGradient(
            0.00f to Color.Transparent,
            0.50f to Color.Transparent,
            0.50f to rangeColor,
            1.00f to rangeColor
        ),

        override val defaultBrush: Brush = Brush.horizontalGradient(
            0.00f to defaultColor,
            1.00f to defaultColor
        ),
        override val transparentBrush: Brush = Brush.horizontalGradient(
            0.00f to Color.Transparent,
            1.00f to Color.Transparent
        ),

        override val selectedTextColor: Color = Color.White,
        override val defaultTextColor: Color = Color.Black,
        override val eventTextColor: Color = Color.Red
    ) : CalendarSelector(
        CircleShape,

        selectedColor,
        rangeColor,
        defaultColor,

        selectedBrush,
        selectedStartBrush,
        selectedEndBrush,

        rangeBrush,
        rangeStartBrush,
        rangeEndBrush,

        defaultBrush,
        transparentBrush,

        selectedTextColor,
        defaultTextColor,
        eventTextColor
    )
}
