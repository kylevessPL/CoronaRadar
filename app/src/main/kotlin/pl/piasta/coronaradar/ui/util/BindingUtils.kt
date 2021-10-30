package pl.piasta.coronaradar.ui.util

import android.view.Gravity.TOP
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.Visibility.MODE_IN
import androidx.transition.Visibility.MODE_OUT
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