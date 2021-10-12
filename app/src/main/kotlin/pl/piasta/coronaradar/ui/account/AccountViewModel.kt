package pl.piasta.coronaradar.ui.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    BaseViewModel() {

    val displayNameEnabled = MutableLiveData<Boolean>().apply { value = false }
    val passwordEnabled = MutableLiveData<Boolean>().apply { value = false }

    fun toggleDisplayName() {
        displayNameEnabled.postValue(!displayNameEnabled.value!!)
    }

    fun togglePassword() {
        passwordEnabled.postValue(!passwordEnabled.value!!)
    }
}