package pl.piasta.coronaradar.ui.util

import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class EditTextPasswordHiddenMatcher(private val hidden: Boolean) :
    TypeSafeMatcher<View>() {

    override fun matchesSafely(item: View?) = item.takeIf { it is EditText }?.let {
        (it as EditText).transformationMethod is PasswordTransformationMethod == hidden
    } ?: false

    override fun describeTo(desc: Description) {
        desc.appendText("with password hidden: ")
            .appendValue(hidden)
    }
}

class TextInputLayoutHelperTextEnabledMatcher(private val enabled: Boolean) :
    TypeSafeMatcher<View>() {

    override fun matchesSafely(item: View?) = item.takeIf { it is TextInputLayout }?.let {
        (it as TextInputLayout).isHelperTextEnabled == enabled
    } ?: false

    override fun describeTo(desc: Description) {
        desc.appendText("with helperText state: ")
            .appendValue(enabled)
    }
}

class TextInputLayoutExpandedHintEnabledMatcher(private val enabled: Boolean) :
    TypeSafeMatcher<View>() {

    override fun matchesSafely(item: View?) = item.takeIf { it is TextInputLayout }?.let {
        (it as TextInputLayout).isExpandedHintEnabled == enabled
    } ?: false

    override fun describeTo(desc: Description) {
        desc.appendText("with expandedHint state: ")
            .appendValue(enabled)
    }
}

class TextInputLayoutCounterEnabledMatcher(private val enabled: Boolean) : TypeSafeMatcher<View>() {

    override fun matchesSafely(item: View?) = item.takeIf { it is TextInputLayout }?.let {
        (it as TextInputLayout).isCounterEnabled == enabled
    } ?: false

    override fun describeTo(desc: Description) {
        desc.appendText("with counter state: ")
            .appendValue(enabled)
    }
}