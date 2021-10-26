package pl.piasta.coronaradar.ui.user.viewmodel

import android.app.Application
import android.net.Uri
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
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.common.viewmodel.ConnectivityLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.FirebaseUserLiveData
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private var _connectivity = ConnectivityLiveData(application)
    val connectivity: LiveData<Boolean>
        get() = _connectivity

    private var _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    private val _progressIndicationVisibility = MutableLiveData(false)
    val progressIndicationVisibility: LiveData<Boolean>
        get() = _progressIndicationVisibility

    private val _verifyEmailResult = LiveEvent<ResultState<Nothing>>()
    val verifyEmailResult: LiveData<ResultState<Nothing>>
        get() = _verifyEmailResult

    private val _verifyActionCodeResult = LiveEvent<ResultState<ActionCode?>>()
    val verifyActionCodeResult: LiveData<ResultState<ActionCode?>>
        get() = _verifyActionCodeResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = visible
    }

    fun verifyActionCode(data: Uri) {
        viewModelScope.launch {
            repository.verifyActionCode(data).collect { result ->
                _verifyActionCodeResult.postValue(result)
            }
        }
    }

    fun verifyEmail(oob: String) {
        viewModelScope.launch {
            repository.verifyEmail(oob).collect { result ->
                _verifyEmailResult.postValue(result)
            }
        }
    }
}