package pn.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.core.base.PNComponentsEnum
import pn.android.core.extensions.showToast
import pn.android.destinations.*
import pn.android.ui.theme.PnandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showToast("Main Entry Point")

        setContent {
            PnandroidTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@Composable
fun rememberCoilImagePainter(image: Any): Painter {

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(image)
        .size(coil.size.Size.ORIGINAL)
        .build()

    return rememberAsyncImagePainter(imageRequest)
}

@Composable
fun ListPNComponents(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        PNComponentsEnum.values().forEach { view ->
            PNComponentsNameBlock(view = view) {
                navigator.navigate(getScreenDestination(view))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PNComponentsNameBlock(modifier: Modifier = Modifier, view: PNComponentsEnum, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 15.dp)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = view.viewName,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            painter = painterResource(id = pn.android.core.R.drawable.ic_arrow_right_24_black),
            contentDescription = null
        )
    }
}
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(navigator: DestinationsNavigator) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(start = 12.dp, end = 12.dp, top = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Pure Native Components",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            ListPNComponents(navigator = navigator)
        }
    }
}

fun getScreenDestination(view: PNComponentsEnum): DirectionDestination {
    return when(view) {
        PNComponentsEnum.GifImageComponent -> GifImageScreenDestination
        PNComponentsEnum.CalendarComponent -> CalendarScreenDestination
        PNComponentsEnum.IndicatorComponent -> PrimaryIndicatorScreenDestination
        PNComponentsEnum.ListItemPickerComponent -> ListItemPickerScreenDestination
        PNComponentsEnum.ImageGalleryComponent -> ImageGalleryScreenDestination
        PNComponentsEnum.MaskVisualTransformationComponent -> MaskVisualTransformationScreenDestination
        PNComponentsEnum.LazyColumnWithPagingComponent -> LazyColumnWithPagingScreenDestination
    }
}