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
import pn.android.core.base.PNViewsEnum
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
fun ListPNViews(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        PNViewsEnum.values().forEach { view ->
            PNViewsNameBlock(view = view) {
                navigator.navigate(getScreenDestination(view))
            }
            Spacer(modifier = Modifier.height(20f.dp))
        }
    }
}

@Composable
fun PNViewsNameBlock(modifier: Modifier = Modifier, view: PNViewsEnum, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(12f.dp))
            .padding(horizontal = 12f.dp, vertical = 15f.dp)
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
            .padding(start = 12f.dp, end = 12f.dp, top = 20f.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Pure Native Views",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20f.dp))
            ListPNViews(navigator = navigator)
        }
    }
}

fun getScreenDestination(view: PNViewsEnum): DirectionDestination {
    return when(view) {
        PNViewsEnum.GifImageView -> GifImageScreenDestination
        PNViewsEnum.CalendarView -> CalendarScreenDestination
        PNViewsEnum.IndicatorView -> PrimaryIndicatorScreenDestination
        PNViewsEnum.ListItemPickerView -> ListItemPickerScreenDestination
        PNViewsEnum.ImageGalleryView -> ImageGalleryScreenDestination
    }
}