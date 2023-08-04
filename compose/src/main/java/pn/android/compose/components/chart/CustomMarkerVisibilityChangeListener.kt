package pn.android.compose.components.chart

import androidx.compose.runtime.MutableState
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener

class CustomMarkerVisibilityChangeListener(var isShown: MutableState<Boolean>) : MarkerVisibilityChangeListener {
    override fun onMarkerHidden(marker: Marker) {
        super.onMarkerHidden(marker)
        isShown.value = false
    }

    override fun onMarkerShown(
        marker: Marker,
        markerEntryModels: List<Marker.EntryModel>
    ) {
        super.onMarkerShown(marker, markerEntryModels)
        isShown.value = true
    }

    override fun onMarkerMoved(
        marker: Marker,
        markerEntryModels: List<Marker.EntryModel>
    ) {
        super.onMarkerMoved(marker, markerEntryModels)
    }
}