package pl.piasta.coronaradar.ui.stats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.data.survey.model.Statistics
import pl.piasta.coronaradar.data.survey.repository.SurveyRepository
import javax.inject.Inject

@HiltViewModel
class StatsChartViewModel @Inject constructor(surveyRepository: SurveyRepository) : ViewModel() {

    private val _statisticsData = surveyRepository.watchStatistics().asLiveData()
    val statisticsData: LiveData<Statistics>
        get() = _statisticsData

    private val _dialogDismiss = LiveEvent<Boolean>()
    val dialogDismiss: LiveData<Boolean>
        get() = _dialogDismiss

    private val _currentChartDisplayed = LiveEvent<Int>()
    val currentChartDisplayed: LiveData<Int>
        get() = _currentChartDisplayed

    val chartDisplayedEvent = fun(position: Int) {
        _currentChartDisplayed.value = position
    }

    fun dialogDismissEvent() {
        _dialogDismiss.value = true
    }
}