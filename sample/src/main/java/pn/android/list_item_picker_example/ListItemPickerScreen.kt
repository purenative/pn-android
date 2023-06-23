package pn.android.list_item_picker_example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.pickers.ListItemPicker
import pn.android.core.R

@Destination
@Composable
fun ListItemPickerScreen(navigator: DestinationsNavigator) {

    val possibleValues = listOf(
        "10 декабря",
        "11 декабря",
        "12 декабря",
        "13 декабря",
        "14 декабря",
    )

    var state by remember { mutableStateOf(possibleValues[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .size(30.dp)
                .clickable { navigator.popBackStack() },
            painter = painterResource(id = R.drawable.ic_arrow_left_24_black),
            contentDescription = null,
            tint = Color.White
        )
        ListItemPicker(
            modifier = Modifier.fillMaxSize(),
            label = { it },
            value = state,
            onValueChange = { state = it },
            dividersColor = Color.Transparent,
            textStyle = TextStyle.Default.copy(color = Color.White, fontSize = 16.sp),
            list = possibleValues
        )
    }
}