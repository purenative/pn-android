package pn.android.core.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun Float.floorTo(decimal: Int = 2, roundMode: RoundingMode = RoundingMode.FLOOR): Float {
    return BigDecimal(this.toDouble()).setScale(decimal, roundMode).toFloat()
}