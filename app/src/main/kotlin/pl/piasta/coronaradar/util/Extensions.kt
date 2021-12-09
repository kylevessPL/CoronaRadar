package pl.piasta.coronaradar.util

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.StepResult
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import pl.piasta.coronaradar.data.base.Labellable
import splitties.resources.appStr
import kotlin.math.roundToInt

inline val Any.TAG: String
    get() = this::class.java.simpleName

inline fun Boolean?.ifTrue(block: Boolean.() -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}

fun Boolean.toInt() = compareTo(false)

fun Int.divideToPercent(divideTo: Int): Int {
    (divideTo == 0).ifTrue { throw IllegalArgumentException("Division by zero is permitted!") }
    return (this / divideTo.toFloat()).times(100).toInt()
}

fun Float.percent() = times(100).roundToInt()

inline fun String?.ifNullOrEmpty(defaultValue: () -> String) =
    this.takeIf { !isNullOrEmpty() } ?: defaultValue()

fun String.isMaxExclusive(maxLength: Int) = this.length < maxLength

inline val String.Companion.EMPTY get() = ""

inline fun <reified T> findByLabel(label: String): T? where T : Enum<T>, T : Labellable =
    enumValues<T>().find { appStr(it.label) == label }

inline fun <reified T> List<StepResult>.findLastResult(id: Int): T? where T : QuestionResult =
    find { it.id.id == id.toString() }?.results?.lastOrNull() as? T

fun CollectionReference.getQuerySnapshotFlow() = callbackFlow {
    Log.d(TAG, "Registering the listener on collection at path - $path")
    val callback = addSnapshotListener { querySnapshot, ex ->
        ex?.let {
            cancel(
                message = "Error fetching collection data at path - $path",
                cause = it
            )
            return@addSnapshotListener
        }
        trySend(querySnapshot)
    }
    awaitClose {
        Log.d(TAG, "Cancelling the listener on collection at path - $path")
        callback.remove()
    }
}

inline fun <T> CollectionReference.getDataFlow(crossinline mapper: (QuerySnapshot?) -> T) =
    getQuerySnapshotFlow().map {
        return@map mapper(it)
    }