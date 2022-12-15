package pn.android.compose.components.calendar.ui.year

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import pn.android.compose.components.calendar.data.CalendarEvent
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.utils.getNext7Dates
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun InfiniteLoadingList(
    modifier: Modifier,
    items: List<Any>,
    loadMore: (() -> Unit)? = null,
    rowContent: @Composable (Int, Any) -> Unit,
) {
    val listState = rememberLazyListState()
    val firstVisibleIndex = remember { mutableStateOf(listState.firstVisibleItemIndex) }
    LazyColumn(state = listState, modifier = modifier) {
        itemsIndexed(items) { index, item ->
            rowContent(index, item)
        }
    }

    loadMore?.let {
        if (listState.isAtLastPosition(firstVisibleIndex)) {
            it()
        }
    }


}

internal fun LazyListState.isAtLastPosition(rememberedIndex: MutableState<Int>): Boolean {
    val firstVisibleIndex = this.firstVisibleItemIndex
    if (rememberedIndex.value != firstVisibleIndex) {
        rememberedIndex.value = firstVisibleIndex
        return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
    }
    return false
}


@Composable
fun CalendarYear(
    selectionMode: CalendarSelectionMode,
    startMonth: YearMonth = YearMonth.now(),
    countMonth: Int = 5,
    textStyle: TextStyle,
    selector: CalendarSelector,
    events: List<CalendarEvent>,
    onDayClick: (LocalDate, CalendarEvent?) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {

        CalendarWeekDayNames(
            week = LocalDate.now().with(DayOfWeek.MONDAY).getNext7Dates(),
            headerStyle = textStyle
        )

        InfiniteLoadingList(
            modifier = Modifier.fillMaxWidth(),
            items = (0..countMonth).toList()
        ) { _, item ->

            CalendarMonth(
                selectionMode = selectionMode,
                month = startMonth.plusMonths((item as Int).toLong()),
                events = events,
                headerTextStyle = textStyle,
                selector = selector,
                onDayClick = onDayClick
            )
        }

    }


}


