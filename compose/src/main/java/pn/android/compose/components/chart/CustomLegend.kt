package pn.android.compose.components.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.legend.Legend


/**
 * Standard Legend For Chart
 * */

@Composable
fun rememberLegend(legendTextFirst: String, legendTextSecond: String): Legend {
    return verticalLegend(
        items = listOf(
            legendItem(
                icon = shapeComponent(
                    shape = Shapes.pillShape,
                    color = Color.Red,
                ),
                label = textComponent {
                    color = android.graphics.Color.RED
                    textSizeSp = 16f
                    typeface = Typeface.DEFAULT_BOLD
                },
                labelText = legendTextFirst
            ),
            legendItem(
                icon = shapeComponent(
                    shape = Shapes.rectShape,
                    color = Color.Blue
                ),
                label = textComponent {
                    color = android.graphics.Color.BLUE
                    textSizeSp = 16f
                    typeface = Typeface.DEFAULT_BOLD
                },
                labelText = legendTextSecond
            )
        ),
        iconSize = 10.dp,
        iconPadding = 10.dp,
        padding = dimensionsOf(top = 10.dp),
        spacing = 10.dp
    )
}