package pl.piasta.coronaradar.ui.main.viewmodel

import android.app.Application
import android.content.Intent
import android.content.Intent.ACTION_TIMEZONE_CHANGED
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.ui.common.viewmodel.ConnectivityLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.FirebaseUserLiveData
import pl.piasta.coronaradar.ui.common.viewmodel.LiveBroadcast
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    application: Application
) : ViewModel() {

    private val _timeZone = LiveBroadcast(application, listOf(ACTION_TIMEZONE_CHANGED))
    val timeZone: LiveData<Intent>
        get() = _timeZone

    private val _connectivity = ConnectivityLiveData(application)
    val connectivity: LiveData<Boolean>
        get() = _connectivity

    private val _firebaseUser = FirebaseUserLiveData()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser
}