package pn.android.compose.components.images.viewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.absoluteValue
import kotlin.math.ceil
data class RenderBlock(
    var inBound: Boolean = false,
    var inSampleSize: Int = 1,
    var renderOffset: IntOffset = IntOffset.Zero,
    var renderSize: IntSize = IntSize.Zero,
    var sliceRect: Rect = Rect(0, 0, 0, 0),
    var bitmap: Bitmap? = null,
)

class ImageDecoder(
    private val decoder: BitmapRegionDecoder,
    private val onRelease: () -> Unit = {},
) : CoroutineScope by MainScope() {

    var decoderWidth by mutableStateOf(0)
        private set

    var decoderHeight by mutableStateOf(0)
        private set
    /**
     * Decoding block size
     * */
    var blockSize by mutableStateOf(0)
        private set

    var renderList: Array<Array<RenderBlock>> = emptyArray()
        private set

    val renderQueue = LinkedBlockingDeque<RenderBlock>()

    /**
     * Number of horizontal blocks in the gallery
     * */
    private var countW = 0

    /**
     * Number of vertical blocks in the gallery
     * */
    private var countH = 0

    /**
     * Maximum number of blocks on the longest side
     * */
    private var maxBlockCount = 0

    init {
        /**
         * Initialize the maximum number of blocks in the gallery
         * */
        setMaxBlockCount(1)
    }

    /**
     * Build a queue of render blocks
     * */
    private fun getRenderBlockList(): Array<Array<RenderBlock>> {
        var endX: Int
        var endY: Int
        var sliceStartX: Int
        var sliceStartY: Int
        var sliceEndX: Int
        var sliceEndY: Int
        return Array(countH) { column ->
            sliceStartY = (column * blockSize)
            endY = (column + 1) * blockSize
            sliceEndY = if (endY > decoderHeight) decoderHeight else endY
            Array(countW) { row ->
                sliceStartX = (row * blockSize)
                endX = (row + 1) * blockSize
                sliceEndX = if (endX > decoderWidth) decoderWidth else endX
                RenderBlock(
                    sliceRect = Rect(
                        sliceStartX,
                        sliceStartY,
                        sliceEndX,
                        sliceEndY,
                    )
                )
            }
        }
    }

    /**
     * Set the maximum number of blocks on the longest side
     * */
    fun setMaxBlockCount(count: Int): Boolean {
        if (maxBlockCount == count) return false
        maxBlockCount = count
        decoderWidth = decoder.width
        decoderHeight = decoder.height
        blockSize =
            (decoderWidth.coerceAtLeast(decoderHeight)).toFloat().div(count).toInt()
        countW = ceil(decoderWidth.toFloat().div(blockSize)).toInt()
        countH = ceil(decoderHeight.toFloat().div(blockSize)).toInt()
        renderList = getRenderBlockList()
        return true
    }

    /**
     * Iterate over each render unit
     * */
    fun forEachBlock(action: (block: RenderBlock, column: Int, row: Int) -> Unit) {
        for ((column, rows) in renderList.withIndex()) {
            for ((row, block) in rows.withIndex()) {
                action(block, column, row)
            }
        }
    }

    /**
     * Remove all references to the bitmap
     * */
    fun clearAllBitmap() {
        forEachBlock { block, _, _ ->
            block.bitmap = null
        }
    }

    /**
     * Free up resources
     * */
    fun release() {
        synchronized(decoder) {
            if (!decoder.isRecycled) {
                renderQueue.clear()
                decoder.recycle()
                renderQueue.putFirst(RenderBlock())
            }
            onRelease()
        }
    }

    /**
     * decode render region
     */
    fun decodeRegion(inSampleSize: Int, rect: Rect): Bitmap? {
        synchronized(decoder) {
            return try {
                val ops = BitmapFactory.Options()
                ops.inSampleSize = inSampleSize
                if (decoder.isRecycled) return null
                decoder.decodeRegion(rect, ops)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    /**
     * Run a loop that start render in the queue
     */
    fun startRenderQueue(onUpdate: () -> Unit) {
        launch(Dispatchers.IO) {
            try {
                while (!decoder.isRecycled) {
                    val block = renderQueue.take()
                    if (decoder.isRecycled) break
                    block.bitmap = decodeRegion(block.inSampleSize, block.sliceRect)
                    onUpdate()
                }
            } catch (e: InterruptedException) {
                Timber.e(e)
            }
        }
    }
}

/**
 * Run a loop that start render in the queue
 */
@Composable
fun ImageComposeCanvas(
    modifier: Modifier = Modifier,
    imageDecoder: ImageDecoder,
    scale: Float = DEFAULT_SCALE,
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    rotation: Float = DEFAULT_ROTATION,
    gesture: RawGesture = RawGesture(),
    onSizeChange: suspend (SizeChangeContent) -> Unit = {},
    boundClip: Boolean = true,
    debugMode: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    /**
     * Container size
     * */
    var bSize by remember { mutableStateOf(IntSize.Zero) }

    /**
     * Container aspect ratio
     * */
    val bRatio by remember { derivedStateOf { bSize.width.toFloat() / bSize.height.toFloat() } }

    /**
     * Original aspect ratio of the image
     * */
    val oRatio by remember { derivedStateOf { imageDecoder.decoderWidth.toFloat() / imageDecoder.decoderHeight.toFloat() } }

    /**
     * Does the width match the size of the container
     * */
    var widthFixed by remember { mutableStateOf(false) }

    /**
     * Whether the length and width of the image exceed the length and width of the container
     * */
    val superSize by remember {
        derivedStateOf {
            imageDecoder.decoderHeight > bSize.height && imageDecoder.decoderWidth > bSize.width
        }
    }

    /**
     * Default display size
     * */
    val uSize by remember {
        derivedStateOf {
            if (oRatio > bRatio) {
                // constant width
                val uW = bSize.width
                val uH = uW / oRatio
                widthFixed = true
                IntSize(uW, uH.toInt())
            } else {
                // constant height
                val uH = bSize.height
                val uW = uH * oRatio
                widthFixed = false
                IntSize(uW.toInt(), uH)
            }
        }
    }

    /**
     * Actual display size
     * */
    val rSize by remember(key1 = scale) {
        derivedStateOf {
            IntSize(
                (uSize.width * scale).toInt(),
                (uSize.height * scale).toInt()
            )
        }
    }

    /**
     * Track container and actual image resizing at the same time
     * */
    LaunchedEffect(key1 = bSize, key2 = rSize) {
        //Get the maximum scaling factor
        val maxScale = when {
            superSize -> {
                imageDecoder.decoderWidth.toFloat() / uSize.width.toFloat()
            }
            widthFixed -> {
                bSize.height.toFloat() / uSize.height.toFloat()
            }
            else -> {
                bSize.width.toFloat() / uSize.width.toFloat()
            }
        }

        //Callback
        onSizeChange(
            SizeChangeContent(
                defaultSize = uSize,
                containerSize = bSize,
                maxScale = maxScale
            )
        )
    }

    /**
     * Determine if high quality rendering is required
     * */
    val needRenderHeightTexture by remember(key1 = bSize) {
        derivedStateOf {
            // Current strategy: The area of the source image is larger than the area of the container, so it is necessary to render high-quality images
            BigDecimal(imageDecoder.decoderWidth)
                .multiply(BigDecimal(imageDecoder.decoderHeight)) > BigDecimal(bSize.height)
                .multiply(BigDecimal(bSize.width))
        }
    }

    /**
     * Indicates whether high quality rendering is currently enabled, whether high quality rendering is required, and whether a scale greater than 1 is required.
     */
    val renderHeightTexture by remember(key1 = scale) { derivedStateOf { needRenderHeightTexture && scale > 1 } }

    /**
     * Current sample rate
     * */
    var inSampleSize by remember { mutableStateOf(1) }

    /**
     * Sampling rate of the smallest graph
     * */
    var zeroInSampleSize by remember { mutableStateOf(8) }

    /**
     * Background sample rate
     * */
    var backGroundInSample by remember { mutableStateOf(0) }

    /**
     * Background bitmap
     * */
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    /**
     * Keep track of the actual render size and dynamically change the image sample rate
     * */
    LaunchedEffect(key1 = rSize) {
        if (scale < 1F) return@LaunchedEffect
        inSampleSize = calculateInSampleSize(
            srcWidth = imageDecoder.decoderWidth,
            reqWidth = rSize.width
        )
        if (scale == 1F) {
            zeroInSampleSize = inSampleSize
        }
    }

    /**
     * The background updates in real time as the sample rate changes.
     * */
    LaunchedEffect(key1 = zeroInSampleSize, key2 = inSampleSize, key3 = needRenderHeightTexture) {
        scope.launch(Dispatchers.IO) {
            // If you don't need to render high quality images, you don't need to render in chunks, just use the current sample rate and use a basemap to render
            val iss = if (needRenderHeightTexture) zeroInSampleSize else inSampleSize
            if (iss == backGroundInSample) return@launch
            backGroundInSample = iss
            bitmap = imageDecoder.decodeRegion(
                inSampleSize = iss,
                rect = Rect(
                    0,
                    0,
                    imageDecoder.decoderWidth,
                    imageDecoder.decoderHeight
                )
            )
        }
    }

    /**
     * The background X offset to make sure the image is in the center of the container.
     * */
    val deltaX by remember(key1 = offsetX, key2 = bSize, key3 = rSize) {
        derivedStateOf {
            offsetX + (bSize.width - rSize.width).toFloat().div(2)
        }
    }

    /**
     * The background Y offset to make sure the image is in the center of the container.
     * */
    val deltaY by remember(key1 = offsetY, key2 = bSize, key3 = rSize) {
        derivedStateOf {
            offsetY + (bSize.height - rSize.height).toFloat().div(2)
        }
    }

    /**
     * Calculates the width of the rectangle in the display area
     * */
    val rectW by remember(key1 = offsetX) {
        derivedStateOf {
            calcLeftSize(
                bSize = bSize.width.toFloat(),
                rSize = rSize.width.toFloat(),
                offset = offsetX,
            )
        }
    }

    /**
     * Calculates the height of the rectangle in the display area
     * */
    val rectH by remember(key1 = offsetY, key2 = rSize) {
        derivedStateOf {
            calcLeftSize(
                bSize = bSize.height.toFloat(),
                rSize = rSize.height.toFloat(),
                offset = offsetY,
            )
        }
    }

    /**
     * Starting X-coordinate of the visible render area
     * */
    val stX by remember(key1 = offsetX) {
        derivedStateOf {
            val rectDeltaX = getRectDelta(
                deltaX,
                rSize.width.toFloat(),
                bSize.width.toFloat(),
                offsetX
            )
            rectDeltaX - deltaX
        }
    }

    /**
     * Starting Y-coordinate of the visible render area
     * */
    val stY by remember(key1 = offsetY) {
        derivedStateOf {
            val rectDeltaY = getRectDelta(
                deltaY,
                rSize.height.toFloat(),
                bSize.height.toFloat(),
                offsetY
            )
            rectDeltaY - deltaY
        }
    }

    /**
     * End X-coordinate calculation
     * */
    val edX by remember(key1 = offsetX) { derivedStateOf { stX + rectW } }
    /**
     * End Y-coordinate calculation
     * */
    val edY by remember(key1 = offsetY) { derivedStateOf { stY + rectH } }

    /**
     * The update timestamp used to notify the canvas that the square has been updated.
     * */
    var renderUpdateTimeStamp by remember { mutableStateOf(0L) }
    /**
     * Run decode queue loop
     * */
    LaunchedEffect(key1 = Unit) {
        imageDecoder.startRenderQueue {
            renderUpdateTimeStamp = System.currentTimeMillis()
        }
    }

    /**
     * When switching to high-quality rendering, you need to clear the decoding queue and clear all bitmaps
     * */
    LaunchedEffect(key1 = renderHeightTexture) {
        if (!renderHeightTexture) {
            imageDecoder.renderQueue.clear()
            imageDecoder.clearAllBitmap()
        }
    }

    /**
     * When switching to high-quality rendering, you need to clear the decoding queue and clear all bitmaps
     * */
    var calcMaxCountPending by remember { mutableStateOf(false) }

    /**
     * Previous scale factor
     * */
    var previousScale by remember { mutableStateOf<Float?>(null) }

    /**
     * Previous offset
     * */
    var previousOffset by remember { mutableStateOf<Offset?>(null) }

    /**
     * Update render unit information
     * */
    fun updateRenderList() {
        if (calcMaxCountPending) return
        if (previousOffset?.x == offsetX &&
            previousOffset?.y == offsetY &&
            previousScale == scale
        ) return
        previousScale = scale
        previousOffset = Offset(offsetX, offsetY)
        val renderBlockSize =
            imageDecoder.blockSize * (rSize.width.toFloat().div(imageDecoder.decoderWidth))
        var tlx: Int
        var tly: Int
        var startX: Float
        var startY: Float
        var endX: Float
        var endY: Float
        var eh: Int
        var ew: Int
        var needUpdate: Boolean
        var previousInBound: Boolean
        var previousInSampleSize: Int
        var lastX: Int?
        var lastY: Int? = null
        var lastXDelta: Int
        var lastYDelta: Int
        val insertList = ArrayList<RenderBlock>()
        val removeList = ArrayList<RenderBlock>()
        for ((column, list) in imageDecoder.renderList.withIndex()) {
            startY = column * renderBlockSize
            endY = (column + 1) * renderBlockSize
            tly = (deltaY + startY).toInt()
            eh = (if (endY > rSize.height) rSize.height - startY else renderBlockSize).toInt()
            lastY?.let {
                if (it < tly) {
                    lastYDelta = tly - it
                    tly = it
                    eh += lastYDelta
                }
            }
            lastY = tly + eh
            lastX = null
            for ((row, block) in list.withIndex()) {
                startX = row * renderBlockSize
                tlx = (deltaX + startX).toInt()
                endX = (row + 1) * renderBlockSize
                ew = (if (endX > rSize.width) rSize.width - startX else renderBlockSize).toInt()
                previousInSampleSize = block.inSampleSize
                previousInBound = block.inBound
                block.inSampleSize = inSampleSize
                block.inBound = checkRectInBound(startX, startY, endX, endY, stX, stY, edX, edY)
                lastX?.let {
                    if (it < tlx) {
                        lastXDelta = tlx - it
                        tlx = it
                        ew += lastXDelta
                    }
                }
                lastX = tlx + ew
                block.renderOffset = IntOffset(tlx, tly)
                block.renderSize = IntSize(
                    width = ew,
                    height = eh,
                )
                needUpdate = previousInBound != block.inBound ||
                        previousInSampleSize != block.inSampleSize
                if (!needUpdate) continue
                if (!renderHeightTexture) continue
                if (block.inBound) {
                    if (!imageDecoder.renderQueue.contains(block)) {
                        insertList.add(block)
                    }
                } else {
                    removeList.add(block)
                    block.bitmap = null
                }
            }
        }
        scope.launch(Dispatchers.IO) {
            synchronized(imageDecoder.renderQueue) {
                insertList.forEach {
                    imageDecoder.renderQueue.putFirst(it)
                }
                removeList.forEach {
                    imageDecoder.renderQueue.remove(it)
                }
            }
        }
    }

    var blockDividerCount by remember { mutableStateOf(1) }

    LaunchedEffect(key1 = rSize, key2 = rectW, key3 = rectH) {

        val rectArea = BigDecimal(rectW.toDouble()).multiply(BigDecimal(rectH.toDouble()))
        val realArea = BigDecimal(rSize.width).multiply(BigDecimal(rSize.height))
        if (realArea.toFloat() == 0F) return@LaunchedEffect

        val renderAreaPercentage =
            rectArea.divide(realArea, 2, RoundingMode.HALF_EVEN).toFloat()

        val goBlockDividerCount = when {
            renderAreaPercentage > 0.6F -> 1
            renderAreaPercentage > 0.025F -> 4
            else -> 8
        }

        if (goBlockDividerCount == blockDividerCount) return@LaunchedEffect
        blockDividerCount = goBlockDividerCount

        scope.launch(Dispatchers.IO) {
            imageDecoder.renderQueue.clear()
            calcMaxCountPending = true
            imageDecoder.setMaxBlockCount(blockDividerCount)
            calcMaxCountPending = false
            updateRenderList()
        }

    }

    /**
     * Center of rotation
     * */
    val rotationCenter by remember(key1 = offsetX, key2 = offsetY, key3 = scale) {
        derivedStateOf {
            val cx = deltaX + rSize.width.div(2)
            val cy = deltaY + rSize.height.div(2)
            Offset(cx, cy)
        }
    }
    Canvas(
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
            .pointerInput(Unit) {
                detectTransformGestures(
                    onTap = gesture.onTap,
                    onDoubleTap = gesture.onDoubleTap,
                    gestureStart = gesture.gestureStart,
                    gestureEnd = gesture.gestureEnd,
                    onGesture = gesture.onGesture,
                )
            },
    ) {
        withTransform({
            rotate(degrees = rotation, pivot = rotationCenter)
        }) {
            bitmap?.let {
                drawImage(
                    image = it.asImageBitmap(),
                    dstSize = IntSize(rSize.width, rSize.height),
                    dstOffset = IntOffset(deltaX.toInt(), deltaY.toInt())
                )
            }
            if (renderUpdateTimeStamp >= 0) updateRenderList()
            if (renderHeightTexture && !calcMaxCountPending) {
                imageDecoder.forEachBlock { block, _, _ ->
                    block.bitmap?.let {
                        drawImage(
                            image = it.asImageBitmap(),
                            dstSize = block.renderSize,
                            dstOffset = block.renderOffset
                        )
                    }
                }
            }

            if (debugMode) {
                drawRect(
                    color = Color.Blue.copy(0.1F),
                    topLeft = Offset(deltaX + stX, deltaY + stY),
                    size = Size(rectW, rectH)
                )
            }
        }
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            imageDecoder.release()
        }
    }

}

fun checkRectInBound(
    stX1: Float,
    stY1: Float,
    edX1: Float,
    edY1: Float,
    stX2: Float,
    stY2: Float,
    edX2: Float,
    edY2: Float,
): Boolean {
    if (edY1 < stY2) return false
    if (stY1 > edY2) return false
    if (edX1 < stX2) return false
    if (stX1 > edX2) return false
    return true
}

fun getRectDelta(delta: Float, rSize: Float, bSize: Float, offset: Float): Float {
    return delta + if (delta < 0) {
        val direction = if (rSize > bSize) -1 else 1
        (offset + (direction) * (bSize - rSize).div(2).absoluteValue).absoluteValue
    } else 0F
}

//Calculates the height of a rectangle in the display area
fun calcLeftSize(bSize: Float, rSize: Float, offset: Float): Float {
    return if (offset.absoluteValue > (bSize - rSize).div(2).absoluteValue) {
        rSize - (offset.absoluteValue - (bSize - rSize).div(2))
    } else {
        rSize.coerceAtMost(bSize)
    }
}

fun calculateInSampleSize(
    srcWidth: Int,
    reqWidth: Int,
): Int {
    var inSampleSize = 1
    while (true) {
        val iss = inSampleSize * 2
        if (srcWidth.toFloat().div(iss) < reqWidth) break
        inSampleSize = iss
    }
    return inSampleSize
}