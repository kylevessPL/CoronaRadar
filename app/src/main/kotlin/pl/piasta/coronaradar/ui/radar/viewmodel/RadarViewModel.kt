package pl.piasta.coronaradar.ui.radar.viewmodel

import android.app.Application
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.squti.androidwaverecorder.RecorderState.RECORDING
import com.github.squti.androidwaverecorder.WaveRecorder
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.util.recordingPath
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class RadarViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recorder: WaveRecorder =
        WaveRecorder(application.recordingPath().absolutePath).apply {
            waveConfig.channels = CHANNEL_IN_MONO
            noiseSuppressorActive = true
            onStateChangeListener = {
                Log.d(TAG, "recorder:onStateChanged ".plus(it.name))
                _isRecording.postValue(it == RECORDING)
            }
            onAmplitudeListener = {
                _amplitude.postValue(it.toDouble().pow(1.6).toInt())
            }
        }

    private val _requestPermissions = LiveEvent<Boolean>()
    val requestPermissions: LiveData<Boolean>
        get() = _requestPermissions

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int>
        get() = _amplitude

    fun requestPermissionsEvent() {
        _requestPermissions.value = true
    }

    fun recordStart() = recorder.startRecording()

    fun recordStop() = recorder.stopRecording()
}