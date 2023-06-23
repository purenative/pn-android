package pn.android.image_gallery_example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.images.viewer.ImageGallery
import pn.android.core.R
import pn.android.rememberCoilImagePainter

@OptIn(ExperimentalPagerApi::class)
@Destination
@Composable
fun ImageGalleryScreen(navigator: DestinationsNavigator) {
    val monkeys = listOf(
        R.drawable.monkey_1,
        R.drawable.monkey_2,
        R.drawable.monkey_3,
        R.drawable.monkey_4,
        R.drawable.monkey_5
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        ImageGallery(count = 5, imageLoader = { index ->
            val monkey = monkeys[index]
            rememberCoilImagePainter(image = monkey)
        })
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .align(Alignment.TopStart)
                .size(30.dp)
                .clickable { navigator.popBackStack() },
            painter = painterResource(id = R.drawable.ic_arrow_left_24_black),
            contentDescription = null,
            tint = Color.White
        )
    }
}