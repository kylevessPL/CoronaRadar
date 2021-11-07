package pl.piasta.coronaradar.ui.util

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.provider.OpenableColumns
import android.view.MotionEvent
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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

fun Window.dispatchActionDownTouchEvent(event: MotionEvent) {
    (event.action == MotionEvent.ACTION_DOWN).ifTrue {
        currentFocus?.takeIf { it is EditText }?.let {
            val outRect = Rect()
            it.getGlobalVisibleRect(outRect)
            (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())).ifTrue {
                it.clearFocus()
                val imm =
                    context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }
}

fun Context.recordingPath() = filesDir.resolve("rec.wav")

fun Uri.fileBytes(ctx: Context) = ctx.contentResolver.openInputStream(this)?.use { it.readBytes() }

fun Uri.fileSize(ctx: Context) =
    ctx.contentResolver.query(this, null, null, null, null)!!.use {
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        it.moveToFirst()
        it.getLong(sizeIndex)
    }
