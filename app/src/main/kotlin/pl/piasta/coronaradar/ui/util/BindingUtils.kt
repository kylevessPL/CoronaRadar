package pl.piasta.coronaradar.ui.util

import android.graphics.Typeface
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.charts.Chart.PAINT_INFO
import com.github.mikephil.charting.components.Legend.*
import com.github.mikephil.charting.formatter.ValueFormatter
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
fun staticValue(view: Slider, active: Boolean) = {
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

@BindingAdapter("android:onPageChanged")
fun onPageChanged(view: ViewPager2, block: (Int) -> Unit) =
    view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            block(position)
        }
    })

@BindingAdapter("android:errorText")
fun errorText(input: TextInputLayout, @StringRes errorText: Int?) {
    input.error = errorText?.let { input.context.str(it) }
}

@BindingAdapter("android:onEndIconClick")
fun onEndIconClick(input: TextInputLayout, block: () -> Unit) =
    input.setEndIconOnClickListener { block() }

@BindingAdapter("android:onFocusChange")
fun onFocusChange(input: TextInputEditText, block: () -> Unit) {
    input.onFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus -> block.takeUnless { hasFocus }?.invoke() }
}

@BindingAdapter("android:loseFocusOnDone")
fun loseFocusOnDone(input: TextInputEditText, value: Boolean) = {
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

@BindingAdapter("android:usePercentValues")
fun usePercentValues(chart: PieChart, value: Boolean) = chart.setUsePercentValues(value)

@BindingAdapter("android:extraTopOffset")
fun extraTopOffset(chart: Chart<*>, value: Float) {
    chart.extraTopOffset = value
}

@BindingAdapter("android:extraBottomOffset")
fun extraBottomOffset(chart: Chart<*>, value: Float) {
    chart.extraBottomOffset = value
}

@BindingAdapter("android:extraLeftOffset")
fun extraLeftOffset(chart: Chart<*>, value: Float) {
    chart.extraLeftOffset = value
}

@BindingAdapter("android:extraRightOffset")
fun extraRightOffset(chart: Chart<*>, value: Float) {
    chart.extraRightOffset = value
}

@BindingAdapter("android:descriptionEnabled")
fun descriptionEnabled(chart: Chart<*>, value: Boolean) {
    chart.description.isEnabled = value
}

@BindingAdapter("android:centerText")
fun centerText(chart: PieChart, @StringRes textRes: Int) {
    chart.centerText = appStr(textRes)
}

@BindingAdapter("android:centerTextSize")
fun centerTextSize(chart: PieChart, value: Float) = chart.setCenterTextSize(value)

@BindingAdapter("android:centerTextTypeface")
fun centerTextTypeface(chart: PieChart, value: Typeface) = chart.setCenterTextTypeface(value)

@BindingAdapter("android:webColor")
fun webColor(chart: RadarChart, @ColorInt value: Int) {
    chart.webColor = value
}

@BindingAdapter("android:webAlpha")
fun webAlpha(chart: RadarChart, value: Int) {
    chart.webAlpha = value
}

@BindingAdapter("android:webInnerColor")
fun webInnerColor(chart: RadarChart, @ColorInt value: Int) {
    chart.webColorInner = value
}

@BindingAdapter("android:webInnerLineWidth")
fun webInnerLineWidth(chart: RadarChart, value: Float) {
    chart.webLineWidthInner = value
}

@BindingAdapter("android:rotationEnabled")
fun rotationEnabled(chart: PieChart, value: Boolean) {
    chart.isRotationEnabled = value
}

@BindingAdapter("android:legendEnabled")
fun legendEnabled(chart: Chart<*>, value: Boolean) {
    chart.legend.isEnabled = value
}

@BindingAdapter("android:legendOrientation")
fun legendOrientation(chart: Chart<*>, value: LegendOrientation) {
    chart.legend.orientation = value
}

@BindingAdapter("android:legendVerticalAlignment")
fun legendVerticalAlignment(chart: Chart<*>, value: LegendVerticalAlignment) {
    chart.legend.verticalAlignment = value
}

@BindingAdapter("android:legendHorizontalAlignment")
fun legendHorizontalAlignment(chart: Chart<*>, value: LegendHorizontalAlignment) {
    chart.legend.horizontalAlignment = value
}

@BindingAdapter("android:legendForm")
fun legendForm(chart: Chart<*>, value: LegendForm) {
    chart.legend.form = value
}

@BindingAdapter("android:legendTextSize")
fun legendTextSize(chart: Chart<*>, value: Float) {
    chart.legend.textSize = value
}

@BindingAdapter("android:legendTextColor")
fun legendTextColor(chart: Chart<*>, @ColorInt value: Int) {
    chart.legend.textColor = value
}

@BindingAdapter("android:legendTextTypeface")
fun legendTextTypeface(chart: Chart<*>, value: Typeface) {
    chart.legend.typeface = value
}

@BindingAdapter("android:legendYOffset")
fun legendYOffset(chart: Chart<*>, value: Float) {
    chart.legend.yOffset = value
}

@BindingAdapter("android:entryLabelTextSize")
fun entryLabelTextSize(chart: PieChart, value: Float) = chart.setEntryLabelTextSize(value)

@BindingAdapter("android:drawGridBackground")
fun drawGridBackground(chart: BarChart, value: Boolean) = chart.setDrawGridBackground(value)

@BindingAdapter("android:drawBarShadow")
fun drawBarShadow(chart: BarChart, value: Boolean) = chart.setDrawBarShadow(value)

@BindingAdapter("android:doubleTapToZoomEnabled")
fun doubleTapToZoomEnabled(chart: BarChart, value: Boolean) {
    chart.isDoubleTapToZoomEnabled = value
}

@BindingAdapter("android:highlightPerTapEnabled")
fun highlightPerTapEnabled(chart: Chart<*>, value: Boolean) {
    chart.isHighlightPerTapEnabled = value
}

@BindingAdapter("android:highlightFullBarEnabled")
fun highlightFullBarEnabled(chart: BarChart, value: Boolean) {
    chart.isHighlightFullBarEnabled = value
}

@BindingAdapter("android:highlightPerDragEnabled")
fun highlightPerDragEnabled(chart: BarChart, value: Boolean) {
    chart.isHighlightPerDragEnabled = value
}

@BindingAdapter("android:leftAxisMin")
fun leftAxisMin(chart: BarChart, value: Float) {
    chart.axisLeft.axisMinimum = value
}

@BindingAdapter("android:leftAxisMin")
fun leftAxisMin(chart: LineChart, value: Float) {
    chart.axisLeft.axisMinimum = value
}

@BindingAdapter("android:leftAxisGranularity")
fun leftAxisGranularity(chart: BarChart, value: Float) {
    chart.axisLeft.granularity = value
}

@BindingAdapter("android:leftAxisGranularity")
fun leftAxisGranularity(chart: LineChart, value: Float) {
    chart.axisLeft.granularity = value
}

@BindingAdapter("android:leftAxisTextSize")
fun leftAxisTextSize(chart: BarChart, value: Float) {
    chart.axisLeft.textSize = value
}

@BindingAdapter("android:leftAxisTextSize")
fun leftAxisTextSize(chart: LineChart, value: Float) {
    chart.axisLeft.textSize = value
}

@BindingAdapter("android:leftAxisTextColor")
fun leftAxisTextColor(chart: BarChart, @ColorInt value: Int) {
    chart.axisLeft.textColor = value
}

@BindingAdapter("android:leftAxisTextColor")
fun leftAxisTextColor(chart: LineChart, @ColorInt value: Int) {
    chart.axisLeft.textColor = value
}

@BindingAdapter("android:leftAxisLineColor")
fun leftAxisLineColor(chart: LineChart, @ColorInt value: Int) {
    chart.axisLeft.axisLineColor = value
}

@BindingAdapter("android:leftAxisXOffset")
fun leftAxisXOffset(chart: BarChart, value: Float) {
    chart.axisLeft.xOffset = value
}

@BindingAdapter("android:leftAxisXOffset")
fun leftAxisXOffset(chart: LineChart, value: Float) {
    chart.axisLeft.xOffset = value
}

@BindingAdapter("android:leftAxisFormatAsInt")
fun leftAxisFormatAsInt(chart: LineChart, value: Boolean) = {
    chart.axisLeft.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float) = value.toInt().toString()
    }
}.takeIf { value }?.invoke()

@BindingAdapter("android:leftAxisLabelCount")
fun leftAxisLabelCount(chart: LineChart, value: Int) = chart.axisLeft.setLabelCount(value, false)

@BindingAdapter("android:rightAxisEnabled")
fun rightAxisEnabled(chart: BarChart, value: Boolean) {
    chart.axisRight.isEnabled = value
}

@BindingAdapter("android:rightAxisEnabled")
fun rightAxisEnabled(chart: LineChart, value: Boolean) {
    chart.axisRight.isEnabled = value
}

@BindingAdapter("android:touchEnabled")
fun touchEnabled(chart: Chart<*>, value: Boolean) = chart.setTouchEnabled(value)

@BindingAdapter("android:dragEnabled")
fun dragEnabled(chart: LineChart, value: Boolean) {
    chart.isDragEnabled = value
}

@BindingAdapter("android:xAxisEnabled")
fun xAxisEnabled(chart: BarChart, value: Boolean) {
    chart.xAxis.isEnabled = value
}

@BindingAdapter("android:xAxisFormatAsString")
fun xAxisFormatAsString(chart: Chart<*>, values: List<Int>) {
    chart.xAxis.valueFormatter = object : ValueFormatter() {
        val labels = values.map { appStr(it) }
        override fun getFormattedValue(value: Float) = labels[value.toInt() % labels.size]
    }
}

@BindingAdapter("android:xAxisDrawAxisLine")
fun xAxisDrawAxisLine(chart: Chart<*>, value: Boolean) = chart.xAxis.setDrawAxisLine(value)

@BindingAdapter("android:xAxisDrawGridLines")
fun xAxisDrawGridLines(chart: Chart<*>, value: Boolean) = chart.xAxis.setDrawGridLines(value)

@BindingAdapter("android:xAxisGranularity")
fun xAxisGranularity(chart: Chart<*>, value: Float) {
    chart.xAxis.granularity = value
}

@BindingAdapter("android:xAxisTextSize")
fun xAxisTextSize(chart: Chart<*>, value: Float) {
    chart.xAxis.textSize = value
}

@BindingAdapter("android:xAxisTextColor")
fun xAxisTextColor(chart: Chart<*>, @ColorInt value: Int) {
    chart.xAxis.textColor = value
}

@BindingAdapter("android:xAxisYOffset")
fun xAxisYOffset(chart: Chart<*>, value: Float) {
    chart.xAxis.yOffset = value
}

@BindingAdapter("android:xAxisAvoidClipping")
fun xAxisAvoidClipping(chart: Chart<*>, value: Boolean) =
    chart.xAxis.setAvoidFirstLastClipping(value)

@BindingAdapter("android:yAxisDrawLabels")
fun yAxisDrawLabels(chart: RadarChart, value: Boolean) = chart.yAxis.setDrawLabels(value)

@BindingAdapter("android:yAxisLabelCount")
fun yAxisLabelCount(chart: RadarChart, value: Int) = chart.yAxis.setLabelCount(value, false)

@BindingAdapter("android:yAxisMin")
fun yAxisMin(chart: RadarChart, value: Float) {
    chart.yAxis.axisMinimum = value
}

@BindingAdapter("android:itemDecoration")
fun itemDecoration(view: RecyclerView, value: Boolean) = {
    view.addItemDecoration(DividerItemDecoration(view.context, VERTICAL))
}.takeIf { value }?.invoke()

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
                ex: GlideException?,
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
