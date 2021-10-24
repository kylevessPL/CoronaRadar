package pl.piasta.coronaradar.ui.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.common.FirebaseUserLiveData
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    val displayNameEnabled = MutableLiveData<Boolean>()
    val passwordEnabled = MutableLiveData<Boolean>()

    private var _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    fun toggleDisplayName() {
        displayNameEnabled.postValue(!displayNameEnabled.value!!)
    }

    fun togglePassword() {
        passwordEnabled.postValue(!passwordEnabled.value!!)
    }
}