package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.user.model.PasswordResetEmailForm
import javax.inject.Inject

@HiltViewModel
class PasswordResetEmailViewModel @Inject constructor(@IoDispatcher private val coroutineDispatcher: CoroutineDispatcher) :
    ViewModel() {

    private val _passwordResetEmailForm = PasswordResetEmailForm()
    val passwordResetEmailForm: PasswordResetEmailForm
        get() = _passwordResetEmailForm

    fun validateEmail() {
        viewModelScope.launch(coroutineDispatcher) {
            _passwordResetEmailForm.validateEmail()
        }
    }
}