package pl.piasta.coronaradar.ui.user.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.common.viewmodel.ConnectivityLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.FirebaseUserLiveData
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _connectivity = ConnectivityLiveData(application)
    val connectivity: LiveData<Boolean>
        get() = _connectivity

    private val _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    private val _progressIndicationVisibility = MutableLiveData(false)
    val progressIndicationVisibility: LiveData<Boolean>
        get() = _progressIndicationVisibility

    private val _verificationEmailResult = LiveEvent<ResultState<Nothing>>()
    val verificationEmailResult: LiveEvent<ResultState<Nothing>>
        get() = _verificationEmailResult

    private val _verifyEmailResult = LiveEvent<ResultState<Nothing>>()
    val verifyEmailResult: LiveData<ResultState<Nothing>>
        get() = _verifyEmailResult

    private val _passwordResetEmailResult = LiveEvent<ResultState<Boolean>>()
    val passwordResetEmailResult: LiveEvent<ResultState<Boolean>>
        get() = _passwordResetEmailResult

    private val _passwordResetResult = LiveEvent<ResultState<Boolean>>()
    val passwordResetResult: LiveEvent<ResultState<Boolean>>
        get() = _passwordResetResult

    private val _verifyActionCodeResult = LiveEvent<ResultState<ActionCode?>>()
    val verifyActionCodeResult: LiveData<ResultState<ActionCode?>>
        get() = _verifyActionCodeResult

    private val _signOutResult = LiveEvent<ResultState<Nothing>>()
    val signOutResult: LiveData<ResultState<Nothing>>
        get() = _signOutResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = visible
    }

    fun verifyActionCode(data: Uri) {
        viewModelScope.launch {
            authRepository.verifyActionCode(data).collect { result ->
                _verifyActionCodeResult.postValue(result)
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            authRepository.sendVerificationEmail().collect { result ->
                _verificationEmailResult.postValue(result)
            }
        }
    }

    fun verifyEmail(oob: String) = viewModelScope.launch {
        authRepository.verifyEmail(oob).collect { result ->
            _verifyEmailResult.postValue(result)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(email).collect { result ->
                _passwordResetEmailResult.postValue(result)
            }
        }
    }

    fun resetPassword(oob: String, newPassword: String) {
        viewModelScope.launch {
            authRepository.resetPassword(oob, newPassword).collect { result ->
                _passwordResetResult.postValue(result)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
                .collect { result ->
                    _signOutResult.postValue(result)
                }
        }
    }
}