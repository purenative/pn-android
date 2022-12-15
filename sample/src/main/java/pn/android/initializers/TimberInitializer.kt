package pn.android.initializers

import android.app.Application
import pn.android.BuildConfig
import timber.log.Timber

class TimberInitializer() : AppInitializer {
    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}