package pn.android.charts_example

import android.graphics.Color.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.topAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pn.android.compose.components.calendar.clickable
import pn.android.compose.components.chart.CustomMarkerVisibilityChangeListener
import pn.android.compose.components.chart.rememberLegend
import pn.android.compose.components.chart.rememberMarker
import pn.android.compose.components.chart.rememberThresholdLine
import pn.android.core.R

@Destination
@Composable
fun ChartsScreen(navigator: DestinationsNavigator) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        val columnChart = columnChart()
        val lineChart = lineChart()

        val chartEntryModelProducer1 = ChartEntryModelProducer(entriesOf(4f, 12f, 8f, 16f))
        val chartEntryModelProducer2 = ChartEntryModelProducer(entriesOf(16f, 8f, 12f, 4f))
        val composedChartEntryModelProducer = chartEntryModelProducer1 + chartEntryModelProducer2

        val chartEntryModel = entryModelOf(4f, 12f, 8f, 16f)

        val isShown = remember {
            mutableStateOf(false)
        }

        Icon(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .size(30.dp)
                .clickable { navigator.popBackStack() },
            painter = painterResource(id = R.drawable.ic_arrow_left_24_black),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Text(
            text = stringResource(id = R.string.simple_line_chart),
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Chart(
            modifier = Modifier.padding(horizontal = 20.dp),
            chart = lineChart,
            model = chartEntryModel,
            startAxis = startAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
            bottomAxis = bottomAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
        )
        Text(
            text = stringResource(id = R.string.simple_column_chart),
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Chart(
            modifier = Modifier.padding(horizontal = 20.dp),
            chart = columnChart,
            model = chartEntryModel,
            startAxis = startAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
            bottomAxis = bottomAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
        )
        Text(
            text = stringResource(id = R.string.simple_composed_chart),
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Chart(
            modifier = Modifier.padding(horizontal = 20.dp),
            chart = remember(lineChart, columnChart) { lineChart + columnChart },
            chartModelProducer = composedChartEntryModelProducer,
            startAxis = startAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
            bottomAxis = bottomAxis(
                label = textComponent {
                    color = BLACK
                    textSizeSp = 16f
                }
            ),
        )
        Text(
            text = stringResource(id = R.string.custom_line_chart),
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        if (isShown.value) {
            Text(
                text = stringResource(id = R.string.marker_shown),
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        val thresholdLine = rememberThresholdLine()
        Chart(
            modifier = Modifier.padding(horizontal = 20.dp),
            chart = lineChart(decorations = remember(thresholdLine) { listOf(thresholdLine) }),
            model = chartEntryModel,
            startAxis = startAxis(label = textComponent {
                color = BLUE
                textSizeSp = 16f
            }),
            bottomAxis = bottomAxis(
                label = textComponent {
                    color = RED
                    textSizeSp = 16f
                },
            ),
            endAxis = endAxis(label = textComponent {
                color = GREEN
                textSizeSp = 16f
            }),
            topAxis = topAxis(label = textComponent {
                color = WHITE
                textSizeSp = 16f
            }),
            marker = rememberMarker(),
            legend = rememberLegend(
                legendTextFirst = stringResource(id = R.string.start_of_production),
                legendTextSecond = stringResource(id = R.string.end_of_production)
            ),
            isZoomEnabled = false,
            markerVisibilityChangeListener = CustomMarkerVisibilityChangeListener(isShown),
            horizontalLayout = HorizontalLayout.fullWidth()
        )
    }
}