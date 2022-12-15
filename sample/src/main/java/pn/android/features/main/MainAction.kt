package pn.android.features.main

import pn.android.features.Feature

sealed class MainAction {
    data class ShowFeature(val feature: Feature): MainAction()
}