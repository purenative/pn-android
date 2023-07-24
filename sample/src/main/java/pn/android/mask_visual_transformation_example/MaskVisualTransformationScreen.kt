package pn.android.mask_visual_transformation_example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.masks.MaskVisualTransformation
import pn.android.core.R

@Destination
@Composable
fun MaskVisualTransformationScreen(navigator: DestinationsNavigator) {
    val mask = remember { "+7 (###) ###-##-##" }
    val maskLength = remember(mask) { mask.count { it == '#' } }

    var text by remember { mutableStateOf("") }

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
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 100.dp)
                .background(color = Color.DarkGray),
            value = text,
            onValueChange = { it ->
                if (it.length <= maskLength) {
                    text = it.filter { it.isDigit() }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = MaskVisualTransformation(mask),
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.White
            )
        )
    }
}