package pl.piasta.coronaradar.ui.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.ui.account.model.UserDetailsForm
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userDetailsForm = UserDetailsForm()
    val userDetailsForm: UserDetailsForm
        get() = _userDetailsForm

    private val _displayNameEnabled = MutableLiveData(false)
    val displayNameEnabled: LiveData<Boolean>
        get() = _displayNameEnabled

    private val _passwordEnabled = MutableLiveData(false)
    val passwordEnabled: LiveData<Boolean>
        get() = _passwordEnabled

    private val _progressIndicationVisibility = MutableLiveData(false)
    val progressIndicationVisibility: LiveData<Boolean>
        get() = _progressIndicationVisibility

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = visible
    }

    fun validatePassword() {
        viewModelScope.launch {
            _userDetailsForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch {
            _userDetailsForm.validatePasswordConfirm()
        }
    }

    private val _signOut = LiveEvent<Boolean>()
    val signOut: LiveData<Boolean>
        get() = _signOut

    fun toggleDisplayName() {
        _displayNameEnabled.postValue(!_displayNameEnabled.value!!)
    }

    fun togglePassword() {
        _passwordEnabled.postValue(!_passwordEnabled.value!!)
    }

    fun signOutEvent() {
        _signOut.value = true
    }

    fun updateProfile() {
        viewModelScope.launch {
            // repository.register(_registerForm.input.email!!, _registerForm.input.password!!)
            //     .collect { result ->
            //         _signUpResult.postValue(result)
            //     }
        }
    }
}