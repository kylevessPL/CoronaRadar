package pl.piasta.coronaradar.ui.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.assertion.ViewAssertions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import io.github.kakaocup.kakao.common.utilities.getResourceString
import io.github.kakaocup.kakao.edit.TextInputLayoutAssertions
import io.github.kakaocup.kakao.image.ImageViewAssertions
import io.github.kakaocup.kakao.text.TextViewAssertions
import pl.piasta.coronaradar.util.ifTrue

fun ImageViewAssertions.hasDrawable(drawableUri: Uri) {
    view.check { view, notFoundException ->
        view.takeIf { it is ImageView }?.let {
            val imageView = it as ImageView
            val orgDrawable = DrawableCompat.wrap(imageView.drawable).mutate()
            Glide.with(view.context)
                .load(drawableUri)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        ex: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        notFoundException?.let { throw AssertionError(ex) }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val cmpDrawable = DrawableCompat.wrap(resource!!).mutate()
                        (!orgDrawable.toBitmap().sameAs(cmpDrawable.toBitmap())).ifTrue {
                            throw AssertionError(
                                "Expected drawable is $cmpDrawable," +
                                        " but actual is $orgDrawable"
                            )
                        }
                        return false
                    }
                })
                .into(imageView)
        } ?: notFoundException?.let { throw AssertionError(it) }
    }
}

fun TextViewAssertions.hasInputType(inputType: Int) {
    view.check { view, notFoundException ->
        view.takeIf { it is EditText }?.let {
            (inputType != (it as EditText).inputType).ifTrue {
                throw AssertionError(
                    "Expected inputType is $inputType," +
                            " but actual is ${it.inputType}"
                )
            }
        } ?: notFoundException?.let { throw AssertionError(it) }
    }
}

fun TextViewAssertions.isPasswordHidden() {
    view.check(ViewAssertions.matches(EditTextPasswordHiddenMatcher(true)))
}

fun TextViewAssertions.isPasswordShown() {
    view.check(ViewAssertions.matches(EditTextPasswordHiddenMatcher(false)))
}

fun TextInputLayoutAssertions.hasEndIconMode(endIconMode: EndIconMode) {
    view.check { view, notFoundException ->
        view.takeIf { it is TextInputLayout }?.let {
            (endIconMode.value != (it as TextInputLayout).endIconMode).ifTrue {
                throw AssertionError(
                    "Expected endIconMode is ${endIconMode.name}," +
                            " but actual is ${EndIconMode.getByValue(it.endIconMode).name}"
                )
            }
        } ?: notFoundException?.let { throw AssertionError(it) }
    }
}

fun TextInputLayoutAssertions.hasPlaceholderText(placeholderText: String) {
    view.check { view, notFoundException ->
        view.takeIf { it is TextInputLayout }?.let {
            (placeholderText != (it as TextInputLayout).placeholderText).ifTrue {
                throw AssertionError(
                    "Expected placeholderText is $placeholderText," +
                            " but actual is ${it.placeholderText}"
                )
            }
        } ?: notFoundException?.let { throw AssertionError(it) }
    }
}

fun TextInputLayoutAssertions.hasHelperText(helperText: String) {
    view.check { view, notFoundException ->
        view.takeIf { it is TextInputLayout }?.let {
            (helperText != (it as TextInputLayout).helperText).ifTrue {
                throw AssertionError(
                    "Expected helperText is $helperText," +
                            " but actual is ${it.helperText}"
                )
            }
        } ?: notFoundException?.let { throw AssertionError(it) }
    }
}

fun TextInputLayoutAssertions.hasPlaceholderText(@StringRes resId: Int) {
    hasPlaceholderText(getResourceString(resId))
}

fun TextInputLayoutAssertions.hasHelperText(@StringRes resId: Int) {
    hasHelperText(getResourceString(resId))
}

fun TextInputLayoutAssertions.isHelperTextEnabled() {
    view.check(ViewAssertions.matches(TextInputLayoutHelperTextEnabledMatcher(true)))
}

fun TextInputLayoutAssertions.isHelperTextDisabled() {
    view.check(ViewAssertions.matches(TextInputLayoutHelperTextEnabledMatcher(false)))
}

fun TextInputLayoutAssertions.isExpandedHintEnabled() {
    view.check(ViewAssertions.matches(TextInputLayoutExpandedHintEnabledMatcher(true)))
}

fun TextInputLayoutAssertions.isExpandedHintDisabled() {
    view.check(ViewAssertions.matches(TextInputLayoutExpandedHintEnabledMatcher(false)))
}

enum class EndIconMode(val value: Int) {
    /**
     * The view will display a custom icon specified
     * by the user.
     */
    CUSTOM(-1),

    /**
     * No end icon.
     */
    NONE(0),

    /**
     * The view will display a toggle when the EditText
     * has a password.
     */
    PASSWORD_TOGGLE(1),

    /**
     * The view will display a clear text button while the EditText
     * contains input.
     */
    CLEAR_TEXT(2),

    /**
     * The view will display a toggle that displays/hides
     * a dropdown menu.
     */
    DROPDOWN_MENU(3);

    companion object {
        @JvmStatic
        fun getByValue(value: Int) = when (value) {
            CUSTOM.value -> CUSTOM
            NONE.value -> NONE
            PASSWORD_TOGGLE.value -> PASSWORD_TOGGLE
            CLEAR_TEXT.value -> CLEAR_TEXT
            DROPDOWN_MENU.value -> DROPDOWN_MENU
            else -> throw IllegalArgumentException("No EndIconMode matching value $value!")
        }
    }
}