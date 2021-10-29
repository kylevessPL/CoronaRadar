package pl.piasta.coronaradar.ui.user.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.passwordConfirmValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage
import pl.piasta.coronaradar.util.ifTrue

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
        (!_input.passwordConfirm.isNullOrBlank()).ifTrue { validatePasswordConfirm() }
        return passwordValidationMessage(_input.password, true).let { message ->
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