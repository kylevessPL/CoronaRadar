package pl.piasta.coronaradar.ui.util

import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str
import splitties.views.material.string

@BindingAdapter("android:errorText")
fun setErrorText(input: TextInputLayout, @StringRes errorText: Int) {
    input.error = input.context.str(errorText)
}

@BindingAdapter("android:onFocusChange")
fun onFocusChange(input: TextInputLayout, block: () -> Unit) {
    input.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        (!hasFocus && (view as TextInputLayout).string.isNotBlank()).ifTrue {
            block()
        }
    }
}