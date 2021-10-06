@file:JvmName("ExtensionsKt")

package pl.piasta.coronaradar.util

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName