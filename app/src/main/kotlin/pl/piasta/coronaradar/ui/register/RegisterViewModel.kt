package pl.piasta.coronaradar.ui.register

import android.view.View
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

    private val _progressIndicationVisibility = MutableLiveData<Int>()
    val progressIndicationVisibility: LiveData<Int>
        get() = _progressIndicationVisibility

    private val _signIn = LiveEvent<Boolean>()
    val signIn: LiveEvent<Boolean>
        get() = _signIn

    private val _signUpResult = LiveEvent<ResultState<FirebaseUser>>()
    val signUpResult: LiveEvent<ResultState<FirebaseUser>>
        get() = _signUpResult

    private val _verificationResult = LiveEvent<ResultState<Nothing>>()
    val verificationResult: LiveEvent<ResultState<Nothing>>
        get() = _verificationResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = when (visible) {
            true -> View.VISIBLE
            false -> View.GONE
        }
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

    fun validateRepeatPassword() {
        viewModelScope.launch {
            _registerForm.validateRepeatPassword()
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

    fun verify() {
        viewModelScope.launch {
            repository.verificationEmail().collect { result ->
                _verificationResult.postValue(result)
            }
        }
    }
}