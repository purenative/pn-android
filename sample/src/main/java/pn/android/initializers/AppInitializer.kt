package pn.android.initializers

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}