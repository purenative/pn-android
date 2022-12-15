package pn.android.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pn.android.features.calendar.CalendarViewModel
import pn.android.features.main.MainViewModel

fun viewModelModule() = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        CalendarViewModel(get())
    }

}
