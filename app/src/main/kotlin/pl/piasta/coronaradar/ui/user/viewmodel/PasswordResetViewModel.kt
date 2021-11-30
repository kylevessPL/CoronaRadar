package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.ui.user.model.PasswordResetForm

class PasswordResetViewModel : ViewModel() {

    private val _passwordResetForm = PasswordResetForm()
    val passwordResetForm: PasswordResetForm
        get() = _passwordResetForm

    fun validatePassword() {
        viewModelScope.launch {
            _passwordResetForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch {
            _passwordResetForm.validatePasswordConfirm()
        }
    }
}