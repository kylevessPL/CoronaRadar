package pl.piasta.coronaradar.ui.account.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.account.model.UserDetailsForm
import pl.piasta.coronaradar.ui.util.contentBytes
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ifTrue
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val application: Application,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userDetailsForm = UserDetailsForm(authRepository.currentUser)
    val userDetailsForm: UserDetailsForm
        get() = _userDetailsForm

    private val _displayNameEnabled = MutableLiveData(false)
    val displayNameEnabled: LiveData<Boolean>
        get() = _displayNameEnabled

    private val _passwordEnabled = MutableLiveData(false)
    val passwordEnabled: LiveData<Boolean>
        get() = _passwordEnabled

    private val _signOut = LiveEvent<Boolean>()
    val signOut: LiveData<Boolean>
        get() = _signOut

    private val _chooseAvatar = LiveEvent<Boolean>()
    val chooseAvatar: LiveData<Boolean>
        get() = _chooseAvatar

    private val _uploadUserAvatarResult = LiveEvent<ResultState<Uri>>()
    val uploadUserAvatarResult: LiveData<ResultState<Uri>>
        get() = _uploadUserAvatarResult

    private val _updateUserProfileResult = LiveEvent<ResultState<Nothing>>()
    val updateUserProfileResult: LiveData<ResultState<Nothing>>
        get() = _updateUserProfileResult

    private val _progressIndicatorVisibility = MutableLiveData(false)
    val progressIndicatorVisibility: LiveData<Boolean>
        get() = _progressIndicatorVisibility

    fun setProgressIndicatorVisibility(visible: Boolean) {
        _progressIndicatorVisibility.value = visible
    }

    fun toggleDisplayName() {
        (!_userDetailsForm.isProcessing).ifTrue {
            _displayNameEnabled.postValue(!_displayNameEnabled.value!!)
        }
    }

    fun togglePassword() {
        (!_userDetailsForm.isProcessing).ifTrue {
            _passwordEnabled.postValue(!_passwordEnabled.value!!)
        }
    }

    fun chooseAvatarEvent() {
        _chooseAvatar.value = true
    }

    fun signOutEvent() {
        _signOut.value = true
    }

    fun validatePassword() {
        viewModelScope.launch(coroutineDispatcher) {
            _userDetailsForm.validatePassword()
        }
    }

    fun validatePasswordConfirm() {
        viewModelScope.launch(coroutineDispatcher) {
            _userDetailsForm.validatePasswordConfirm()
        }
    }

    fun setUserAvatar(uri: Uri) = _userDetailsForm.input.avatar.set(uri)

    fun updateProfile() {
        viewModelScope.launch(coroutineDispatcher) {
            _userDetailsForm.isProcessing = true
            disableFields()
            uploadUserAvatar()
            updateUserDetails()
        }.invokeOnCompletion {
            _userDetailsForm.isProcessing = false
        }
    }

    private suspend fun uploadUserAvatar() {
        val uploadAvatarTask = _userDetailsForm.avatarChosen().takeIf { it }
            ?.let {
                authRepository.uploadAvatar(
                    _userDetailsForm.input.avatar.get()!!.contentBytes(application)!!
                )
            }
        uploadAvatarTask?.let {
            it.collect { result ->
                _uploadUserAvatarResult.postValue(result)
                when (result) {
                    is ResultState.Success -> userDetailsForm.input.avatar.set(result.data)
                    is ResultState.Error -> throw CancellationException()
                    else -> {
                    }
                }
            }
        }
    }

    private suspend fun updateUserDetails() {
        val updatePasswordTask = _userDetailsForm.passwordFilled().takeIf { it }
            ?.let { authRepository.updateCurrentUserPassword(_userDetailsForm.input.password.get()!!) }
        val updateUserDetailsTask =
            (_userDetailsForm.avatarChosen() || _userDetailsForm.displayNameFilled()).takeIf { it }
                ?.let {
                    authRepository.updateCurrentUserDetails(
                        _userDetailsForm.input.displayName.get()!!,
                        _userDetailsForm.input.avatar.get()
                    )
                }
        val task = updatePasswordTask?.let {
            updateUserDetailsTask?.zip(it) { _, t2 -> t2 } ?: it
        } ?: updateUserDetailsTask
        task?.collect { result ->
            _updateUserProfileResult.postValue(result)
        }
        _userDetailsForm.clear()
        _displayNameEnabled.postValue(false)
        _passwordEnabled.postValue(false)
    }

    private fun disableFields() {
        _displayNameEnabled.postValue(false)
        _passwordEnabled.postValue(false)
    }
}