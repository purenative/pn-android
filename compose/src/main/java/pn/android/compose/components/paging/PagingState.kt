package pn.android.compose.components.paging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * the data class needed to control the pagination process
 * [pageSize] - how many parts of the list of information are in the request
 * [isLoading] - checking for an loading condition
 * [page] - what part of the list of information is currently being added from the request
 * [endReached] - checking if the request returns an empty list
 * [isError] - checking for an error condition
 * */
@Parcelize
data class PagingState(
    val pageSize: Int,
    val isLoading: Boolean,
    val page: Int,
    val endReached: Boolean,
    val isError: Boolean
) : Parcelable {
    companion object {
        val EMPTY = PagingState(
            pageSize = 10,
            isLoading = false,
            page = 0,
            endReached = false,
            isError = false
        )
    }
}