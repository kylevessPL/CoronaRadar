package pl.piasta.coronaradar.ui.register.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.emailValidationMessage
import pl.piasta.coronaradar.ui.util.passwordConfirmValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage
import pl.piasta.coronaradar.util.ifTrue

class RegisterForm : BaseObservable() {

    var isProcessing: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.registerFormValid)
        }

    private val _input = InputFields { notifyPropertyChanged(BR.registerFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isRegisterFormValid() =
        !isProcessing && validateEmail(false) && validatePassword(false) && validatePasswordConfirm(
            false
        )

    fun validateEmail(showError: Boolean = true): Boolean {
        return emailValidationMessage(_input.email.get()).let { message ->
            { _error.email.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePassword(showError: Boolean = true): Boolean {
        (!_input.passwordConfirm.get().isNullOrBlank()).ifTrue { validatePasswordConfirm() }
        return passwordValidationMessage(_input.password.get(), true).let { message ->
            { _error.password.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePasswordConfirm(showError: Boolean = true): Boolean {
        return passwordConfirmValidationMessage(
            _input.password.get(),
            _input.passwordConfirm.get()
        ).let { message ->
            { _error.passwordConfirm.set(message) }.takeUnless { message != null && !showError }
                ?.invoke()
            message == null
        }
    }

    inner class InputFields(val onChange: () -> Unit) {

        var email = object : ObservableField<String?>() {

            override fun set(value: String?) {
                super.set(value)
                onChange()
            }
        }

        var password = object : ObservableField<String?>() {

            override fun set(value: String?) {
                super.set(value)
                onChange()
            }
        }

        var passwordConfirm = object : ObservableField<String?>() {

            override fun set(value: String?) {
                super.set(value)
                onChange()
            }
        }
    }

    inner class ErrorFields {

        val email: ObservableField<Int> by lazy { ObservableField<Int>() }
        val password: ObservableField<Int> by lazy { ObservableField<Int>() }
        val passwordConfirm: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}