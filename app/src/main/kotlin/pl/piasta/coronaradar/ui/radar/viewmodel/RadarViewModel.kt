package pl.piasta.coronaradar.ui.radar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val _requestPermissions = LiveEvent<Boolean>()
    val requestPermissions: LiveData<Boolean>
        get() = _requestPermissions

    private val _isRecording = MutableLiveData(false)
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int>
        get() = _amplitude

    fun requestPermissionsEvent() {
        _requestPermissions.value = true
    }

    fun recordStart() {
        _isRecording.postValue(true)
    }

    fun recordStop() {
        _isRecording.postValue(false)
    }
}