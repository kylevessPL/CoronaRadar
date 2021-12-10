package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.user.model.PasswordResetForm
import javax.inject.Inject

class PasswordResetViewModel @Inject constructor(@IoDispatcher private val coroutineDispatcher: CoroutineDispatcher) :
    ViewModel() {

    private val _passwordResetForm = PasswordResetForm()
    val passwordResetForm: PasswordResetForm
        get() = _passwordResetForm

    fun validatePassword() {
        viewModelScope.launch(coroutineDispatcher) {
            _passwordResetForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch(coroutineDispatcher) {
            _passwordResetForm.validatePasswordConfirm()
        }
    }
}