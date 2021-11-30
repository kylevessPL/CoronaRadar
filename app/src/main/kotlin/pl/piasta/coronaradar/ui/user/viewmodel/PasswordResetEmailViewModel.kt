package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.ui.user.model.PasswordResetEmailForm

class PasswordResetEmailViewModel : ViewModel() {

    private val _passwordResetEmailForm = PasswordResetEmailForm()
    val passwordResetEmailForm: PasswordResetEmailForm
        get() = _passwordResetEmailForm

    fun validateEmail() {
        viewModelScope.launch {
            _passwordResetEmailForm.validateEmail()
        }
    }
}