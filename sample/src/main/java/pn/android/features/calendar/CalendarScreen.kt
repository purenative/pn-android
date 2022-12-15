package pn.android.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel
import pn.android.compose.components.calendar.data.CalendarSelector
import pn.android.compose.components.calendar.ui.year.CalendarSelectionMode
import pn.android.compose.components.calendar.ui.year.CalendarYear
import pn.android.core.extensions.showToast
import pn.android.theme.PnColors
import pn.android.theme.PnRegularTextStyles

@Destination
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val viewModel = getViewModel<CalendarViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collect {
            when (it) {
                is CalendarAction.DateSelected -> context.showToast(it.date.toString())
            }
        }
    }

    val state by viewModel.container.stateFlow.collectAsState()

    CalendarScreenContent(state, viewModel)
}

@Composable
fun CalendarScreenContent(state: CalendarViewState, viewModel: CalendarViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PnColors.Grey50)
            .systemBarsPadding()
    ) {

        CalendarYear(
            selectionMode = CalendarSelectionMode.Single(state.selectedDate),
            selector = CalendarSelector.AppSelector(),
            events = emptyList(),
            textStyle = PnRegularTextStyles.BodyBlack,
            onDayClick = { date, _ ->
                viewModel.onDateSelected(date)
            }
        )

    }



}