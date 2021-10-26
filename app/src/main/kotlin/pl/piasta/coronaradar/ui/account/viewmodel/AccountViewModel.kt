package pl.piasta.coronaradar.ui.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val displayNameEnabled = MutableLiveData(false)
    val passwordEnabled = MutableLiveData(false)

    private val _progressIndicationVisibility = MutableLiveData(false)
    val progressIndicationVisibility: LiveData<Boolean>
        get() = _progressIndicationVisibility

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = visible
    }

    private val _signOut = LiveEvent<Boolean>()
    val signOut: LiveData<Boolean>
        get() = _signOut

    fun toggleDisplayName() {
        displayNameEnabled.postValue(!displayNameEnabled.value!!)
    }

    fun togglePassword() {
        passwordEnabled.postValue(!passwordEnabled.value!!)
    }

    fun signOutEvent() {
        _signOut.value = true
    }
}