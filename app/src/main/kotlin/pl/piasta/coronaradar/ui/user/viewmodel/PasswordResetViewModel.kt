package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.FirebaseAuthRepository
import pl.piasta.coronaradar.ui.user.model.PasswordResetForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _passwordResetForm = PasswordResetForm()
    val passwordResetForm: PasswordResetForm
        get() = _passwordResetForm

    private val _passwordResetResult = LiveEvent<ResultState<Boolean>>()
    val passwordResetResult: LiveEvent<ResultState<Boolean>>
        get() = _passwordResetResult

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

    fun resetPassword(oob: String, newPassword: String) {
        viewModelScope.launch {
            repository.resetPassword(oob, newPassword).collect { result ->
                _passwordResetResult.postValue(result)
            }
        }
    }
}