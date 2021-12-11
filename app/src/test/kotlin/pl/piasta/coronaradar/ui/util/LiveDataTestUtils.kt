package pl.piasta.coronaradar.ui.util

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = PRIVATE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T? {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }
    return data
}

@VisibleForTesting(otherwise = PRIVATE)
fun <T> LiveData<T>.observeForTesting(observer: Observer<T>, block: () -> Unit) {
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}

@VisibleForTesting(otherwise = PRIVATE)
@Suppress("UNCHECKED_CAST")
fun Map<LiveData<*>, Observer<*>>.observeMultipleForTesting(block: () -> Unit) {
    try {
        forEach { (liveData, observer) -> liveData.observeForever(observer as Observer<in Any>) }
        block()
    } finally {
        forEach { (liveData, observer) -> liveData.removeObserver(observer as Observer<in Any>) }
    }
}