package pn.android.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import pn.android.R
import pn.android.core.extensions.showToast
import pn.android.features.Feature
import pn.android.features.destinations.CalendarScreenDestination
import pn.android.theme.*

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(destinationsNavigator: DestinationsNavigator) {
    val context = LocalContext.current
    val viewModel = getViewModel<MainViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collect {
            when (it) {
                is MainAction.ShowFeature -> when(it.feature) {
                    Feature.CALENDAR -> destinationsNavigator.navigate(CalendarScreenDestination)
                }
            }
        }
    }

    val state by viewModel.container.stateFlow.collectAsState()

    MainScreenContent(state, viewModel)
}

@Composable
fun MainScreenContent(state: MainViewState, viewModel: MainViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PnColors.Grey50)
            .systemBarsPadding()
    ) {

        Text(
            modifier = Modifier
                .padding(14.dp),
            text = stringResource(R.string.features),
            style = PnBoldTextStyles.Title1Black,
            textAlign = TextAlign.Start
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = state.features,
                key = { feature -> feature.id }
            ) { feature ->

                MainItem(
                    feature = feature,
                    onClick = viewModel::showFeature
                )

            }
        }

    }

}

@Composable
fun MainItem(feature: Feature, onClick: (Feature) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .fillMaxWidth()
            .height(52.dp)
            .clip(PnShapes.RoundedCornerShape16)
            .background(color = PnColors.White)
            .clickable { onClick(feature) }
    ) {

        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart),
            text = feature.title,
            style = PnMediumTextStyles.BodyBlack,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}