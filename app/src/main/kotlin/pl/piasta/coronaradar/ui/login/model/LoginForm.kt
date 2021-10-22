package pl.piasta.coronaradar.ui.login.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.ifTrue
import pl.piasta.coronaradar.util.isMaxExclusive

class LoginForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.loginFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    fun validateEmail() = isEmailValid()

    fun validatePassword() = isPasswordValid()

    @Bindable
    fun isLoginFormValid(): Boolean = isEmailValid(false) && isPasswordValid(false)

    private fun isEmailValid(showError: Boolean = true): Boolean {
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
                else -> {
                    _error.email.set(null)
                    true
                }
            }
        }
    }

    private fun isPasswordValid(showError: Boolean = false): Boolean {
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