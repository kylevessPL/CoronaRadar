package pl.piasta.coronaradar.ui.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Patterns
import android.view.Gravity.TOP
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.Visibility.MODE_IN
import androidx.transition.Visibility.MODE_OUT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str

@BindingAdapter("android:errorText")
fun setErrorText(input: TextInputLayout, @StringRes errorText: Int?) {
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
            (actionId == EditorInfo.IME_ACTION_DONE).also {
                it.ifTrue {
                    view.clearFocus()
                    val imm =
                        view.context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }.takeIf { value }?.invoke()
}

//@BindingAdapter("android:onSurveyFinish")
//fun onSurveyFinish(view: SurveyView, block: (TaskResult, FinishReason) -> Unit) {
//    view.onSurveyFinish = { taskResult, reason -> block(taskResult, reason) }
//}

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
        true -> View.VISIBLE
        false -> View.GONE
    }
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
    val ref = when (Patterns.WEB_URL.matcher(uri).matches()) {
        true -> Uri.parse(uri.replace("s96-c", "s300-c")).buildUpon()
            .appendQueryParameter("width", "300")
            .appendQueryParameter("height", "300")
            .build().toString()
        false -> it.contentBytes(view.context)
    }
    progressIndicator.visibility = View.VISIBLE
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
                progressIndicator.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressIndicator.visibility = View.GONE
                return false
            }
        })
        .centerCrop()
        .into(view)
}
