package pl.piasta.coronaradar.ui.account.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.passwordConfirmValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage
import pl.piasta.coronaradar.util.ifTrue

class UserDetailsForm : BaseObservable() {

    private val _input = InputFields { notifyPropertyChanged(BR.userDetailsFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    @Bindable
    fun isUserDetailsFormValid(): Boolean =
        formFilled() && validatePassword(false) && validatePasswordConfirm(false)

    private fun formFilled() = displayNameFilled() || passwordFilled()

    private fun displayNameFilled() = !input.displayName.isNullOrBlank()

    private fun passwordFilled() =
        !(input.password.isNullOrBlank() && _input.passwordConfirm.isNullOrBlank())

    fun validatePassword(showError: Boolean = true): Boolean {
        (!_input.passwordConfirm.isNullOrBlank()).ifTrue { validatePasswordConfirm() }
        return (!passwordFilled()).takeIf { it }?.also {
            clearPasswordInput()
        } ?: passwordValidationMessage(_input.password, true).let { message ->
            { _error.password.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePasswordConfirm(showError: Boolean = true): Boolean {
        return (!passwordFilled()).takeIf { it }?.also {
            clearPasswordInput()
        } ?: passwordConfirmValidationMessage(
            _input.password,
            _input.passwordConfirm
        ).let { message ->
            { _error.passwordConfirm.set(message) }.takeUnless { message != null && !showError }
                ?.invoke()
            message == null
        }
    }

    private fun clearPasswordInput() {
        _error.password.set(null)
        _error.passwordConfirm.set(null)
    }

    class InputFields(val onChange: () -> Unit) {

        var displayName: String? = null
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

        val password: ObservableField<Int> by lazy { ObservableField<Int>() }
        val passwordConfirm: ObservableField<Int> by lazy { ObservableField<Int>() }
    }
}