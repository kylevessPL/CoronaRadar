package pl.piasta.coronaradar.ui.account.model

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.ui.util.passwordConfirmValidationMessage
import pl.piasta.coronaradar.ui.util.passwordValidationMessage
import pl.piasta.coronaradar.util.ifTrue

class UserDetailsForm(userDetails: UserDetails? = null) : BaseObservable() {

    var isProcessing: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.userDetailsFormValid)
        }

    private val _input = InputFields { notifyPropertyChanged(BR.userDetailsFormValid) }
    val input: InputFields
        get() = _input

    private val _error = ErrorFields()
    val error: ErrorFields
        get() = _error

    init {
        userDetails?.apply { init(this) }
    }

    @Bindable
    fun isUserDetailsFormValid(): Boolean =
        !isProcessing && formFilled() && validateDisplayName() && validatePassword(false) && validatePasswordConfirm(
            false
        )

    fun clear() {
        _input.initialDisplayName = _input.displayName.get()
        _input.avatarChosen = false
        _input.password.set(null)
        _input.passwordConfirm.set(null)
        notifyPropertyChanged(BR.userDetailsFormValid)
    }

    fun displayNameFilled() = _input.displayName.get() != _input.initialDisplayName

    fun passwordFilled() =
        !(_input.password.get().isNullOrBlank() && _input.passwordConfirm.get().isNullOrBlank())

    fun avatarChosen() = _input.avatarChosen

    fun validatePassword(showError: Boolean = true): Boolean {
        (!_input.passwordConfirm.get().isNullOrBlank()).ifTrue { validatePasswordConfirm() }
        return (!passwordFilled()).takeIf { it }?.also {
            clearPasswordError()
        } ?: passwordValidationMessage(_input.password.get(), true).let { message ->
            { _error.password.set(message) }.takeUnless { message != null && !showError }?.invoke()
            message == null
        }
    }

    fun validatePasswordConfirm(showError: Boolean = true): Boolean {
        return (!passwordFilled()).takeIf { it }?.also {
            clearPasswordError()
        } ?: passwordConfirmValidationMessage(
            _input.password.get(),
            _input.passwordConfirm.get()
        ).let { message ->
            { _error.passwordConfirm.set(message) }.takeUnless { message != null && !showError }
                ?.invoke()
            message == null
        }
    }

    private fun init(userDetails: UserDetails) {
        _input.displayName.set(userDetails.displayName)
        _input.initialDisplayName = userDetails.displayName
        _input.email.set(userDetails.email)
        _input.avatar.set(userDetails.avatarUri)
        _input.avatarChosen = false
    }

    private fun validateDisplayName(): Boolean {
        return (!displayNameFilled()).takeIf { it } ?: with(_input.displayName.get()) {
            !this.isNullOrEmpty() && this != _input.initialDisplayName
        }
    }

    private fun formFilled() = avatarChosen() || displayNameFilled() || passwordFilled()

    private fun clearPasswordError() {
        _error.password.set(null)
        _error.passwordConfirm.set(null)
    }

    inner class InputFields(val onChange: () -> Unit) {

        var avatarChosen: Boolean = false

        var initialDisplayName: String? = null

        var avatar = object : ObservableField<Uri?>() {

            override fun set(value: Uri?) {
                super.set(value)
                avatarChosen = true
                onChange()
            }
        }

        var displayName = object : ObservableField<String?>() {

            override fun set(value: String?) {
                super.set(value)
                onChange()
            }
        }

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

        val password = ObservableField<Int>()
        val passwordConfirm = ObservableField<Int>()
    }
}