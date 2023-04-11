package pn.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import pn.android.compose.components.images.viewer.ImageGallery
import pn.android.compose.components.images.viewer.rememberViewerState
import pn.android.core.extensions.showToast
import pn.android.ui.theme.PnandroidTheme

@OptIn(ExperimentalPagerApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showToast("Main Entry Point")

        setContent {
            PnandroidTheme {

                val images = remember {
                    mutableStateListOf(
                        "https://upload.wikimedia.org/wikipedia/commons/1/16/Saul_Goodman.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/1/16/Saul_Goodman.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/1/16/Saul_Goodman.jpg",
                    )
                }

                ImageGallery(
                    modifier = Modifier.fillMaxSize(),
                    count = images.size,
                    imageLoader = { index ->
                        val image = images[index]
                        rememberCoilImagePainter(image = image)
                    }
                )

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