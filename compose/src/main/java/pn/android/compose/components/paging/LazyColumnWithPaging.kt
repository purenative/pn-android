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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pn.android.compose.components.indicators.PrimaryIndicator

/**
 * Component to display vertically scrolling list that only composes and lays out the currently visible items
 * with a request for only a separate limited piece of information from the network.
 * [modifier] - parameter to change the LazyColumn according to the needs of the developer
 * [state] - the state object to be used to control or observe the list's state.
 * [pagingState] - needed to control the pagination process
 * [contentPadding] - a padding around the whole content. This will add padding for the. content after it has been clipped, which is not possible via modifier param. You can use it to add a padding before the first item or after the last one. If you want to add a spacing between each item use verticalArrangement.
 * [reverseLayout] - reverse the direction of scrolling and layout. When true, items are laid out in the reverse order and LazyListState.firstVisibleItemIndex == 0 means that column is scrolled to the bottom. Note that reverseLayout does not change the behavior of verticalArrangement, e.g. with Arrangement.Top (top) 123### (bottom) becomes (top) 321### (bottom).
 * [verticalArrangement] - The vertical arrangement of the layout's children. This allows to add a spacing between items and specify the arrangement of the items when we have not enough of them to fill the whole minimum size.
 * [horizontalAlignment] - the horizontal alignment applied to the items.
 * [flingBehavior] - logic describing fling behavior.
 * [userScrollEnabled] - whether the scrolling via the user gestures or accessibility actions is allowed. You can still scroll programmatically using the state even when it is disabled
 * [errorTextTitle] - logic show error text if request return error
 * [errorTextStyle] - style of error text
 * [items] - the data list
 * [key] - a factory of stable and unique keys representing the item. Using the same key for multiple items in the list is not allowed.
 *      Type of the key should be saveable via Bundle on Android. If null is passed the position in the list will represent the key.
 *      When you specify the key the scroll position will be maintained based on the key, which means if you add/remove items before the current visible item the item with the given key will be kept as the first visible one.
 * [onLoadNextData] - logic for requesting the following information
 * [onError] - logic when the request returned an error
 * [itemContent] - views for the data list
 * */
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
                    PrimaryIndicator()
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