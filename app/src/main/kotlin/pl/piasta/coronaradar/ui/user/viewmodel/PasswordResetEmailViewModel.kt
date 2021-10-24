package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.FirebaseAuthRepository
import pl.piasta.coronaradar.ui.user.model.PasswordResetEmailForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class PasswordResetEmailViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _passwordResetEmailForm = PasswordResetEmailForm()
    val passwordResetEmailForm: PasswordResetEmailForm
        get() = _passwordResetEmailForm

    private val _passwordResetEmailResult = LiveEvent<ResultState<Boolean>>()
    val passwordResetEmailResult: LiveEvent<ResultState<Boolean>>
        get() = _passwordResetEmailResult

    fun validateEmail() {
        viewModelScope.launch {
            _passwordResetEmailForm.validateEmail()
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            repository.sendPasswordResetEmail(email).collect { result ->
                _passwordResetEmailResult.postValue(result)
            }
        }
    }
}