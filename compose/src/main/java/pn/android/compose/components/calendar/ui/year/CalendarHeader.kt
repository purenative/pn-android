package pn.android.compose.components.calendar.ui.year

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pn.android.compose.components.calendar.data.CalendarSelector

@Composable
fun CalendarHeader(
    month: String,
    year: Int,
    selector: CalendarSelector,
    monthTextStyle: TextStyle,
    yearTextStyle: TextStyle
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
    ) {

        Text(
            text = month,
            textAlign = TextAlign.End,
            style = monthTextStyle
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = year.toString(),
            textAlign = TextAlign.Start,
            style = yearTextStyle
        )

    }
}

