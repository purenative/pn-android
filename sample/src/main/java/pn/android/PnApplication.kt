package pn.android

import android.app.Application
import pn.android.initializers.KoinInitializer
import pn.android.initializers.TimberInitializer

class PnApplication : Application() {

    private val diInitializer by lazy { KoinInitializer() }
    private val timberInitializer by lazy { TimberInitializer() }

    override fun onCreate() {
        super.onCreate()

        diInitializer.init(this)
        timberInitializer.init(this)
    }

}