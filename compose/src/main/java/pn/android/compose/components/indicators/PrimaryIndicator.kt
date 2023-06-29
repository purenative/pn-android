package pn.android.compose.components.indicators

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Component to display the loader
 * [modifier] - parameter to change the CircularProgressIndicator according to the needs of the developer
 * [color] - serves to change the color of the loader
 * [size] - serves to change the size of the loader
 * [strokeWidth] - The stroke width for the loader
 * */
@Composable
fun PrimaryIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    size: Dp = 28.dp,
    strokeWidth: Dp = 3.dp
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}