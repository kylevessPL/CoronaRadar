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
class VerificationViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _verificationResult = LiveEvent<ResultState<Nothing>>()
    val verificationResult: LiveEvent<ResultState<Nothing>>
        get() = _verificationResult

    fun verify() {
        viewModelScope.launch {
            repository.verificationEmail().collect { result ->
                _verificationResult.postValue(result)
            }
        }
    }
}