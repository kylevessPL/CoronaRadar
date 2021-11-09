package pl.piasta.coronaradar.ui.radar.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.squti.androidwaverecorder.RecorderState.RECORDING
import com.github.squti.androidwaverecorder.WaveRecorder
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.ml.repository.MlRepository
import pl.piasta.coronaradar.ui.util.recordingPath
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.divideToPercent
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val repository: MlRepository,
    private val application: Application,
    private val preferences: SharedPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recorder: WaveRecorder by lazy {
        WaveRecorder(application.recordingPath().absolutePath).apply {
            waveConfig.channels = CHANNEL_IN_MONO
            noiseSuppressorActive = true
            onTimeElapsed = {
                _recordingProgress.postValue(it.toInt().plus(1).divideToPercent(20))
                (it == 20L).ifTrue { stopRecording() }
            }
            onStateChangeListener = {
                Log.d(TAG, "recorder:onStateChanged ".plus(it.name))
                _isRecording.postValue(it == RECORDING)
                (it != RECORDING).ifTrue { _recordingProgress.postValue(0) }
            }
            onAmplitudeListener = {
                _amplitude.postValue(it.toDouble().pow(1.6).toInt())
            }
        }
    }

    private val _requestPermissions = LiveEvent<Boolean>()
    val requestPermissions: LiveData<Boolean>
        get() = _requestPermissions

    private val _updateModelResult = LiveEvent<ResultState<Nothing>>()
    val updateModelResult: LiveData<ResultState<Nothing>>
        get() = _updateModelResult

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _recordingProgress = MutableLiveData(0)
    val recordingProgress: LiveData<Int>
        get() = _recordingProgress

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int>
        get() = _amplitude

    fun requestPermissionsEvent() {
        _requestPermissions.value = true
    }

    fun updateModel() {
        with(preferences) {
            getLong(
                application.str(R.string.coronakit_last_update_key),
                Instant.EPOCH.epochSecond
            ).takeIf { lastUpdate ->
                lastUpdate == Instant.EPOCH.epochSecond || getString(
                    application.str(R.string.coronakit_update_frequency_key),
                    "24"
                )!!.toLong().takeIf { it != -1L }?.let {
                    !Instant.ofEpochSecond(lastUpdate).plus(it, ChronoUnit.HOURS)
                        .isAfter(Instant.now())
                } ?: false
            }?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.downloadModel(
                        getBoolean(
                            application.str(R.string.coronakit_update_wifi_only),
                            false
                        )
                    )
                        .collect { result ->
                            _updateModelResult.postValue(result)
                        }
                }
            }
        }
    }

    fun updateModelUpdatePreferences() = preferences.edit().apply {
        putLong(application.str(R.string.coronakit_last_update_key), Instant.now().epochSecond)
    }.apply()

    fun recordStart() = recorder.startRecording()

    fun recordStop() = recorder.stopRecording()
}