package pn.android.initializers

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pn.android.BuildConfig
import pn.android.di.viewModelModule

class KoinInitializer : AppInitializer {
    override fun init(application: Application) {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(application)
            modules(
                listOf(
                    viewModelModule(),
                )
            )
        }
    }
}