package pl.piasta.coronaradar.ui.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.view.MotionEvent
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
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
import splitties.resources.strArray
import java.nio.ByteBuffer
import java.util.*

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) =
    this.observe(owner, {
        it?.let(observer)
    })

fun <T> LiveData<T>.observeNull(owner: LifecycleOwner, observer: (t: T) -> Unit) =
    this.observe(owner, {
        (it == null).ifTrue { observer(it) }
    })

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

fun <T : Drawable> T.pixelsEqualTo(t: T?) = toBitmap().pixelsEqualTo(t?.toBitmap())

fun Bitmap.pixelsEqualTo(otherBitmap: Bitmap?) =
    otherBitmap?.takeIf { width == it.width && height == it.height }?.let {
        toPixels().compareTo(it.toPixels()) == 0
    } ?: false

fun Bitmap.toPixels(): ByteBuffer =
    ByteBuffer.allocate(byteCount).apply { copyPixelsToBuffer(this) }

fun Context.strLocalized(@StringRes stringResId: Int, locale: Locale): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration).resources.getString(stringResId)
}

val Context.supportedLanguagesCodes
    get() = strArray(R.array.language_entries)
        .zip(strArray(R.array.language_values))

val Context.recordingPath get() = filesDir.resolve("rec.wav")

fun Uri.contentBytes(ctx: Context) =
    ctx.contentResolver.openInputStream(this)?.use { it.readBytes() }

fun Uri.fileSize(ctx: Context) =
    ctx.contentResolver.query(this, null, null, null, null)!!.use {
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        it.moveToFirst()
        val ifa = it.getLong(sizeIndex)
        ifa
    }

fun stepOf(id: Int) = StepIdentifier(id.toString())
