package pl.piasta.coronaradar.ui.radar.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.chaquo.python.Python
import com.github.squti.androidwaverecorder.RecorderState.RECORDING
import com.github.squti.androidwaverecorder.WaveRecorder
import com.hadilq.liveevent.LiveEvent
import com.quickbirdstudios.surveykit.AnswerFormat.BooleanAnswerFormat.Result.PositiveAnswer
import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.FinishReason.Completed
import com.quickbirdstudios.surveykit.result.TaskResult
import com.quickbirdstudios.surveykit.result.question_results.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.data.common.*
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.model.HistoryDetails
import pl.piasta.coronaradar.data.history.repository.HistoryRepository
import pl.piasta.coronaradar.data.ml.repository.MlRepository
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.model.SurveyDetails
import pl.piasta.coronaradar.data.survey.repository.SurveyRepository
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.util.contentBytes
import pl.piasta.coronaradar.ui.util.recordingPath
import pl.piasta.coronaradar.util.*
import pl.piasta.coronaradar.util.ResultState.*
import splitties.resources.str
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import kotlin.math.pow
import kotlin.streams.toList

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository,
    private val mlRepository: MlRepository,
    private val historyRepository: HistoryRepository,
    private val surveyRepository: SurveyRepository,
    private val pythonInterpreter: Python,
    private val preferences: SharedPreferences
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
                _amplitude.postValue(it.toDouble().pow(1.5).toInt())
            }
        }
    }

    private val _currentOperationMessage = MutableLiveData(String.EMPTY)
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

    private val _classificationResultDialogDismiss = LiveEvent<Boolean>()
    val classificationResultDialogDismiss: LiveData<Boolean>
        get() = _classificationResultDialogDismiss

    private val _updateModelResult = LiveEvent<ResultState<Nothing>>()
    val updateModelResult: LiveData<ResultState<Nothing>>
        get() = _updateModelResult

    private val _classificationResult = LiveEvent<ResultState<Classification>>()
    val classificationResult: LiveData<ResultState<Classification>>
        get() = _classificationResult

    private val _saveUserHistoryResult = LiveEvent<ResultState<Nothing>>()
    val saveUserHistoryResult: LiveData<ResultState<Nothing>>
        get() = _saveUserHistoryResult

    private val _collectSurveyDataResult = LiveEvent<Boolean>()
    val collectSurveyDataResult: LiveData<Boolean>
        get() = _collectSurveyDataResult

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _recordingProgress = MutableLiveData(0)
    val recordingProgress: LiveData<Int>
        get() = _recordingProgress

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int>
        get() = _amplitude

    val onSurveyFinished = { result: TaskResult, reason: FinishReason ->
        collectSurveyData(result, reason)
    }

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

    fun classificationResultDialogDismissEvent() {
        _classificationResultDialogDismiss.value = true
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
            val modelPath = mlRepository.getLocalModel()!!
            val classification = classify(modelPath)
            _classificationResult.postValue(Success(classification))
        }
    }

    fun saveUserHistory(classification: Classification) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.currentUser ?: return@launch
            val history = with(classification) {
                History(
                    UUID.randomUUID(),
                    Instant.now(),
                    HistoryDetails(ResultLabel.valueOf(result.name), probability.toLong())
                )
            }
            historyRepository.createHistory(history).collect { result ->
                _saveUserHistoryResult.postValue(result)
            }
        }
    }

    private fun collectSurveyData(result: TaskResult, reason: FinishReason) {
        _collectSurveyDataResult.value = true
        (reason == Completed).ifTrue {
            viewModelScope.launch(Dispatchers.IO) {
                val covidTestResult = extractCovidTestResult(result)
                val details = createSurveyDetails(result)
                val survey = Survey(
                    UUID.randomUUID(),
                    Instant.now(),
                    details
                )
                val uploadCoughAudioTask = covidTestResult?.let {
                    surveyRepository.uploadCoughAudio(
                        it,
                        application.recordingPath.toUri().contentBytes(application)!!
                    )
                }
                val createSurveyTask = surveyRepository.createSurvey(survey)
                flowOf(uploadCoughAudioTask, createSurveyTask).filterNotNull().flattenMerge()
                    .collect()
            }
        }
    }

    private fun createSurveyDetails(result: TaskResult) = with(result.results) {
        val data = (_classificationResult.value as Success).data!!
        SurveyDetails(
            data.result,
            data.probability.toLong(),
            findLastResult<TextQuestionResult>(0)?.answer!!,
            findLastResult<TextQuestionResult>(1)?.answer!!.toLong(),
            findByLabel(findLastResult<ValuePickerQuestionResult>(2)?.answer!!)!!,
            findByLabel(findLastResult<ValuePickerQuestionResult>(3)?.answer!!)!!,
            findLastResult<MultipleChoiceQuestionResult>(4)?.answer?.map {
                findByLabel(it.value)!!
            } ?: emptyList(),
            findLastResult<BooleanQuestionResult>(5)!!.answer == PositiveAnswer,
            findLastResult<BooleanQuestionResult>(6)!!.answer == PositiveAnswer,
            findLastResult<BooleanQuestionResult>(7)!!.answer == PositiveAnswer,
            findLastResult<BooleanQuestionResult>(8)!!.answer == PositiveAnswer,
            findLastResult<MultipleChoiceQuestionResult>(9)?.answer?.map {
                findByLabel(it.value)!!
            } ?: emptyList(),
            findLastResult<ScaleQuestionResult>(12)?.answer!!.toLong()
        )
    }

    private fun extractCovidTestResult(result: TaskResult): ResultLabel? = result.results.run {
        return findLastResult<BooleanQuestionResult>(10)?.answer.takeIf { it == PositiveAnswer }
            ?.let {
                findByLabel(findLastResult<ValuePickerQuestionResult>(11)?.answer!!)!!
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
            mlRepository.downloadModel(lastUpdate != Instant.EPOCH && updateWifiOnly)
                .collect { result ->
                    _updateModelResult.postValue(result)
                    (result is Error && it == Instant.EPOCH).ifTrue {
                        throw CancellationException()
                    }
                }
        }
    }

    private fun classify(modelPath: Uri): Classification {
        val script = pythonInterpreter.getModule("spectrogram")
        val result =
            script.callAttr("generate", application.recordingPath.absolutePath, modelPath.path)
                .use { it.toJava(Float::class.java) }
        val labels = application.assets.open("labels.txt").use {
            BufferedReader(InputStreamReader(it)).lines().toList()
        }
        return Classification(
            ResultLabel.valueOf(labels[(result >= 0.5).toInt()]),
            result.percent()
        )
    }

    private fun modelUpdateRequired(lastUpdate: Instant, frequency: Long) =
        frequency.takeIf { it != -1L }?.let {
            !lastUpdate.plus(frequency, ChronoUnit.HOURS)
                .isAfter(Instant.now())
        } ?: false
}