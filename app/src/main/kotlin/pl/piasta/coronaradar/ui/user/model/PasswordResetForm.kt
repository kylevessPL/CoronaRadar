package pl.piasta.coronaradar.ui.user.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.ifTrue
import pl.piasta.coronaradar.util.isMaxExclusive

class PasswordResetForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.passwordResetFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isPasswordResetFormValid(): Boolean =
        validatePassword(false) && validatePasswordConfirm(false)

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

        val password: ObservableField<Int> by lazy { ObservableField<Int>() }
        val passwordConfirm: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}