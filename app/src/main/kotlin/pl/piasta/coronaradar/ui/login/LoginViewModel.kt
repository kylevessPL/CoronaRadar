package pl.piasta.coronaradar.ui.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.login.model.LoginForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loginForm = LoginForm()
    val loginForm: LoginForm
        get() = _loginForm

    private val _progressIndicationVisibility = MutableLiveData<Int>()
    val progressIndicationVisibility: LiveData<Int>
        get() = _progressIndicationVisibility

    private val _signUp = LiveEvent<Boolean>()
    val signUp: LiveEvent<Boolean>
        get() = _signUp

    private val _signInWithGoogle = LiveEvent<Boolean>()
    val signInWithGoogle: LiveEvent<Boolean>
        get() = _signInWithGoogle

    private val _signInWithFacebook = LiveEvent<Boolean>()
    val signInWithFacebook: LiveEvent<Boolean>
        get() = _signInWithFacebook

    private val _resetPassword = LiveEvent<Boolean>()
    val resetPassword: LiveEvent<Boolean>
        get() = _resetPassword

    private val _signInResult = LiveEvent<ResultState<FirebaseUser>>()
    val signInResult: LiveEvent<ResultState<FirebaseUser>>
        get() = _signInResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = when (visible) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    fun validateEmail() {
        viewModelScope.launch {
            _loginForm.validateEmail()
        }
    }

    fun validatePassword() {
        viewModelScope.launch {
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
        viewModelScope.launch {
            repository.login(_loginForm.input.email!!, _loginForm.input.password!!)
                .collect { result ->
                    _signInResult.postValue(result)
                }
        }
    }

    fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            repository.googleLogin(task).collect { result ->
                _signInResult.postValue(result)
            }
        }
    }

    fun signInWithFacebook(callbackManager: CallbackManager) {
        viewModelScope.launch {
            repository.facebookLogin(callbackManager).collect { result ->
                _signInResult.postValue(result)
            }
        }
    }
}