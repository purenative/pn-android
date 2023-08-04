package pn.android.compose.components.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.component.shape.Shapes

private const val COLOR_4_CODE = 0xffe9e5af
private const val THRESHOLD_LINE_VALUE_RANGE_START = 7f
private const val THRESHOLD_LINE_VALUE_RANGE_END = 14f
private const val THRESHOLD_LINE_ALPHA = .36f

private val color4 = Color(COLOR_4_CODE)
private val thresholdLineValueRange = THRESHOLD_LINE_VALUE_RANGE_START..THRESHOLD_LINE_VALUE_RANGE_END
private val thresholdLineLabelHorizontalPaddingValue = 8.dp
private val thresholdLineLabelVerticalPaddingValue = 2.dp
private val thresholdLineLabelMarginValue = 4.dp
private val thresholdLineLabelPadding =
    dimensionsOf(thresholdLineLabelHorizontalPaddingValue, thresholdLineLabelVerticalPaddingValue)
private val thresholdLineLabelMargins = dimensionsOf(thresholdLineLabelMarginValue)
private val thresholdLineColor = color4.copy(THRESHOLD_LINE_ALPHA)

/**
 * Standard ThresholdLine For Chart
 * */

@Composable
fun rememberThresholdLine(): ThresholdLine {
    val label = textComponent(
        color = Color.Black,
        background = shapeComponent(Shapes.rectShape, color4),
        padding = thresholdLineLabelPadding,
        margins = thresholdLineLabelMargins,
        typeface = Typeface.MONOSPACE,
    )
    val line = shapeComponent(color = thresholdLineColor)
    return remember(label, line) {
        ThresholdLine(thresholdRange = thresholdLineValueRange, labelComponent = label, lineComponent = line)
    }
}