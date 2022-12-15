package pn.android.base

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any> :
    ContainerHost<STATE, SIDE_EFFECT>, ViewModel()