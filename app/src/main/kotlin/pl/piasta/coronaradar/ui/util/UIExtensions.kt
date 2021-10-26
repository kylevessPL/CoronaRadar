package pl.piasta.coronaradar.ui.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import pl.piasta.coronaradar.util.ifTrue

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, {
        it?.let(observer)
    })
}

fun <T> LiveData<T>.observeNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, {
        (it == null).ifTrue { observer(it) }
    })
}