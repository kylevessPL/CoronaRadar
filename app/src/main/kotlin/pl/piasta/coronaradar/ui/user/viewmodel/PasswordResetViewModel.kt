package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.FirebaseAuthRepository
import pl.piasta.coronaradar.ui.user.model.PasswordResetForm
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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