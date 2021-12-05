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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.quickbirdstudios.surveykit.StepIdentifier
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.color

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

fun SwipeRefreshLayout.setGoogleSchemeColors() = setColorSchemeColors(
    color(R.color.google_blue),
    color(R.color.google_red),
    color(R.color.google_yellow),
    color(R.color.google_green)
)

inline fun <reified T : Fragment> newFragmentInstance(vararg params: Pair<String, Any>): T =
    T::class.java.newInstance().apply {
        arguments = bundleOf(*params)
    }

fun BottomSheetDialogFragment.expandDialog() {
    val dialog = dialog as BottomSheetDialog
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

val Context.recordingPath get() = filesDir.resolve("rec.wav")

fun Uri.contentBytes(ctx: Context) =
    ctx.contentResolver.openInputStream(this)?.use { it.readBytes() }

fun Uri.fileSize(ctx: Context) =
    ctx.contentResolver.query(this, null, null, null, null)!!.use {
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        it.moveToFirst()
        it.getLong(sizeIndex)
    }

fun stepOf(id: Int) = StepIdentifier(id.toString())
