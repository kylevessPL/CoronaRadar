package pl.piasta.coronaradar.ui.user.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.common.FirebaseUserLiveData
import pl.piasta.coronaradar.util.ResultState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private var _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    private val _progressIndicationVisibility = MutableLiveData<Int>()
    val progressIndicationVisibility: LiveData<Int>
        get() = _progressIndicationVisibility

    private val _verifyEmailResult = LiveEvent<ResultState<Nothing>>()
    val verifyEmailResult: LiveEvent<ResultState<Nothing>>
        get() = _verifyEmailResult

    private val _verifyActionCodeResult = LiveEvent<ResultState<ActionCode>>()
    val verifyActionCodeResult: LiveEvent<ResultState<ActionCode>>
        get() = _verifyActionCodeResult

    fun setProgressIndicationVisibility(visible: Boolean) {
        _progressIndicationVisibility.value = when (visible) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    fun verifyActionCode(task: Task<PendingDynamicLinkData>) {
        viewModelScope.launch {
            repository.verifyActionCode(task).collect { result ->
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