package pl.piasta.coronaradar.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.common.viewmodel.ConnectivityLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.FirebaseUserLiveData
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _connectivity = ConnectivityLiveData(application)
    val connectivity: LiveData<Boolean>
        get() = _connectivity

    private var _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser
}