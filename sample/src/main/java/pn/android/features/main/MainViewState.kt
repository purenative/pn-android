package pn.android.features.main

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pn.android.features.Feature

@Parcelize
data class MainViewState(
    val features: List<Feature> = Feature.values().toList()
) : Parcelable