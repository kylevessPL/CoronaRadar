package pl.piasta.coronaradar.ui.register.viewmodel

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
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.register.model.RegisterForm
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerForm = RegisterForm()
    val registerForm: RegisterForm
        get() = _registerForm

    private val _progressIndicatorVisibility = MutableLiveData(false)
    val progressIndicatorVisibility: LiveData<Boolean>
        get() = _progressIndicatorVisibility

    private val _signIn = LiveEvent<Boolean>()
    val signIn: LiveData<Boolean>
        get() = _signIn

    private val _signUpResult = LiveEvent<ResultState<FirebaseUser>>()
    val signUpResult: LiveData<ResultState<FirebaseUser>>
        get() = _signUpResult

    fun setProgressIndicatorVisibility(visible: Boolean) {
        _progressIndicatorVisibility.value = visible
    }

    fun validateEmail() {
        viewModelScope.launch(coroutineDispatcher) {
            _registerForm.validateEmail()
        }
    }

    fun validatePassword() {
        viewModelScope.launch(coroutineDispatcher) {
            _registerForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch(coroutineDispatcher) {
            _registerForm.validatePasswordConfirm()
        }
    }

    fun signInEvent() {
        _signIn.value = true
    }

    fun signUp() {
        viewModelScope.launch(coroutineDispatcher) {
            _registerForm.isProcessing = true
            authRepository.register(
                _registerForm.input.email.get()!!,
                _registerForm.input.password.get()!!
            )
                .collect { result ->
                    _signUpResult.postValue(result)
                }
        }.invokeOnCompletion {
            _registerForm.isProcessing = false
        }
    }
}