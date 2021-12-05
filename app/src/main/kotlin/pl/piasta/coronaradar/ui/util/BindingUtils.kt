package pl.piasta.coronaradar.ui.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.SystemClock
import android.util.Patterns.WEB_URL
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity.TOP
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.Visibility.MODE_IN
import androidx.transition.Visibility.MODE_OUT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.Chart.PAINT_INFO
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.appStr
import splitties.resources.str
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.MEDIUM
import java.time.format.FormatStyle.SHORT

@BindingAdapter("android:staticValue")
fun staticValue(view: Slider, active: Boolean) {
    {
        with(view) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {

                @Suppress("CLICKABLEVIEWACCESSIBILITY")
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_DOWN,
                            trackWidth / (valueTo - valueFrom) * (value - valueFrom),
                            0F,
                            0
                        )
                    )
                    haloRadius = 0
                    setOnTouchListener { _, _ -> true }
                }
            })
        }
    }.takeIf { active }?.invoke()
}

@BindingAdapter("android:errorText")
fun errorText(input: TextInputLayout, @StringRes errorText: Int?) {
    input.error = errorText?.let { input.context.str(it) }
}

@BindingAdapter("android:onEndIconClick")
fun onEndIconClick(input: TextInputLayout, block: () -> Unit) {
    input.setEndIconOnClickListener { block() }
}

@BindingAdapter("android:onFocusChange")
fun onFocusChange(input: TextInputEditText, block: () -> Unit) {
    input.onFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus -> block.takeUnless { hasFocus }?.invoke() }
}

@BindingAdapter("android:loseFocusOnDone")
fun loseFocusOnDone(input: TextInputEditText, value: Boolean) {
    {
        input.setOnEditorActionListener { view, actionId, _ ->
            (actionId == IME_ACTION_DONE).also {
                it.ifTrue {
                    view.clearFocus()
                    val imm =
                        view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }.takeIf { value }?.invoke()
}

@BindingAdapter("android:animatedVisibility")
fun animatedVisibility(view: CardView, isVisible: Boolean) {
    val transition = Slide()
    transition.mode = when (isVisible) {
        true -> MODE_IN
        false -> MODE_OUT
    }
    transition.slideEdge = TOP
    transition.duration = 1200
    transition.addTarget(view)
    TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
    view.visibility = when (isVisible) {
        true -> VISIBLE
        false -> GONE
    }
}

@BindingAdapter("android:noDataText")
fun noDataText(view: Chart<*>, @StringRes textRes: Int) = view.setNoDataText(appStr(textRes))

@BindingAdapter("android:noDataTextColor")
fun noDataTextColor(view: Chart<*>, @ColorInt color: Int) = view.setNoDataTextColor(color)

@BindingAdapter("android:noDataTextSize")
fun noDataTextSize(view: Chart<*>, textSize: Int) {
    val sizePx = TypedValue.applyDimension(
        COMPLEX_UNIT_SP,
        textSize.toFloat(),
        view.context.resources.displayMetrics
    )
    val paint = view.getPaint(PAINT_INFO).apply {
        setTextSize(sizePx)
    }
    view.setPaint(paint, PAINT_INFO)
}

@BindingAdapter("android:itemDecoration")
fun itemDecoration(view: RecyclerView, value: Boolean) {
    {
        view.addItemDecoration(DividerItemDecoration(view.context, VERTICAL))
    }.takeIf { value }?.invoke()
}

@BindingAdapter("android:progressCompat")
fun progressCompat(view: BaseProgressIndicator<*>, value: Int) = view.setProgressCompat(value, true)

@BindingAdapter("android:imageUri", "android:placeholderDrawable", "android:progressIndicator")
fun imageUri(
    view: ImageView,
    imageUri: Uri?,
    placeholderDrawable: Drawable,
    progressIndicator: CircularProgressIndicator
) = imageUri?.let {
    val uri = it.toString()
    val ref = when (WEB_URL.matcher(uri).matches()) {
        true -> Uri.parse(uri.replace("s96-c", "s300-c")).buildUpon()
            .appendQueryParameter("width", "300")
            .appendQueryParameter("height", "300")
            .build().toString()
        false -> it.contentBytes(view.context)
    }
    progressIndicator.visibility = VISIBLE
    Glide.with(view.context)
        .load(ref)
        .placeholder(placeholderDrawable)
        .fallback(placeholderDrawable)
        .error(placeholderDrawable)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressIndicator.visibility = GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressIndicator.visibility = GONE
                return false
            }
        })
        .centerCrop()
        .into(view)
}

@BindingConversion
fun convertBooleanToVisibility(visible: Boolean) = when (visible) {
    true -> VISIBLE
    false -> GONE
}

@BindingConversion
fun convertLocalDateToText(date: LocalDate): String =
    date.format(DateTimeFormatter.ofLocalizedDate(MEDIUM))

@BindingConversion
fun convertLocalTimeToText(time: LocalTime): String =
    time.format(DateTimeFormatter.ofLocalizedTime(SHORT))
