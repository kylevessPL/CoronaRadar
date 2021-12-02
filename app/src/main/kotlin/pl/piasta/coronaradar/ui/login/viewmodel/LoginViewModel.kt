package pl.piasta.coronaradar.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.login.model.LoginForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginForm = LoginForm()
    val loginForm: LoginForm
        get() = _loginForm

    private val _progressIndicatorVisibility = MutableLiveData(false)
    val progressIndicatorVisibility: LiveData<Boolean>
        get() = _progressIndicatorVisibility

    private val _signUp = LiveEvent<Boolean>()
    val signUp: LiveData<Boolean>
        get() = _signUp

    private val _signInWithGoogle = LiveEvent<Boolean>()
    val signInWithGoogle: LiveData<Boolean>
        get() = _signInWithGoogle

    private val _signInWithFacebook = LiveEvent<Boolean>()
    val signInWithFacebook: LiveData<Boolean>
        get() = _signInWithFacebook

    private val _resetPassword = LiveEvent<Boolean>()
    val resetPassword: LiveData<Boolean>
        get() = _resetPassword

    private val _signInResult = LiveEvent<ResultState<FirebaseUser>>()
    val signInResult: LiveData<ResultState<FirebaseUser>>
        get() = _signInResult

    fun setProgressIndicatorVisibility(visible: Boolean) {
        _progressIndicatorVisibility.value = visible
    }

    fun validateEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginForm.validateEmail()
        }
    }

    fun validatePassword() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginForm.validatePassword()
        }
    }

    fun signUpEvent() {
        _signUp.value = true
    }

    fun signInWithGoogleEvent() {
        _signInWithGoogle.value = true
    }

    fun signInWithFacebookEvent() {
        _signInWithFacebook.value = true
    }

    fun resetPasswordEvent() {
        _resetPassword.value = true
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginForm.isProcessing = true
            authRepository.login(_loginForm.input.email.get()!!, _loginForm.input.password.get()!!)
                .collect { result ->
                    _signInResult.postValue(result)
                }
        }.invokeOnCompletion {
            _loginForm.isProcessing = false
        }
    }

    fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginForm.isProcessing = true
            authRepository.loginWithGoogle(task).collect { result ->
                _signInResult.postValue(result)
            }
        }.invokeOnCompletion {
            _loginForm.isProcessing = false
        }
    }

    fun signInWithFacebook(callbackManager: CallbackManager) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginForm.isProcessing = true
            authRepository.loginWithFacebook(callbackManager).collect { result ->
                _signInResult.postValue(result)
            }
        }.invokeOnCompletion {
            _loginForm.isProcessing = false
        }
    }
}