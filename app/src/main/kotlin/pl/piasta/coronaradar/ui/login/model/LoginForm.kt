package pl.piasta.coronaradar.ui.login.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.emailValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage

class LoginForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.loginFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isLoginFormValid(): Boolean = validateEmail(false) && validatePassword(false)

    fun validateEmail(showError: Boolean = true): Boolean {
        return emailValidationMessage(_input.email).let { message ->
            message.takeUnless { it != null && !showError }?.let { _error.email.set(it) }
            message != null
        }
    }

    fun validatePassword(showError: Boolean = true): Boolean {
        return passwordValidationMessage(_input.password).let { message ->
            message.takeUnless { it != null && !showError }?.let { _error.password.set(it) }
            message != null
        }
    }

    class InputFields(val onChange: () -> Unit) {

        var email: String? = null
            set(value) {
                field = value
                onChange()
            }

        var password: String? = null
            set(value) {
                field = value
                onChange()
            }
    }

    class ErrorFields {

        val email: ObservableField<Int> by lazy { ObservableField<Int>() }
        val password: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}