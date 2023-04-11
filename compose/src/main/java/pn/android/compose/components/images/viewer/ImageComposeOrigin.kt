package pn.android.compose.components.images.viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

class RawGesture(
    val onTap: (Offset) -> Unit = {},
    val onDoubleTap: (Offset) -> Unit = {},
    val onLongPress: (Offset) -> Unit = {},
    val gestureStart: () -> Unit = {},
    val gestureEnd: (transformOnly: Boolean) -> Unit = {},
    val onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        event: PointerEvent
    ) -> Boolean = { _, _, _, _, _ -> true },
)

data class SizeChangeContent(
    val defaultSize: IntSize,
    val containerSize: IntSize,
    val maxScale: Float,
)

@Composable
fun ImageComposeOrigin(
    modifier: Modifier = Modifier,
    model: Any,
    scale: Float = DEFAULT_SCALE,
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    rotation: Float = DEFAULT_ROTATION,
    gesture: RawGesture = RawGesture(),
    onSizeChange: suspend (SizeChangeContent) -> Unit = {},
    boundClip: Boolean = true,
) {
    var bSize by remember { mutableStateOf(IntSize(0, 0)) }
    val bRatio by remember { derivedStateOf { bSize.width.toFloat() / bSize.height.toFloat() } }

    var oSize by remember { mutableStateOf(IntSize(0, 0)) }
    val oRatio by remember { derivedStateOf { oSize.width.toFloat() / oSize.height.toFloat() } }

    var widthFixed by remember { mutableStateOf(false) }

    val superSize by remember {
        derivedStateOf {
            oSize.height > bSize.height && oSize.width > bSize.width
        }
    }

    val uSize by remember {
        derivedStateOf {
            if (oRatio > bRatio) {
                // Постоянная ширина
                val uW = bSize.width
                val uH = uW / oRatio
                widthFixed = true
                IntSize(uW, uH.toInt())
            } else {
                //
                val uH = bSize.height
                val uW = uH * oRatio
                widthFixed = false
                IntSize(uW.toInt(), uH)
            }
        }
    }

    val rSize by remember {
        derivedStateOf {
            IntSize(
                (uSize.width * scale).toInt(),
                (uSize.height * scale).toInt()
            )
        }
    }

    LaunchedEffect(key1 = oSize, key2 = bSize, key3 = rSize) {
        val maxScale = when {
            superSize -> {
                oSize.width.toFloat() / uSize.width.toFloat()
            }
            widthFixed -> {
                bSize.height.toFloat() / uSize.height.toFloat()
            }
            else -> {
                bSize.width.toFloat() / uSize.width.toFloat()
            }
        }
        onSizeChange(
            SizeChangeContent(
                defaultSize = uSize,
                containerSize = bSize,
                maxScale = maxScale
            )
        )
    }

    var imageSpecified by remember { mutableStateOf(false) }

    when (model) {
        is Painter -> {
            imageSpecified = model.intrinsicSize.isSpecified
            LaunchedEffect(key1 = model.intrinsicSize, block = {
                if (imageSpecified) {
                    oSize = IntSize(
                        model.intrinsicSize.width.toInt(),
                        model.intrinsicSize.height.toInt()
                    )
                }
            })
        }
        is ImageVector -> {
            imageSpecified = true
            LocalDensity.current.run {
                oSize = IntSize(
                    model.defaultWidth.toPx().toInt(),
                    model.defaultHeight.toPx().toInt(),
                )
            }
        }
        is ImageBitmap -> {
            imageSpecified = true
            oSize = IntSize(
                model.width,
                model.height
            )
        }
        else -> throw Exception("不支持这种类型的数据！")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                clip = boundClip
            }
            .onSizeChanged {
                bSize = it
            }
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = gesture.onLongPress)
            }
            .pointerInput(key1 = imageSpecified) {
                if (imageSpecified) detectTransformGestures(
                    onTap = gesture.onTap,
                    onDoubleTap = gesture.onDoubleTap,
                    gestureStart = gesture.gestureStart,
                    gestureEnd = gesture.gestureEnd,
                    onGesture = gesture.onGesture,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        val imageModifier = Modifier
            .graphicsLayer {
                if (imageSpecified) {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                    rotationZ = rotation
                }
            }
            .size(
                LocalDensity.current.run { uSize.width.toDp() },
                LocalDensity.current.run { uSize.height.toDp() }
            )
        when (model) {
            is Painter -> {
                Image(
                    painter = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }
            is ImageVector -> {
                Image(
                    imageVector = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }
            is ImageBitmap -> {
                Image(
                    bitmap = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }
        }

    }
}