package pn.android.features.calendar

import androidx.lifecycle.SavedStateHandle
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import pn.android.base.BaseViewModel
import java.time.LocalDate

class CalendarViewModel(handle: SavedStateHandle) :
    BaseViewModel<CalendarViewState, CalendarAction>() {

    override val container = container<CalendarViewState, CalendarAction>(
        CalendarViewState(),
        handle
    )

    fun onDateSelected(date: LocalDate) {
        intent {
            reduce { state.copy(selectedDate = date) }
            postSideEffect(CalendarAction.DateSelected(date))
        }
    }

}