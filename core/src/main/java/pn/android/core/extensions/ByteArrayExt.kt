package pn.android.core.extensions

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }