package pn.android.compose.components.blur

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.cloudy.Cloudy

var radius = 0f

/**
 * Cloudy function can add blur to views in Cloudy content
 */
@Composable
fun BlurExample() {
    var radiusValueBySlider by remember { mutableStateOf(0f) }
    var key1Value by remember { mutableStateOf(0f) }
    var key2Value by remember { mutableStateOf(0f) }
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedRadius by animateIntAsState(
        targetValue = if (animationPlayed) 10 else 0,
        animationSpec = tween(
            durationMillis = 3000,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(12))
                    .padding(10.dp)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Blur changing by mutableState in radius:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Slider(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    value = radiusValueBySlider,
                    onValueChange = { value -> radiusValueBySlider = value },
                    valueRange = 0f..25f
                )
                Cloudy(
                    modifier = Modifier.padding(top = 20.dp),
                    radius = castFloatToInt(radiusValueBySlider),
                ) {
                    GradientBlock(modifier = Modifier, text = "Blur 1")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(12))
                    .padding(10.dp)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Blur changing by key1, key2:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Button(modifier = Modifier.weight(0.45f), onClick = {
                        radius = 25f
                        key1Value++
                    }) {
                        Text(
                            text = "Changing state by giving key1 parameter(25f radius)",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                    Button(modifier = Modifier.weight(0.45f), onClick = {
                        radius = 5f
                        key2Value++
                    }) {
                        Text(
                            text = "Changing state by giving key2 parameter(5f radius)",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Cloudy(
                    modifier = Modifier.padding(top = 20.dp),
                    radius = castFloatToInt(radius),
                    key1 = key1Value,
                    key2 = key2Value,
                ) {
                    GradientBlock(modifier = Modifier, text = "Blur 2")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(12))
                    .padding(10.dp)
            ) {

                Text(
                    textAlign = TextAlign.Center,
                    text = "Blur changing by allowAccumulate:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Button(modifier = Modifier.padding(top = 20.dp), onClick = { animationPlayed = true }) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Changing state by giving allowAccumulate parameter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Cloudy(
                    modifier = Modifier.padding(top = 20.dp),
                    radius = animatedRadius,
                    allowAccumulate = { true }
                ) {
                    GradientBlock(modifier = Modifier, text = "Blur 3")
                }
            }
        }
    }
}

@Composable
private fun GradientBlock(modifier: Modifier, text: String) {
    Box(
        modifier = modifier
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Red,
                        Color.Blue
                    )
                )
            )
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
@Preview
private fun BlurPreview() {
    BlurExample()
}

private fun castFloatToInt(value: Float): Int {
    return try {
        value.toInt()
    } catch (e: Exception) {
        0
    }
}