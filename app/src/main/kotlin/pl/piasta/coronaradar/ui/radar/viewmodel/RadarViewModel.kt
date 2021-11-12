package pl.piasta.coronaradar.ui.radar.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.github.squti.androidwaverecorder.RecorderState.RECORDING
import com.github.squti.androidwaverecorder.WaveRecorder
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.ml.repository.MlRepository
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.model.ClassificationResult
import pl.piasta.coronaradar.ui.radar.model.ClassificationResult.NEGATIVE
import pl.piasta.coronaradar.ui.radar.model.ClassificationResult.POSITIVE
import pl.piasta.coronaradar.ui.util.recordingPath
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.divideToPercent
import pl.piasta.coronaradar.util.ifTrue
import pl.piasta.coronaradar.util.toInt
import splitties.resources.str
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.streams.toList

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val repository: MlRepository,
    private val application: Application,
    private val pythonInterpreter: Python,
    private val preferences: SharedPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recorder: WaveRecorder by lazy {
        WaveRecorder(application.recordingPath.absolutePath).apply {
            waveConfig.channels = CHANNEL_IN_MONO
            noiseSuppressorActive = true
            onTimeElapsed = {
                _recordingProgress.postValue(it.toInt().plus(1).divideToPercent(20))
                (it == 20L).ifTrue { analyzeData() }
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

    private val _currentOperationMessage = MutableLiveData("")
    val currentOperationMessage: LiveData<String>
        get() = _currentOperationMessage

    private val _processingVisibility = MutableLiveData(false)
    val processingVisibility: LiveData<Boolean>
        get() = _processingVisibility

    private val _modelUpdateVisibility = MutableLiveData(false)
    val modelUpdateVisibility: LiveData<Boolean>
        get() = _modelUpdateVisibility

    private val _requestPermissions = LiveEvent<Boolean>()
    val requestPermissions: LiveData<Boolean>
        get() = _requestPermissions

    private val _updateModelResult = LiveEvent<ResultState<Nothing>>()
    val updateModelResult: LiveData<ResultState<Nothing>>
        get() = _updateModelResult

    private val _classificationResult = LiveEvent<ResultState<Classification>>()
    val classificationResult: LiveData<ResultState<Classification>>
        get() = _classificationResult

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _recordingProgress = MutableLiveData(0)
    val recordingProgress: LiveData<Int>
        get() = _recordingProgress

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int>
        get() = _amplitude

    fun setCurrentOperationMessage(message: String) {
        _currentOperationMessage.value = message
    }

    fun setProcessingVisibility(visible: Boolean) {
        _processingVisibility.value = visible
    }

    fun setModelUpdateVisibility(visible: Boolean) {
        _modelUpdateVisibility.value = visible
    }

    fun requestPermissionsEvent() {
        _requestPermissions.value = true
    }

    fun updateModelUpdatePreferences() = preferences.edit().apply {
        putLong(application.str(R.string.coronakit_last_update_key), Instant.now().epochSecond)
    }.apply()

    fun recordData() {
        viewModelScope.launch(Dispatchers.IO) {
            updateModel()
            recorder.startRecording()
        }
    }

    fun analyzeData() {
        viewModelScope.launch(Dispatchers.IO) {
            recorder.stopRecording()
            _classificationResult.postValue(Loading)
            val modelPath = repository.getLocalModel()!!
            val classification = classify(modelPath)
            _classificationResult.postValue(Success(classification))
        }
    }

    private suspend fun updateModel() = with(preferences) {
        val lastUpdate = Instant.ofEpochSecond(
            getLong(
                application.str(R.string.coronakit_last_update_key),
                Instant.EPOCH.epochSecond
            )
        )
        val updateFrequency = getString(
            application.str(R.string.coronakit_update_frequency_key),
            "24"
        )!!.toLong()
        val updateWifiOnly = getBoolean(
            application.str(R.string.coronakit_update_wifi_only),
            false
        )
        lastUpdate.takeIf { it == Instant.EPOCH || modelUpdateRequired(it, updateFrequency) }?.let {
            repository.downloadModel(lastUpdate != Instant.EPOCH && updateWifiOnly)
                .collect { result ->
                    _updateModelResult.postValue(result)
                    (result is Error).ifTrue { coroutineContext.cancel() }
                }
        }
    }

    private fun classify(modelPath: Uri): Classification {
        val script = pythonInterpreter.getModule("spectrogram")
        val result =
            script.callAttr("generate", application.recordingPath.absolutePath, modelPath.path)
                .use { it.toJava(Float::class.java) }
        val labels =
            BufferedReader(InputStreamReader(application.assets.open("labels.txt"))).lines()
                .toList()
        val classification = ClassificationResult.valueOf(labels[(result >= 0.5).toInt()])
        val confidence = when (classification) {
            POSITIVE -> result.roundToInt()
            NEGATIVE -> 1 - result.roundToInt()
        }
        return Classification(classification, confidence)
    }

    private fun modelUpdateRequired(lastUpdate: Instant, frequency: Long) =
        frequency.takeIf { it != -1L }?.let {
            !lastUpdate.plus(frequency, ChronoUnit.HOURS)
                .isAfter(Instant.now())
        } ?: false
}