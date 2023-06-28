package pn.android.compose.components.images.viewer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue

const val DEFAULT_OFFSET_X = 0F
const val DEFAULT_OFFSET_Y = 0F
const val DEFAULT_SCALE = 1F
const val DEFAULT_ROTATION = 0F

const val MIN_SCALE = 0.5F
const val MAX_SCALE_RATE = 3.2F

// Minimum distance between finger gestures
const val MIN_GESTURE_FINGER_DISTANCE = 200

class ImageViewerState(
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    scale: Float = DEFAULT_SCALE,
    rotation: Float = DEFAULT_ROTATION,
) : CoroutineScope by MainScope() {

    // x offset
    val offsetX = Animatable(offsetX)
    // у offset
    val offsetY = Animatable(offsetY)

    // Increasing the picture size
    val scale = Animatable(scale)
    // Rotation
    val rotation = Animatable(rotation)

    // Container size
    internal var containerSize by mutableStateOf(IntSize(0, 0))
    // Default display size
    internal var defaultSize by mutableStateOf(IntSize(0, 0))

    // Maximum zoom
    internal var maxScale by mutableStateOf(1F)

    // Whether the flag is from the splash screen, it will become true after the screen is rotated internal var fromSaver = false
    internal var fromSaver = false

    // Recovered timestamp
    internal var resetTimeStamp by mutableStateOf(0L)


    /**
     * Detect if animation is running
     * @return Boolean
     */
    internal fun isRunning(): Boolean {
        return scale.isRunning || offsetX.isRunning || offsetY.isRunning || rotation.isRunning
    }

    /**
     * Revert to default
     * */
    suspend fun reset() {
        coroutineScope {
            launch {
                rotation.animateTo(DEFAULT_ROTATION)
                resetTimeStamp = System.currentTimeMillis()
            }
            launch {
                offsetX.animateTo(DEFAULT_OFFSET_X)
                resetTimeStamp = System.currentTimeMillis()
            }
            launch {
                offsetY.animateTo(DEFAULT_OFFSET_Y)
                resetTimeStamp = System.currentTimeMillis()
            }
            launch {
                scale.animateTo(DEFAULT_SCALE)
                resetTimeStamp = System.currentTimeMillis()
            }
        }
    }

    /**
     * Zoom to the max
     */
    suspend fun scaleToMax(offset: Offset) {
        // Calculate the x and y offsets and dimensions and make sure you don't go out of bounds while zooming in
        var bcx = (containerSize.width / 2 - offset.x) * maxScale
        val boundX = getBound(defaultSize.width.toFloat() * maxScale, containerSize.width.toFloat())
        bcx = limitToBound(bcx, boundX)

        var bcy = (containerSize.height / 2 - offset.y) * maxScale
        val boundY =
            getBound(defaultSize.height.toFloat() * maxScale, containerSize.height.toFloat())
        bcy = limitToBound(bcy, boundY)
        // Start
        coroutineScope {
            launch {
                scale.animateTo(maxScale)
            }
            launch {
                offsetX.animateTo(bcx)
            }
            launch {
                offsetY.animateTo(bcy)
            }
        }

    }

    /**
     * Zoom in or out
     */
    suspend fun toggleScale(offset: Offset) {
        //If not equal to 1, set it back to 1
        if (scale.value != 1F) {
            reset()
        } else {
            scaleToMax(offset)
        }
    }

    /**
     * Limit zoom
     */
    suspend fun fixToBound() {
        val boundX =
            getBound(defaultSize.width.toFloat() * scale.value, containerSize.width.toFloat())
        val boundY =
            getBound(defaultSize.height.toFloat() * scale.value, containerSize.height.toFloat())
        val limitX = limitToBound(offsetX.value, boundX)
        val limitY = limitToBound(offsetY.value, boundY)
        offsetX.snapTo(limitX)
        offsetY.snapTo(limitY)
    }

    companion object {
        val SAVER: Saver<ImageViewerState, *> = listSaver(save = {
            listOf(it.offsetX.value, it.offsetY.value, it.scale.value, it.rotation.value)
        }, restore = {
            val state = ImageViewerState(
                offsetX = it[0],
                offsetY = it[1],
                scale = it[2],
                rotation = it[3],
            )
            state.fromSaver = true
            state
        })
    }
}

@Composable
fun rememberViewerState(
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    scale: Float = DEFAULT_SCALE,
    rotation: Float = DEFAULT_ROTATION,
): ImageViewerState = rememberSaveable(saver = ImageViewerState.SAVER) {
    ImageViewerState(offsetX, offsetY, scale, rotation)
}

/**
 * compose fun with box with image. The image can be zoomed by double tapping or tapping with two fingers at the same time and spreading them apart
 * [model] supports Painter、ImageBitmap、ImageVector、BitmapRegionDecoder
 */
@Composable
fun ImageViewer(
    modifier: Modifier = Modifier,
    model: Any,
    state: ImageViewerState = rememberViewerState(),
    onTap: (Offset) -> Unit = {},
    onDoubleTap: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    boundClip: Boolean = true,
    debugMode: Boolean = false,
    rotationEnabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    // Center position when touched
    var centroid by remember { mutableStateOf(Offset.Zero) }

    // Ease motion animation curve
    val decay = remember {
        FloatExponentialDecaySpec(2f).generateDecayAnimationSpec<Float>()
    }

    var velocityTracker = remember { VelocityTracker() }
    // Count the number of fingers in the touch event
    var eventChangeCount by remember { mutableStateOf(0) }
    // Last offset move
    var lastPan by remember { mutableStateOf(Offset.Zero) }

    // Realtime Gesture Offset Range
    var boundX by remember { mutableStateOf(0F) }
    var boundY by remember { mutableStateOf(0F) }

    // Maximum zoom speed, when double clicked it will increase to this value.
    var maxScale by remember { mutableStateOf(1F) }
    // Maximum display scaling speed. Once the zoom speed exceeds this value, it will automatically return to this value after the gesture ends.
    val maxDisplayScale by remember { derivedStateOf { maxScale * MAX_SCALE_RATE } }

    // Target offset
    var desX by remember { mutableStateOf(0F) }
    var desY by remember { mutableStateOf(0F) }

    var desScale by remember { mutableStateOf(1F) }
    // Value before rescaling
    var fromScale by remember { mutableStateOf(1F) }
    // Scale factor used to calculate borders
    var boundScale by remember { mutableStateOf(1F) }
    // Target Rotation Angle
    var desRotation by remember { mutableStateOf(0F) }

    // Add Angle To Rotate
    var rotate by remember { mutableStateOf(0F) }
    // Value for zoom
    var zoom by remember { mutableStateOf(1F) }

    // Distance between two fingers
    var fingerDistanceOffset by remember { mutableStateOf(Offset.Zero) }


    // Synchronize parameters des. When switching the image in the gallery after zooming out, gesture commands are still accepted, so the parameters after zooming out need to be synchronized
    fun asyncDesParams() {
        desX = state.offsetX.value
        desY = state.offsetY.value
        desScale = state.scale.value
        desRotation = state.rotation.value
    }

    LaunchedEffect(key1 = state.resetTimeStamp) {
        asyncDesParams()
    }

    val gesture = remember {
        RawGesture(
            onTap = onTap,
            onDoubleTap = onDoubleTap,
            onLongPress = onLongPress,
            gestureStart = {
                eventChangeCount = 0
                velocityTracker = VelocityTracker()
                scope.launch {
                    state.offsetX.stop()
                    state.offsetY.stop()
                    state.offsetX.updateBounds(null, null)
                    state.offsetY.updateBounds(null, null)
                }
                asyncDesParams()
            },
            gestureEnd = { transformOnly ->
                // transformOnly records if there is an offset in the gesture event, if it's just a click or double click it will return false
                // If it is in the animation, do not perform subsequent actions, such as: when executing the reset command
                if (transformOnly && !state.isRunning()) {

                    // Handle the case when the point added by the acceleration is empty
                    var velocity = try {
                        velocityTracker.calculateVelocity()
                    } catch (e: Exception) {
                        Timber.e(e)
                        null
                    }

                    // If the scale factor is less than 1, it will automatically revert to 1
                    // If the scaling factor is greater than the maximum display scaling factor, set it back and avoid speedup.
                    val scale = when {
                        state.scale.value < 1 -> 1F
                        state.scale.value > maxDisplayScale -> {
                            velocity = null
                            maxDisplayScale
                        }
                        else -> null
                    }

                    // If the offset is out of range at this time, animate back into the range
                    // If it's not out of range, set the animation range and then perform the throw animation.
                    scope.launch {
                        if (inBound(state.offsetX.value, boundX) && velocity != null) {
                            val vx = sameDirection(lastPan.x, velocity.x)
                            state.offsetX.updateBounds(-boundX, boundX)
                            state.offsetX.animateDecay(vx, decay)
                        } else {
                            val targetX = if (scale != maxDisplayScale) {
                                limitToBound(state.offsetX.value, boundX)
                            } else {
                                panTransformAndScale(
                                    offset = state.offsetX.value,
                                    center = centroid.x,
                                    bh = state.containerSize.width.toFloat(),
                                    uh = state.defaultSize.width.toFloat(),
                                    fromScale = state.scale.value,
                                    toScale = scale,
                                )
                            }
                            state.offsetX.animateTo(targetX)
                        }
                    }

                    scope.launch {
                        if (inBound(state.offsetY.value, boundY) && velocity != null) {
                            val vy = sameDirection(lastPan.y, velocity.y)
                            state.offsetY.updateBounds(-boundY, boundY)
                            state.offsetY.animateDecay(vy, decay)
                        } else {
                            val targetY = if (scale != maxDisplayScale) {
                                limitToBound(state.offsetY.value, boundY)
                            } else {
                                panTransformAndScale(
                                    offset = state.offsetY.value,
                                    center = centroid.y,
                                    bh = state.containerSize.height.toFloat(),
                                    uh = state.defaultSize.height.toFloat(),
                                    fromScale = state.scale.value,
                                    toScale = scale,
                                )
                            }
                            state.offsetY.animateTo(targetY)
                        }
                    }

                    scope.launch {
                        state.rotation.animateTo(0F)
                    }

                    scale?.let {
                        scope.launch {
                            state.scale.animateTo(scale)
                        }
                    }

                }
            },
        ) { center, pan, _zoom, _rotate, event ->
            // Only the maximum number of fingers is recorded here
            if (event.changes.size > eventChangeCount) eventChangeCount = event.changes.size
            // If the number of fingers changes from multiple to one, end the gesture operation.
            if (eventChangeCount > event.changes.size) return@RawGesture false

            rotate = _rotate
            zoom = _zoom

            // In the case of two fingers, when the distance between the fingers is less than a certain value, the zoom and rotate values will be at the maximum, so don't bother zooming and rotating in this case
            if (event.changes.size == 2) {
                fingerDistanceOffset = event.changes[0].position - event.changes[1].position
                if (
                    fingerDistanceOffset.x.absoluteValue < MIN_GESTURE_FINGER_DISTANCE &&
                    fingerDistanceOffset.y.absoluteValue < MIN_GESTURE_FINGER_DISTANCE
                ) {
                    rotate = 0F
                    zoom = 1F
                }
            }

            // Last offset
            lastPan = pan
            // The middle of the gesture
            centroid = center
            // Current scaling factor
            fromScale = desScale
            // Target increase
            desScale *= zoom

            // Check the minimum increase
            if (desScale < MIN_SCALE) desScale = MIN_SCALE

            // Calculate border. If the target scaling value is greater than the maximum display scaling value, the border will be calculated with the maximum scaling value, otherwise it will fail to return when the gesture ends.
            boundScale = if (desScale > maxDisplayScale) maxDisplayScale else desScale
            boundX =
                getBound(boundScale * state.defaultSize.width, state.containerSize.width.toFloat())
            boundY =
                getBound(
                    boundScale * state.defaultSize.height,
                    state.containerSize.height.toFloat()
                )

            desX = panTransformAndScale(
                offset = desX,
                center = center.x,
                bh = state.containerSize.width.toFloat(),
                uh = state.defaultSize.width.toFloat(),
                fromScale = fromScale,
                toScale = desScale,
            ) + pan.x


            // If the screen touches are 1 at the same time, it is being dragged and the drag is limited to a range
            // If there are more than 1 touches on the screen at the same time, i.e. there is a zoom event, scaling of the center point is supported.
            if (eventChangeCount == 1) desX = limitToBound(desX, boundX)
            desY = panTransformAndScale(
                offset = desY,
                center = center.y,
                bh = state.containerSize.height.toFloat(),
                uh = state.defaultSize.height.toFloat(),
                fromScale = fromScale,
                toScale = desScale,
            ) + pan.y
            if (eventChangeCount == 1) desY = limitToBound(desY, boundY)

            if (rotationEnabled) {
                if (desScale < 1) desRotation += rotate
            }
            velocityTracker.addPosition(
                event.changes[0].uptimeMillis,
                Offset(desX, desY),
            )
            if (!state.isRunning()) scope.launch {
                state.scale.snapTo(desScale)
                state.offsetX.snapTo(desX)
                state.offsetY.snapTo(desY)
                state.rotation.snapTo(desRotation)
            }

            // This evaluates if it has moved to the border, if it has reached the border the event will not be consumed and the top interface will receive the event
            val onLeft = desX >= boundX
            val onRight = desX <= -boundX
            val reachSide = !(onLeft && pan.x > 0) && !(onRight && pan.x < 0) && !(onLeft && onRight)
            if (reachSide || state.scale.value < 1) {
                event.changes.forEach {
                    if (it.positionChanged()) {
                        it.consume()
                    }
                }
            }
            // Return true to move on to the next gesture
            return@RawGesture true
        }
    }
    val sizeChange: suspend (SizeChangeContent) -> Unit = { content ->
        maxScale = content.maxScale
        state.defaultSize = content.defaultSize
        state.containerSize = content.containerSize
        state.maxScale = content.maxScale
        if (state.fromSaver) {
            state.fromSaver = false
            state.fixToBound()
        }
    }
    Box(modifier = modifier) {
        when (model) {
            is Painter,
            is ImageVector,
            is ImageBitmap,
            -> {
                ImageComposeOrigin(
                    model = model,
                    scale = state.scale.value,
                    offsetX = state.offsetX.value,
                    offsetY = state.offsetY.value,
                    rotation = state.rotation.value,
                    gesture = gesture,
                    onSizeChange = sizeChange,
                    boundClip = boundClip,
                )
            }
            is ImageDecoder -> {
                ImageComposeCanvas(
                    imageDecoder = model,
                    scale = state.scale.value,
                    offsetX = state.offsetX.value,
                    offsetY = state.offsetY.value,
                    rotation = state.rotation.value,
                    gesture = gesture,
                    onSizeChange = sizeChange,
                    boundClip = boundClip,
                )
            }
            else -> {
                throw Exception("Unrecognized model: ${model::class.java.name}")
            }
        }
        if (debugMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10F)
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = centroid.x - 6.dp.toPx()
                            translationY = centroid.y - 6.dp.toPx()
                        }
                        .clip(CircleShape)
                        .background(Color.Red.copy(0.4f))
                        .size(12.dp)
                )
            }
        }
    }
}

/**
 * Overriding the event listener method
 * */
suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    gestureStart: () -> Unit = {},
    gestureEnd: (Boolean) -> Unit = {},
    onTap: (Offset) -> Unit = {},
    onDoubleTap: (Offset) -> Unit = {},
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float, event: PointerEvent) -> Boolean,
) {
    var lastReleaseTime = 0L
    var scope: CoroutineScope? = null
    forEachGesture {
        awaitPointerEventScope {
            var rotation = 0f
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var lockedToPanZoom = false

            awaitFirstDown(requireUnconsumed = false)
            val t0 = System.currentTimeMillis()
            var releasedEvent: PointerEvent? = null
            var moveCount = 0
            // Start action
            gestureStart()
            do {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Release) releasedEvent = event
                if (event.type == PointerEventType.Move) moveCount++
                val canceled = event.changes.any { it.isConsumed }
                if (!canceled) {
                    val zoomChange = event.calculateZoom()
                    val rotationChange = event.calculateRotation()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        rotation += rotationChange
                        pan += panChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                        val panMotion = pan.getDistance()

                        if (zoomMotion > touchSlop ||
                            rotationMotion > touchSlop ||
                            panMotion > touchSlop
                        ) {
                            pastTouchSlop = true
                            lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                        }
                    }
                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                        if (effectiveRotation != 0f ||
                            zoomChange != 1f ||
                            panChange != Offset.Zero
                        ) {
                            if (!onGesture(
                                    centroid,
                                    panChange,
                                    zoomChange,
                                    effectiveRotation,
                                    event
                                )
                            ) break
                        }
                    }
                }
            } while (!canceled && event.changes.any { it.pressed })

            var t1 = System.currentTimeMillis()
            val dt = t1 - t0
            val dlt = t1 - lastReleaseTime

            if (moveCount == 0) releasedEvent?.let { e ->
                if (e.changes.isEmpty()) return@let
                val offset = e.changes.first().position
                if (dlt < 272) {
                    t1 = 0L
                    scope?.cancel()
                    onDoubleTap(offset)
                } else if (dt < 200) {
                    scope = MainScope()
                    scope?.launch(Dispatchers.Main) {
                        delay(272)
                        onTap(offset)
                    }
                }
                lastReleaseTime = t1
            }

            // End of action
            gestureEnd(moveCount != 0)
        }
    }
}

/**
 * Make the sign of the next number match the sign of the previous number
 * @param a Float
 * @param b Float
 * @return Float
 */
fun sameDirection(a: Float, b: Float): Float {
    return if (a > 0) {
        if (b < 0) {
            b.absoluteValue
        } else {
            b
        }
    } else {
        if (b > 0) {
            -b
        } else {
            b
        }
    }
}

/**
 * Get moving border
 * */
fun getBound(rw: Float, bw: Float): Float {
    return if (rw > bw) {
        var xb = (rw - bw).div(2)
        if (xb < 0) xb = 0F
        xb
    } else {
        0F
    }
}

/**
 * Determine if offset is within bounds
 * */
fun inBound(offset: Float, bound: Float): Boolean {
    return if (offset > 0) {
        offset < bound
    } else if (offset < 0) {
        offset > -bound
    } else {
        true
    }
}

/**
 * Restrict movement within boundaries
 */
fun limitToBound(offset: Float, bound: Float): Float {
    return when {
        offset > bound -> {
            bound
        }
        offset < -bound -> {
            -bound
        }
        else -> {
            offset
        }
    }
}

/**
 * Tracking the center point while zooming
 */
fun panTransformAndScale(
    offset: Float,
    center: Float,
    bh: Float,
    uh: Float,
    fromScale: Float,
    toScale: Float,
): Float {
    val srcH = uh * fromScale
    val desH = uh * toScale
    val gapH = (bh - uh) / 2

    val py = when {
        uh >= bh -> {
            val upy = (uh * fromScale - uh).div(2)
            (upy - offset + center) / (fromScale * uh)
        }
        srcH > bh || bh > uh -> {
            val upy = (srcH - uh).div(2)
            (upy - gapH - offset + center) / (fromScale * uh)
        }
        else -> {
            val upy = -(bh - srcH).div(2)
            (upy - offset + center) / (fromScale * uh)
        }
    }
    return when {
        uh >= bh -> {
            val upy = (uh * toScale - uh).div(2)
            upy + center - py * toScale * uh
        }
        desH > bh -> {
            val upy = (desH - uh).div(2)
            upy - gapH + center - py * toScale * uh
        }
        else -> {
            val upy = -(bh - desH).div(2)
            upy + center - py * desH
        }
    }
}