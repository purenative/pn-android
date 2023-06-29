package pn.android.primary_indicator_example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.indicators.PrimaryIndicator

@Destination
@Composable
fun PrimaryIndicatorScreen(navigator: DestinationsNavigator) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .align(Alignment.TopStart)
                .size(30.dp)
                .clickable { navigator.popBackStack() },
            painter = painterResource(id = pn.android.core.R.drawable.ic_arrow_left_24_black),
            contentDescription = null,
            tint = Color.White
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryIndicator(modifier = Modifier.size(50.dp), color = Color.White)
        }
    }
}