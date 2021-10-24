package pl.piasta.coronaradar.ui.user.viewmodel

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
class VerificationEmailViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _verificationEmailResult = LiveEvent<ResultState<Nothing>>()
    val verificationEmailResult: LiveEvent<ResultState<Nothing>>
        get() = _verificationEmailResult

    fun sendVerificationEmail() {
        viewModelScope.launch {
            repository.sendVerificationEmail().collect { result ->
                _verificationEmailResult.postValue(result)
            }
        }
    }
}