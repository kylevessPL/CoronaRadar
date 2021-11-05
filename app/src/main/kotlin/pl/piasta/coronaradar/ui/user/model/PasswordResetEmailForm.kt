package pl.piasta.coronaradar.ui.user.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.emailValidationMessage

class PasswordResetEmailForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.passwordResetEmailFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isPasswordResetEmailFormValid(): Boolean = validateEmail(false)

    fun validateEmail(showError: Boolean = true): Boolean {
        return emailValidationMessage(_input.email.get()).let { message ->
            { _error.email.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    class InputFields(val onChange: () -> Unit) {

        var email = object : ObservableField<String?>() {

            override fun set(value: String?) {
                super.set(value)
                onChange()
            }
        }
    }

    class ErrorFields {

        val email: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}