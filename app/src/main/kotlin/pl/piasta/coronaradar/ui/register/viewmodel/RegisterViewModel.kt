package pl.piasta.coronaradar.ui.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.register.model.RegisterForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _registerForm = RegisterForm()
    val registerForm: RegisterForm
        get() = _registerForm

    private val _progressIndicationVisibility = MutableLiveData(false)
    val progressIndicationVisibility: LiveData<Boolean>
        get() = _progressIndicationVisibility

    private val _signIn = LiveEvent<Boolean>()
    val signIn: LiveData<Boolean>
        get() = _signIn

    private val _signUpResult = LiveEvent<ResultState<FirebaseUser>>()
    val signUpResult: LiveData<ResultState<FirebaseUser>>
        get() = _signUpResult

    private val _verificationEmailResult = LiveEvent<ResultState<Nothing>>()
    val verificationEmailResult: LiveData<ResultState<Nothing>>
        get() = _verificationEmailResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = visible
    }

    fun validateEmail() {
        viewModelScope.launch {
            _registerForm.validateEmail()
        }
    }

    fun validatePassword() {
        viewModelScope.launch {
            _registerForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch {
            _registerForm.validatePasswordConfirm()
        }
    }

    fun signInEvent() {
        _signIn.value = true
    }

    fun signUp() {
        viewModelScope.launch {
            repository.register(_registerForm.input.email!!, _registerForm.input.password!!)
                .collect { result ->
                    _signUpResult.postValue(result)
                }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            repository.sendVerificationEmail().collect { result ->
                _verificationEmailResult.postValue(result)
            }
        }
    }
}