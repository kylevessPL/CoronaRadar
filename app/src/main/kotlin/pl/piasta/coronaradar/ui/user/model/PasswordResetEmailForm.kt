package pl.piasta.coronaradar.ui.user.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.ifTrue
import pl.piasta.coronaradar.util.isMaxExclusive

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

    class InputFields(val onChange: () -> Unit) {

        var email: String? = null
            set(value) {
                field = value
                onChange()
            }
    }

    class ErrorFields {

        val email: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}