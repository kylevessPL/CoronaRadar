package pl.piasta.coronaradar.ui.register.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.EMAIL_REGEX
import pl.piasta.coronaradar.util.ifTrue
import pl.piasta.coronaradar.util.isMaxExclusive

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
        with(_input.email) {
            return when {
                this.isNullOrBlank() -> {
                    showError.ifTrue { _error.email.set(R.string.empty_not_allowed) }
                    false
                }
                this.isMaxExclusive(6) -> {
                    showError.ifTrue { _error.email.set(R.string.min_six_chars_allowed) }
                    false
                }
                !this.matches(EMAIL_REGEX.toRegex()) -> {
                    showError.ifTrue { _error.email.set(R.string.email_not_valid) }
                    false
                }
                else -> {
                    _error.email.set(null)
                    true
                }
            }
        }
    }

    fun validatePassword(showError: Boolean = true): Boolean {
        validatePasswordConfirm()
        with(_input.password) {
            return when {
                this.isNullOrBlank() -> {
                    showError.ifTrue { _error.password.set(R.string.empty_not_allowed) }
                    false
                }
                this.isMaxExclusive(6) -> {
                    showError.ifTrue { _error.password.set(R.string.min_six_chars_allowed) }
                    false
                }
                else -> {
                    _error.password.set(null)
                    true
                }
            }
        }
    }

    fun validatePasswordConfirm(showError: Boolean = true): Boolean {
        with(_input.passwordConfirm) {
            return when {
                this.isNullOrBlank() -> {
                    showError.ifTrue { _error.passwordConfirm.set(R.string.empty_not_allowed) }
                    false
                }
                this != _input.password && !_input.password.isNullOrBlank() -> {
                    showError.ifTrue { _error.passwordConfirm.set(R.string.passwords_not_match) }
                    false
                }
                else -> {
                    _error.passwordConfirm.set(null)
                    true
                }
            }
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