package pn.android.features.main

import androidx.lifecycle.SavedStateHandle
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import pn.android.base.BaseViewModel
import pn.android.features.Feature

class MainViewModel(handle: SavedStateHandle) :
    BaseViewModel<MainViewState, MainAction>() {

    override val container = container<MainViewState, MainAction>(
        MainViewState(),
        handle
    )

    fun showFeature(feature: Feature) {
        intent {
            postSideEffect(MainAction.ShowFeature(feature))
        }
    }

}