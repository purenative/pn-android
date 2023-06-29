package pn.android.compose.components.paging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pn.android.compose.components.indicators.PrimaryIndicator

@Composable
fun <T> LazyColumnWithPaging(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    pagingState: PagingState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    errorTextTitle: String,
    errorTextStyle: TextStyle,
    items: List<T>,
    key: ((index: Int, item: T) -> Any)? = null,
    primaryIndicatorColor: Color = Color.White,
    onLoadNextData: () -> Unit,
    onError: () -> Unit = {},
    itemContent: @Composable (Int, T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        itemsIndexed(
            items = items,
            key = key
        ) { index, item ->
            if (items.size - index < pagingState.pageSize / 2 &&
                !pagingState.endReached &&
                !pagingState.isLoading &&
                !pagingState.isError
            ) {
                onLoadNextData()
            }
            itemContent(index, item)
        }

        if (pagingState.isLoading) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PrimaryIndicator(color = primaryIndicatorColor)
                }
            }
        }

        if (pagingState.isError) {
            item {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 30.dp)
                        .fillMaxWidth()
                        .clickable { onError() },
                    text = errorTextTitle,
                    textAlign = TextAlign.Center,
                    style = errorTextStyle
                )
            }
        }
    }
}