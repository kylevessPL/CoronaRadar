package pl.piasta.coronaradar.util

inline val Any.TAG: String
    get() = this::class.java.simpleName

inline fun String?.ifNullOrEmpty(defaultValue: () -> String): String =
    if (isNullOrEmpty()) defaultValue() else this

inline fun Boolean?.ifTrue(block: Boolean.() -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}

fun String.isMaxExclusive(maxLength: Int) = this.length < maxLength