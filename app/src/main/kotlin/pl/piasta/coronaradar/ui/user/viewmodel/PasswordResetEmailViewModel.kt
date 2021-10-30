package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.ui.user.model.PasswordResetEmailForm
import javax.inject.Inject

@HiltViewModel
class PasswordResetEmailViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val _passwordResetEmailForm = PasswordResetEmailForm()
    val passwordResetEmailForm: PasswordResetEmailForm
        get() = _passwordResetEmailForm

    fun validateEmail() {
        viewModelScope.launch {
            _passwordResetEmailForm.validateEmail()
        }
    }
}