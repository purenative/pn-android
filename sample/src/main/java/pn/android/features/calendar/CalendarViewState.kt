package pn.android.features.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class CalendarViewState(
    val selectedDate: LocalDate? = LocalDate.now()
) : Parcelable