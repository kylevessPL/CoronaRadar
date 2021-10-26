package pl.piasta.coronaradar.ui.user.viewmodel

import androidx.lifecycle.LiveData
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
class SignOutViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _signOutResult = LiveEvent<ResultState<Nothing>>()
    val signOutResult: LiveData<ResultState<Nothing>>
        get() = _signOutResult

    fun signOut() {
        viewModelScope.launch {
            repository.logout()
                .collect { result ->
                    _signOutResult.postValue(result)
                }
        }
    }
}