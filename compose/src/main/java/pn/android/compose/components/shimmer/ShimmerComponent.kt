package pn.android.compose.components.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.*

/**
 * Component of a real screen, where during the loading of data,
 * flickering blocks of the sizes corresponding to these components are displayed in place of the final UI.
 * [modifier] - parameter to change the Shimmer according to the needs of the developer
 * [shimmerInstance] - parameter that can set the style for the shimmer
 *  It has options like:
 *      (1) - shimmerBounds: ShimmerBounds - Due to the differences in sizes,
 *      all three shimmers have a different velocitiy,
 *      which doesn't look as calm as it should.
 *      It would be way cleaner if it was only a single animation traversing over the views we want to be shimmering.
 *      That's why the library offers different options for the effect's boundaries:
 *          (a) - ShimmerBounds.View:
 *              The default option, which was used to create the gifs above and is used in the theming samples in the app.
 *              Depending on the use case, this option might already be sufficient.
 *          (b) - ShimmerBounds.Window:
 *               One option is to use the boundaries of the current window.
 *               This will create a shimmer that travels over the whole window,
 *               while affecting only the views (and child views) which have the shimmer modifier attached.
 *               Be aware that this option might look odd on scrollable content,
 *               because the shimmer will be positioned relative to the window and will not be moved together with the content.
 *               Depending on the theme this effect might be more or less visible.
 *               If the shimmer moves from the left to the right for example,
 *               and the content can only be scrolled up and down, this effect won't be visible and can be ignored.
 *           (c) - ShimmerBounds.Custom:
 *               The downsides of the Window option is why the ShimmerBounds.
 *               Custom option exists.
 *               By using it the shimmer and its content will not be drawn until the bounds are set manually by using the updateBounds method on the Shimmer.
 *               This allows for attaching the shimmer to a scrollable list for example.
 *               Column(
 *                   modifier = Modifier
 *                   .onGloballyPositioned { layoutCoordinates ->
 *                   // Util function included in the library
 *                   val position = layoutCoordinates.unclippedBoundsInWindow()
 *                   shimmerCustomInstance.updateBounds(position)
 *                   }
 *                   .verticalScroll(rememberScrollState()),
 *              )
 *      (2) - theme: ShimmerTheme - can be provided as a local composition.
 *      So overwriting the shimmer's default theme for the whole application is possible as usual:
 *
 *      val yourShimmerTheme = defaultShimmerTheme.copy(...)
 *      CompositionLocalProvider(
 *      LocalShimmerTheme provides yourShimmerTheme
 *      ) {
 *      ...
 *      }
 * */
@Composable
fun ShimmerComponent(modifier: Modifier = Modifier, shimmerInstance: Shimmer, title: String) {
    Text(
        modifier = Modifier
            .padding(start = 20.dp, bottom = 10.dp, end = 20.dp),
        color = Color.Black,
        textAlign = TextAlign.Start,
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Box(
        modifier = modifier
            .size(128.dp)
            .background(color = Color.Blue)
            .shimmer(shimmerInstance)
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.Red)
                .size(50.dp)
                .align(Alignment.Center)
        )
    }
}


@Composable
fun ShimmerBehindBackgroundComponent(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .padding(start = 20.dp, bottom = 10.dp, end = 20.dp),
        color = Color.Black,
        textAlign = TextAlign.Start,
        text = "Shimmer modifier behind background modifier",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Box(
        modifier = Modifier
            .size(128.dp)
            .padding(20.dp)
            .shimmer()
            .background(color = Color.Blue)
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.Red)
                .size(50.dp)
                .align(Alignment.Center)
        )
    }
}



@Preview
@Composable
fun ShimmerPreview() {
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
    Column(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                // Util function included in the library
                val position = layoutCoordinates.unclippedBoundsInWindow()
                shimmerCustomInstance.updateBounds(position)
            }
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Preview",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        /**
         * shimmer modifier behind background
         * */
        ShimmerBehindBackgroundComponent()
        ShimmerComponent(
            modifier = Modifier.padding(20.dp),
            shimmerInstance = shimmerWindowInstance,
            title = "ShimmerBounds.Window"
        )
        ShimmerComponent(
            modifier = Modifier.padding(20.dp),
            shimmerInstance = shimmerViewInstance,
            title = "ShimmerBounds.View"
        )
        ShimmerComponent(
            modifier = Modifier.padding(20.dp),
            shimmerInstance = shimmerCustomInstance,
            title = "ShimmerBounds.Custom"
        )
        ShimmerComponent(
            modifier = Modifier.padding(20.dp),
            shimmerInstance = shimmerCustomThemeInstance,
            title = "ShimmerBounds.Window, Custom theme radius"
        )
    }
}