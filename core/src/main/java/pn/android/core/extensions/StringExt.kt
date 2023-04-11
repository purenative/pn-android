package pn.android.core.extensions

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun String.md5(): ByteArray = MessageDigest
    .getInstance("MD5")
    .digest(toByteArray(StandardCharsets.UTF_8))

fun String.toBase64(): String {
    return String(
        android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT),
        StandardCharsets.UTF_8
    )
}

fun String.fromBase64(): String {
    return String(
        android.util.Base64.decode(this, android.util.Base64.DEFAULT),
        StandardCharsets.UTF_8
    )
}