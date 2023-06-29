package pn.android.compose.components.paging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PagingState(
    val pageSize: Int,
    var isLoading: Boolean,
    val page: Int,
    val endReached: Boolean,
    val isError: Boolean
) : Parcelable {
    companion object {
        val EMPTY = PagingState(
            pageSize = 100,
            isLoading = false,
            page = 0,
            endReached = false,
            isError = false
        )
    }
}