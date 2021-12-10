package pl.piasta.coronaradar.ui.user.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.common.viewmodel.ConnectivityLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.FirebaseUserLiveData
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    application: Application,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _connectivity = ConnectivityLiveData(application)
    val connectivity: LiveData<Boolean>
        get() = _connectivity

    private val _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    private val _progressIndicatorVisibility = MutableLiveData(false)
    val progressIndicatorVisibility: LiveData<Boolean>
        get() = _progressIndicatorVisibility

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

    fun setProgressIndicatorVisibility(visible: Boolean) {
        _progressIndicatorVisibility.value = visible
    }

    fun verifyActionCode(data: Uri) {
        viewModelScope.launch(coroutineDispatcher) {
            authRepository.verifyActionCode(data).collect { result ->
                _verifyActionCodeResult.postValue(result)
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(coroutineDispatcher) {
            authRepository.sendVerificationEmail().collect { result ->
                _verificationEmailResult.postValue(result)
            }
        }
    }

    fun verifyEmail(oob: String) = viewModelScope.launch(coroutineDispatcher) {
        authRepository.verifyEmail(oob).collect { result ->
            _verifyEmailResult.postValue(result)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(coroutineDispatcher) {
            authRepository.sendPasswordResetEmail(email).collect { result ->
                _passwordResetEmailResult.postValue(result)
            }
        }
    }

    fun resetPassword(oob: String, newPassword: String) {
        viewModelScope.launch(coroutineDispatcher) {
            authRepository.resetPassword(oob, newPassword).collect { result ->
                _passwordResetResult.postValue(result)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(coroutineDispatcher) {
            authRepository.logout()
                .collect { result ->
                    _signOutResult.postValue(result)
                }
        }
    }
}