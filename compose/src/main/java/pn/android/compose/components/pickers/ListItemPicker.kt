package pn.android.compose.components.pickers

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Logic for creating white space between picker list items
 *
 * [range] - List with elements for picker
 * [value] - Choosing element from list
 * [offset] - white space between elements in float
 * [halfNumbersColumnHeightPx] - Half column height in pixels
 * */
private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}

/**
 * Compose function that contains a layout that contains a list of elements between which the user wants to choose
 *
 * [modifier] - a parameter to change the parent Layout according to the needs of the developer
 * [label] - logic that takes an element from the list and displays it on the screen as a string
 * [value] - selected list item
 * [onValueChange] - logic that happens when another list item is selected on the screen
 * [dividersColor] - a parameter to select the color of the separator on the screen between list items
 * [list] - a parameter that contains a list of items to display on the screen
 * [textStyle] - list item display style
 * */
@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    dividersColor: Color,
    list: List<T>,
    textStyle: TextStyle,
) {
    val minimumAlpha = 0.3f
    val verticalMargin = 8.dp
    val numbersColumnHeight = 80.dp
    val halfNumbersColumnHeight = numbersColumnHeight / 2
    val halfNumbersColumnHeightPx = with(LocalDensity.current) { halfNumbersColumnHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            val index = list.indexOf(value)
            val offsetRange = remember(value, list) {
                -((list.count() - 1) - index) * halfNumbersColumnHeightPx to
                        index * halfNumbersColumnHeightPx
            }
            updateBounds(offsetRange.first, offsetRange.second)
        }

    val coercedAnimatedOffset = animatedOffset.value % halfNumbersColumnHeightPx

    val indexOfElement = getItemIndexForOffset(list, value, animatedOffset.value, halfNumbersColumnHeightPx)

    var dividersWidth by remember { mutableStateOf(0.dp) }

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnHeightPx
                                val coercedAnchors =
                                    listOf(
                                        -halfNumbersColumnHeightPx,
                                        0f,
                                        halfNumbersColumnHeightPx
                                    )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    halfNumbersColumnHeightPx * (target / halfNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val result = list.elementAt(
                            getItemIndexForOffset(list, value, endValue, halfNumbersColumnHeightPx)
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .padding(vertical = numbersColumnHeight / 3 + verticalMargin * 2),
        content = {
            Box(
                modifier
                    .width(dividersWidth)
                    .height(2.dp)
                    .background(color = dividersColor)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = verticalMargin, horizontal = 20.dp)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {
                    if (indexOfElement > 0)
                        Label(
                            text = label(list.elementAt(indexOfElement - 1)),
                            modifier = baseLabelModifier
                                .offset(y = -halfNumbersColumnHeight)
                                .alpha(
                                    maxOf(
                                        minimumAlpha,
                                        coercedAnimatedOffset / halfNumbersColumnHeightPx
                                    )
                                )
                        )
                    Label(
                        text = label(list.elementAt(indexOfElement)),
                        modifier = baseLabelModifier
                            .alpha(
                                (maxOf(
                                    minimumAlpha,
                                    1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                                ))
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray)
                    )
                    if (indexOfElement < list.count() - 1)
                        Label(
                            text = label(list.elementAt(indexOfElement + 1)),
                            modifier = baseLabelModifier
                                .offset(y = halfNumbersColumnHeight)
                                .alpha(
                                    maxOf(
                                        minimumAlpha,
                                        -coercedAnimatedOffset / halfNumbersColumnHeightPx
                                    )
                                )
                        )
                }
            }
            Box(
                modifier
                    .width(dividersWidth)
                    .height(2.dp)
                    .background(color = dividersColor)
            )
        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        dividersWidth = placeables
            .drop(1)
            .first()
            .width
            .toDp()

        // Set the size of the layout as big as it can
        layout(dividersWidth.toPx().toInt(), placeables
            .sumOf {
                it.height
            }
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

/**
 * Compose function that contains Text view with a string representation of the list item
 *
 * [modifier] - a parameter to change the parent Text according to the needs of the developer
 * [text] - the string representation of the list item to display
 * */
@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                // FIXME: Empty to disable text selection
            })
        },
        text = text,
        textAlign = TextAlign.Center,
    )
}

/**
 * A function that selects the type of animation depending on whether the adjustTarget parameter is null or not
 *
 * [initialVelocity] - animation display speed
 * [animationSpec] - defines the decay animation that will be used for this animation.
 * Some options for this animationSpec include: androidx.compose .animation.splineBasedDecay and exponentialDecay.
 * block will be invoked on each animation frame.
 * [adjustTarget] - the end result of the animation that the animation will aim for
 * [block] - block will be invoked on each animation frame.
 * */
private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}

/**
 * Preview of ListItemPicker with food list
 * */
@Preview
@Composable
fun ListItemPickerPreview() {
    val possibleValues = listOf("🍎", "🍊", "🍉", "🥭", "🍈", "🍇", "🍍")
    var state by remember { mutableStateOf(possibleValues[0]) }
    ListItemPicker(
        label = { it },
        value = state,
        onValueChange = { state = it },
        dividersColor = Color.Transparent,
        textStyle = TextStyle.Default.copy(color = Color.Black, fontSize = 16.sp),
        list = possibleValues
    )
}

/**
 * Preview of ListItemPicker with dates list
 * */
@Preview
@Composable
fun ListItemPickerPreviewDates() {

    val possibleValues = listOf(
        "10 декабря",
        "11 декабря",
        "12 декабря",
        "13 декабря",
        "14 декабря",
    )

    var state by remember { mutableStateOf(possibleValues[0]) }

    ListItemPicker(
        modifier = Modifier.fillMaxWidth(),
        label = { it },
        value = state,
        onValueChange = { state = it },
        dividersColor = Color.Transparent,
        textStyle = TextStyle.Default.copy(color = Color.Black, fontSize = 16.sp),
        list = possibleValues
    )
}
