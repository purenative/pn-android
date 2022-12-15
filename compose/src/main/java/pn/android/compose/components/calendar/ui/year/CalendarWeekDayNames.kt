package pn.android.compose.components.calendar.ui.year

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CalendarWeekDayNames(
    modifier: Modifier = Modifier,
    week: List<LocalDate>,
    headerStyle: TextStyle
) {
    Row(
        modifier = modifier
            .background(color = Color.White)
            .fillMaxWidth()
    ) {
        week.forEach { day ->

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = day.dayOfWeek.toString().take(2).uppercase(),
                    style = headerStyle
                )

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                )

            }

        }
    }
}
