package pl.piasta.coronaradar.ui.register.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.emailValidationMessage
import pl.piasta.coronaradar.ui.util.passwordConfirmValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage

class RegisterForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.registerFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isRegisterFormValid(): Boolean =
        validateEmail(false) && validatePassword(false) && validatePasswordConfirm(false)

    fun validateEmail(showError: Boolean = true): Boolean {
        return emailValidationMessage(_input.email).let { message ->
            { _error.email.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePassword(showError: Boolean = true): Boolean {
        validatePasswordConfirm()
        return passwordValidationMessage(_input.password).let { message ->
            { _error.password.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePasswordConfirm(showError: Boolean = true): Boolean {
        return passwordConfirmValidationMessage(
            _input.password,
            _input.passwordConfirm
        ).let { message ->
            { _error.passwordConfirm.set(message) }.takeUnless { message != null && !showError }
                ?.invoke()
            message == null
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

        var passwordConfirm: String? = null
            set(value) {
                field = value
                onChange()
            }
    }

    class ErrorFields {

        val email: ObservableField<Int> by lazy { ObservableField<Int>() }
        val password: ObservableField<Int> by lazy { ObservableField<Int>() }
        val passwordConfirm: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}