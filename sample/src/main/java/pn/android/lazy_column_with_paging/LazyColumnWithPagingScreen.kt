package pn.android.lazy_column_with_paging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.paging.LazyColumnWithPaging
import pn.android.compose.components.paging.PagingState
import pn.android.core.R

val messagesList = arrayListOf<String>()
@Destination
@Composable
fun LazyColumnWithPagingScreen(navigator: DestinationsNavigator) {
    val pagingState = remember {
        mutableStateOf(PagingState.EMPTY)
    }

    val itemsList = remember {
        mutableListOf<String>()
    }

    LaunchedEffect(key1 = pagingState) {
        if (messagesList.isEmpty()) {
            for (index in 0..10) {
                messagesList.add("index: $index")
            }
        }

        loadNextData(pagingState) {
            itemsList.addAll(messagesList)
        }
    }

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
        LazyColumnWithPaging(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 20.dp),
            pagingState = pagingState.value,
            errorTextTitle = "",
            errorTextStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Red
            ),
            items = itemsList,
            onLoadNextData = {
                loadNextData(pagingState) {
                    itemsList.addAll(messagesList)
                }
            }) { _, item ->
            ItemBlock(item)
        }
    }
}

@Composable
fun ItemBlock(text: String) {
    Text(
        text = text, style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.White
        )
    )
    Spacer(modifier = Modifier.height(10.dp))
}

fun loadNextData(state: MutableState<PagingState>, onLoadEnd: () -> Unit) {
    GlobalScope.launch {
        state.value = state.value.copy(isLoading = true)
        delay(2000L)
        state.value = state.value.copy(isLoading = false)
        onLoadEnd()
    }
}