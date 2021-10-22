package pl.piasta.coronaradar.ui.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.FirebaseAuthRepository
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _passwordResetResult = LiveEvent<ResultState<Boolean>>()
    val passwordResetResult: LiveEvent<ResultState<Boolean>>
        get() = _passwordResetResult

    fun resetPassword(email: String) {
        viewModelScope.launch {
            repository.passwordResetEmail(email).collect { result ->
                _passwordResetResult.postValue(result)
            }
        }
    }
}