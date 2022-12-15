package pn.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ramcosta.composedestinations.DestinationsNavHost
import pn.android.features.NavGraphs

class PnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}