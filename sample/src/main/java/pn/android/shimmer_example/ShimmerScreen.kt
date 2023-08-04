package pn.android.shimmer_example

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.valentinilk.shimmer.*
import pn.android.compose.components.calendar.clickable
import pn.android.core.R

const val oneFloat = 1f
const val zeroFloat = 0f
const val thirtyFourFloat = 34f
const val tenSeconds = 10000
const val sevenSeconds = 7000

@Destination
@Composable
fun ShimmerScreen(navigator: DestinationsNavigator) {
    val yourShimmerTheme = defaultShimmerTheme.copy(rotation = 225f)
    CompositionLocalProvider(
        LocalShimmerTheme provides yourShimmerTheme
    ) {

    }
    val shimmerWindowInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    val shimmerViewInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    val shimmerCustomInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Custom)
    val shimmerCustomThemeInstance =
        rememberShimmer(shimmerBounds = ShimmerBounds.Window, theme = yourShimmerTheme)

    val isClick = remember { mutableStateOf(false) }
    val isAnimated = remember { mutableStateOf(false) }

    val alphaValue = remember { androidx.compose.animation.core.Animatable(oneFloat) }
    val spValue = remember { androidx.compose.animation.core.Animatable(zeroFloat) }

    LaunchedEffect(isAnimated) {
        alphaValue.animateTo(
            if (isAnimated.value) zeroFloat else oneFloat,
            animationSpec = tween(tenSeconds)
        )
    }

    LaunchedEffect(isAnimated) {
        spValue.animateTo(
            if (isAnimated.value) thirtyFourFloat else zeroFloat,
            animationSpec = tween(sevenSeconds)
        )
    }

    isAnimated.value = true

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .align(Alignment.TopStart)
                .size(30.dp)
                .clickable { navigator.popBackStack() },
            painter = painterResource(id = R.drawable.ic_arrow_left_24_black),
            contentDescription = null,
            tint = Color.Black
        )
        if (!isClick.value) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.car),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable {
                        isClick.value = true
                    },
                color = Color.White,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.wasted_click),
                fontSize = spValue.value.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .alpha(alphaValue.value),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(oneFloat)
                        .shimmer()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color = Color.Blue)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(oneFloat)
                        .shimmer()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color = Color.Red)
                    )
                }
            }
        } else {
            Column(modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                    // Util function included in the library
                    val position = layoutCoordinates.unclippedBoundsInWindow()
                    shimmerCustomInstance.updateBounds(position)
                }
                .verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(40.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.normal_example),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                ExampleBlock(shimmerWindowInstance, "ShimmerBounds.Window")
                ExampleBlock(shimmerViewInstance, "ShimmerBounds.View")
                ExampleBlock(shimmerCustomInstance, "ShimmerBounds.Custom")
                ExampleBlock(
                    shimmerCustomThemeInstance,
                    "ShimmerBounds.Window, Custom theme radius"
                )
            }
        }
    }
}

@Composable
private fun ExampleBlock(shimmerInstance: Shimmer, title: String) {
    Text(
        modifier = Modifier
            .padding(start = 20.dp, bottom = 10.dp, end = 20.dp),
        color = Color.Black,
        textAlign = TextAlign.Start,
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Row(
        modifier = Modifier
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .shimmer(shimmerInstance)
                .size(100.dp)
                .weight(0.4f)
                .background(color = Color.Gray)
        )
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 10.dp)
                .align(Alignment.Top)
        ) {
            Text(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .fillMaxWidth()
                    .background(color = Color.Gray),
                color = Color.Black,
                textAlign = TextAlign.Center,
                text = "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .fillMaxWidth(0.5f)
                    .background(color = Color.Gray),
                color = Color.Black,
                textAlign = TextAlign.Center,
                text = "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}