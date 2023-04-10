package pn.android.core.base

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}