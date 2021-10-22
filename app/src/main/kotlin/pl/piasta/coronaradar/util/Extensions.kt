package pl.piasta.coronaradar.util

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

inline fun Boolean?.ifTrue(block: Boolean.() -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}

fun CharSequence.isMaxExclusive(maxLength: Int): Boolean {
    return this.length < maxLength
}